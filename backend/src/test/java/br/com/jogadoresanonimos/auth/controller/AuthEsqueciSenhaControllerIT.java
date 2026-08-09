package br.com.jogadoresanonimos.auth.controller;

import br.com.jogadoresanonimos.auth.repository.AdminResetCodigoRepository;
import br.com.jogadoresanonimos.auth.repository.PasswordResetTokenRepository;
import br.com.jogadoresanonimos.usuario.entity.Role;
import br.com.jogadoresanonimos.usuario.entity.Usuario;
import br.com.jogadoresanonimos.usuario.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("qa-integration")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class AuthEsqueciSenhaControllerIT {

    private static final String MENSAGEM_GENERICA =
            "Se o apelido ou e-mail existir em nossa base, enviaremos um link de redefinição.";

    @LocalServerPort
    int port;

    @Autowired
    UsuarioRepository usuarioRepository;

    @Autowired
    PasswordResetTokenRepository passwordResetTokenRepository;

    @Autowired
    AdminResetCodigoRepository adminResetCodigoRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    private final RestTemplate restTemplate = new RestTemplate();

    private String esqueciSenhaUrl() {
        return "http://localhost:" + port + "/api/v1/auth/esqueci-senha";
    }

    private ResponseEntity<String> esqueciSenha(String corpoJson) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        try {
            return restTemplate.postForEntity(esqueciSenhaUrl(), new HttpEntity<>(corpoJson, headers), String.class);
        } catch (HttpStatusCodeException ex) {
            return ResponseEntity.status(ex.getStatusCode()).body(ex.getResponseBodyAsString());
        }
    }

    private String bodyEsqueciSenha(String identificador) {
        return "{\"identificador\":\"" + identificador + "\"}";
    }

    private Usuario criarUsuario(String apelido, String email, Role role) {
        Usuario usuario = new Usuario(apelido, email, passwordEncoder.encode("senhaAtual123"), role);
        return usuarioRepository.saveAndFlush(usuario);
    }

    @Test
    void deveRetornarMensagemGenericaQuandoIdentificadorEUsuarioComum() {
        criarUsuario("qa-esqueci-comum-1", "qa-esqueci-comum-1@example.com", Role.PARTICIPANTE);
        ResponseEntity<String> resposta = esqueciSenha(bodyEsqueciSenha("qa-esqueci-comum-1"));
        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resposta.getBody()).contains("\"success\":true").contains(MENSAGEM_GENERICA);
    }

    @Test
    void deveGerarTokenDeResetComumQuandoUsuarioComumComEmailConfirmado() {
        Usuario usuario = criarUsuario("qa-esqueci-comum-2", "qa-esqueci-comum-2@example.com", Role.PARTICIPANTE);
        usuario.confirmarEmail();
        Usuario usuarioConfirmado = usuarioRepository.saveAndFlush(usuario);
        ResponseEntity<String> resposta = esqueciSenha(bodyEsqueciSenha("qa-esqueci-comum-2"));
        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(passwordResetTokenRepository.findAll())
                .anyMatch(token -> token.getUsuarioId().equals(usuarioConfirmado.getId()));
        assertThat(adminResetCodigoRepository.findAll())
                .noneMatch(codigo -> codigo.getUsuarioId().equals(usuarioConfirmado.getId()));
    }

    @Test
    void naoDeveGerarTokenDeResetQuandoUsuarioComumComEmailNaoConfirmado() {
        Usuario usuario = criarUsuario("qa-esqueci-naoconf-1", "qa-esqueci-naoconf-1@example.com", Role.PARTICIPANTE);
        assertThat(usuario.getEmailConfirmadoEm()).isNull();
        ResponseEntity<String> resposta = esqueciSenha(bodyEsqueciSenha("qa-esqueci-naoconf-1"));
        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resposta.getBody()).contains("\"success\":true").contains(MENSAGEM_GENERICA);
        assertThat(passwordResetTokenRepository.findAll())
                .noneMatch(token -> token.getUsuarioId().equals(usuario.getId()));
    }

    @Test
    void deveRetornarMensagemIdenticaEntreEmailConfirmadoENaoConfirmado() {
        Usuario confirmado = criarUsuario("qa-esqueci-cmp-conf", "qa-esqueci-cmp-conf@example.com", Role.PARTICIPANTE);
        confirmado.confirmarEmail();
        usuarioRepository.saveAndFlush(confirmado);
        criarUsuario("qa-esqueci-cmp-naoconf", "qa-esqueci-cmp-naoconf@example.com", Role.PARTICIPANTE);
        ResponseEntity<String> respostaConfirmado = esqueciSenha(bodyEsqueciSenha("qa-esqueci-cmp-conf"));
        ResponseEntity<String> respostaNaoConfirmado = esqueciSenha(bodyEsqueciSenha("qa-esqueci-cmp-naoconf"));
        assertThat(respostaConfirmado.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(respostaNaoConfirmado.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(respostaConfirmado.getBody()).isEqualTo(respostaNaoConfirmado.getBody());
    }

    @Test
    void deveRetornarMensagemIdenticaEmCorpoEStatusQuandoIdentificadorEAdmin() {
        criarUsuario("qa-esqueci-admin-1", "qa-esqueci-admin-1@example.com", Role.PARTICIPANTE);
        criarUsuario("qa-esqueci-admin-2", "qa-esqueci-admin-2@example.com", Role.ADMIN);
        ResponseEntity<String> respostaComum = esqueciSenha(bodyEsqueciSenha("qa-esqueci-admin-1"));
        ResponseEntity<String> respostaAdmin = esqueciSenha(bodyEsqueciSenha("qa-esqueci-admin-2"));
        assertThat(respostaComum.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(respostaAdmin.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(respostaComum.getBody()).isEqualTo(respostaAdmin.getBody());
    }

    @Test
    void deveGerarCodigoAdminSemGerarTokenComumQuandoUsuarioEhAdmin() {
        Usuario admin = criarUsuario("qa-esqueci-admin-3", "qa-esqueci-admin-3@example.com", Role.ADMIN);
        ResponseEntity<String> resposta = esqueciSenha(bodyEsqueciSenha("qa-esqueci-admin-3"));
        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(adminResetCodigoRepository.findAll())
                .anyMatch(codigo -> codigo.getUsuarioId().equals(admin.getId()));
        assertThat(passwordResetTokenRepository.findAll())
                .noneMatch(token -> token.getUsuarioId().equals(admin.getId()));
    }

    @Test
    void deveRetornarMensagemGenericaComMesmoCorpoQuandoIdentificadorInexistente() {
        ResponseEntity<String> respostaInexistente = esqueciSenha(bodyEsqueciSenha("apelido-que-nao-existe-na-base"));
        assertThat(respostaInexistente.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(respostaInexistente.getBody()).contains("\"success\":true").contains(MENSAGEM_GENERICA);
        criarUsuario("qa-esqueci-inexistente-cmp", "qa-esqueci-inexistente-cmp@example.com", Role.PARTICIPANTE);
        ResponseEntity<String> respostaComumExistente = esqueciSenha(bodyEsqueciSenha("qa-esqueci-inexistente-cmp"));
        assertThat(respostaComumExistente.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(respostaInexistente.getBody()).isEqualTo(respostaComumExistente.getBody());
    }

    @Test
    void naoDeveGerarTokenNemCodigoQuandoIdentificadorInexistente() {
        long tokensAntes = passwordResetTokenRepository.count();
        long codigosAntes = adminResetCodigoRepository.count();
        ResponseEntity<String> resposta = esqueciSenha(bodyEsqueciSenha("outro-apelido-inexistente-na-base"));
        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(passwordResetTokenRepository.count()).isEqualTo(tokensAntes);
        assertThat(adminResetCodigoRepository.count()).isEqualTo(codigosAntes);
    }

    @Test
    void deveRetornarValidationErrorQuandoIdentificadorAusente() {
        ResponseEntity<String> resposta = esqueciSenha("{\"identificador\":\"\"}");
        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(resposta.getBody()).contains("\"success\":false").contains("VALIDATION_ERROR");
    }

    @Test
    void respostaNuncaContemCodigoOuTokenNoCorpoQuandoIdentificadorEAdmin() {
        Usuario admin = criarUsuario("qa-esqueci-admin-4", "qa-esqueci-admin-4@example.com", Role.ADMIN);
        ResponseEntity<String> resposta = esqueciSenha(bodyEsqueciSenha("qa-esqueci-admin-4"));
        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resposta.getBody()).isEqualTo("{\"success\":true,\"data\":{\"mensagem\":\"" + MENSAGEM_GENERICA + "\"},\"error\":null}");
    }
}
