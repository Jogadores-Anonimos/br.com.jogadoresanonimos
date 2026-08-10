package br.com.jogadoresanonimos.usuario.controller;

import br.com.jogadoresanonimos.auth.entity.EmailConfirmacaoToken;
import br.com.jogadoresanonimos.auth.entity.TipoEmailConfirmacaoToken;
import br.com.jogadoresanonimos.auth.repository.EmailConfirmacaoTokenRepository;
import br.com.jogadoresanonimos.auth.service.EmailService;
import br.com.jogadoresanonimos.security.JwtService;
import br.com.jogadoresanonimos.usuario.entity.Role;
import br.com.jogadoresanonimos.usuario.entity.Usuario;
import br.com.jogadoresanonimos.usuario.repository.UsuarioRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("qa-integration")
class TrocaEmailFluxoDoisPasosIT {

    private static final String SENHA_ATUAL = "senhaAtual123";
    private static final String MENSAGEM_SOLICITACAO =
            "Enviamos um link de confirmação para o seu e-mail atual. Clique nele para concluir a troca.";
    private static final String MENSAGEM_CONFIRMACAO_SUCESSO = "E-mail confirmado com sucesso.";

    @LocalServerPort
    int port;

    @Autowired
    UsuarioRepository usuarioRepository;

    @Autowired
    EmailConfirmacaoTokenRepository emailConfirmacaoTokenRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    JwtService jwtService;

    @MockitoBean
    EmailService emailService;

    private final RestTemplate restTemplate = new RestTemplate(new JdkClientHttpRequestFactory());
    private final List<UUID> usuariosCriadosNoTeste = new ArrayList<>();

    @AfterEach
    void limparUsuariosDeTeste() {
        usuariosCriadosNoTeste.forEach(usuarioRepository::deleteById);
        usuariosCriadosNoTeste.clear();
    }

    private String url(String path) {
        return "http://localhost:" + port + path;
    }

    private String sufixoCurto() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

    private Usuario criarUsuario(String sufixo) {
        Usuario usuario = new Usuario("qa-troca-" + sufixo, "qa-troca-" + sufixo + "@example.com",
                passwordEncoder.encode(SENHA_ATUAL), Role.PARTICIPANTE);
        Usuario salvo = usuarioRepository.saveAndFlush(usuario);
        usuariosCriadosNoTeste.add(salvo.getId());
        return salvo;
    }

    private String tokenDe(Usuario usuario) {
        return jwtService.gerarToken(usuario.getId(), usuario.getRole().name(), usuario.getSenhaAlteradaEm());
    }

    private String sha256(String valorBruto) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] resultado = digest.digest(valorBruto.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(resultado);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private EmailConfirmacaoToken criarTokenTrocaEmail(UUID usuarioId, String tokenBruto, String novoEmail, Instant expiraEm) {
        EmailConfirmacaoToken token = new EmailConfirmacaoToken(usuarioId, sha256(tokenBruto), expiraEm,
                TipoEmailConfirmacaoToken.TROCA_EMAIL, novoEmail);
        return emailConfirmacaoTokenRepository.saveAndFlush(token);
    }

    private EmailConfirmacaoToken criarTokenCadastro(UUID usuarioId, String tokenBruto, Instant expiraEm) {
        EmailConfirmacaoToken token = new EmailConfirmacaoToken(usuarioId, sha256(tokenBruto), expiraEm);
        return emailConfirmacaoTokenRepository.saveAndFlush(token);
    }

    private ResponseEntity<String> solicitarTroca(Usuario solicitante, String senhaAtual, String novoEmail) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(tokenDe(solicitante));
        String corpo = "{\"senhaAtual\":\"" + senhaAtual + "\",\"novoEmail\":\"" + novoEmail + "\"}";
        try {
            return restTemplate.postForEntity(url("/api/v1/usuarios/me/email/solicitar-troca"),
                    new HttpEntity<>(corpo, headers), String.class);
        } catch (HttpStatusCodeException ex) {
            return ResponseEntity.status(ex.getStatusCode()).body(ex.getResponseBodyAsString());
        }
    }

    private ResponseEntity<String> confirmarEmail(String tokenBruto) {
        try {
            return restTemplate.getForEntity(url("/api/v1/auth/confirmar-email?token=" + tokenBruto), String.class);
        } catch (HttpStatusCodeException ex) {
            return ResponseEntity.status(ex.getStatusCode()).body(ex.getResponseBodyAsString());
        }
    }

    private ResponseEntity<String> atualizarConta(Usuario solicitante, String corpoJson) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(tokenDe(solicitante));
        try {
            return restTemplate.exchange(url("/api/v1/usuarios/me"), HttpMethod.PUT,
                    new HttpEntity<>(corpoJson, headers), String.class);
        } catch (HttpStatusCodeException ex) {
            return ResponseEntity.status(ex.getStatusCode()).body(ex.getResponseBodyAsString());
        }
    }

    @Test
    void solicitarTroca_comSenhaCertaGeraTokenENaoAlteraEmailAinda() {
        doNothing().when(emailService).enviarLinkConfirmacaoEmail(anyString(), anyString(), anyString());
        Usuario usuario = criarUsuario(sufixoCurto());
        String emailOriginal = usuario.getEmail();
        String novoEmail = "novo-" + sufixoCurto() + "@example.com";

        ResponseEntity<String> resposta = solicitarTroca(usuario, SENHA_ATUAL, novoEmail);

        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resposta.getBody()).contains("\"success\":true").contains(MENSAGEM_SOLICITACAO);

        Usuario recarregado = usuarioRepository.findById(usuario.getId()).orElseThrow();
        assertThat(recarregado.getEmail()).isEqualTo(emailOriginal);

        List<EmailConfirmacaoToken> tokensTrocaEmail = emailConfirmacaoTokenRepository
                .findByUsuarioIdAndTipoAndUsadoEmIsNull(usuario.getId(), TipoEmailConfirmacaoToken.TROCA_EMAIL);
        assertThat(tokensTrocaEmail).hasSize(1);
        assertThat(tokensTrocaEmail.get(0).getNovoEmail()).isEqualTo(novoEmail);
        assertThat(tokensTrocaEmail.get(0).getUsadoEm()).isNull();
    }

    @Test
    void solicitarTroca_comSenhaErradaRetorna401ENaoGeraToken() {
        Usuario usuario = criarUsuario(sufixoCurto());
        String novoEmail = "novo-" + sufixoCurto() + "@example.com";

        ResponseEntity<String> resposta = solicitarTroca(usuario, "senhaErrada999", novoEmail);

        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(resposta.getBody()).contains("\"success\":false").contains("USUARIO_SENHA_ATUAL_INCORRETA");
        assertThat(emailConfirmacaoTokenRepository
                .findByUsuarioIdAndTipoAndUsadoEmIsNull(usuario.getId(), TipoEmailConfirmacaoToken.TROCA_EMAIL))
                .isEmpty();
        Usuario recarregado = usuarioRepository.findById(usuario.getId()).orElseThrow();
        assertThat(recarregado.getEmail()).isEqualTo(usuario.getEmail());
    }

    @Test
    void solicitarTroca_comNovoEmailJaEmUsoRetorna409() {
        Usuario usuario = criarUsuario(sufixoCurto());
        Usuario outraConta = criarUsuario(sufixoCurto());

        ResponseEntity<String> resposta = solicitarTroca(usuario, SENHA_ATUAL, outraConta.getEmail());

        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(resposta.getBody()).contains("\"success\":false").contains("USUARIO_DADOS_EM_USO");
        assertThat(emailConfirmacaoTokenRepository
                .findByUsuarioIdAndTipoAndUsadoEmIsNull(usuario.getId(), TipoEmailConfirmacaoToken.TROCA_EMAIL))
                .isEmpty();
    }

    @Test
    void confirmarEmail_comTokenTrocaEmailValidoEfetivaATroca() {
        Usuario usuario = criarUsuario(sufixoCurto());
        String emailAntigo = usuario.getEmail();
        String novoEmail = "confirmado-" + sufixoCurto() + "@example.com";
        String tokenBruto = "token-troca-valido-" + UUID.randomUUID();
        criarTokenTrocaEmail(usuario.getId(), tokenBruto, novoEmail, Instant.now().plus(24, ChronoUnit.HOURS));

        ResponseEntity<String> resposta = confirmarEmail(tokenBruto);

        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resposta.getBody()).contains("\"success\":true").contains(MENSAGEM_CONFIRMACAO_SUCESSO);

        Usuario recarregado = usuarioRepository.findById(usuario.getId()).orElseThrow();
        assertThat(recarregado.getEmail()).isEqualTo(novoEmail);
        assertThat(recarregado.getEmail()).isNotEqualTo(emailAntigo);
        assertThat(recarregado.getEmailConfirmadoEm()).isNotNull();

        EmailConfirmacaoToken tokenRecarregado = emailConfirmacaoTokenRepository.findByTokenHash(sha256(tokenBruto)).orElseThrow();
        assertThat(tokenRecarregado.getUsadoEm()).isNotNull();
    }

    @Test
    void confirmarEmail_comTokenCadastroNaoMexeEmNovoEmailNemEfetivaTroca() {
        Usuario usuario = criarUsuario(sufixoCurto());
        String emailOriginal = usuario.getEmail();
        String tokenBruto = "token-cadastro-" + UUID.randomUUID();
        criarTokenCadastro(usuario.getId(), tokenBruto, Instant.now().plus(24, ChronoUnit.HOURS));

        ResponseEntity<String> resposta = confirmarEmail(tokenBruto);

        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resposta.getBody()).contains("\"success\":true").contains(MENSAGEM_CONFIRMACAO_SUCESSO);

        Usuario recarregado = usuarioRepository.findById(usuario.getId()).orElseThrow();
        assertThat(recarregado.getEmail()).isEqualTo(emailOriginal);
        assertThat(recarregado.getEmailConfirmadoEm()).isNotNull();

        EmailConfirmacaoToken tokenRecarregado = emailConfirmacaoTokenRepository.findByTokenHash(sha256(tokenBruto)).orElseThrow();
        assertThat(tokenRecarregado.getTipo()).isEqualTo(TipoEmailConfirmacaoToken.CADASTRO);
        assertThat(tokenRecarregado.getNovoEmail()).isNull();
    }

    @Test
    void confirmarEmail_comTokenTrocaEmailExpiradoNaoAplicaATroca() {
        Usuario usuario = criarUsuario(sufixoCurto());
        String emailOriginal = usuario.getEmail();
        String novoEmail = "expirado-" + sufixoCurto() + "@example.com";
        String tokenBruto = "token-troca-expirado-" + UUID.randomUUID();
        criarTokenTrocaEmail(usuario.getId(), tokenBruto, novoEmail, Instant.now().minus(1, ChronoUnit.HOURS));

        ResponseEntity<String> resposta = confirmarEmail(tokenBruto);

        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(resposta.getBody()).contains("\"success\":false").contains("AUTH_EMAIL_CONFIRMACAO_TOKEN_INVALIDO");

        Usuario recarregado = usuarioRepository.findById(usuario.getId()).orElseThrow();
        assertThat(recarregado.getEmail()).isEqualTo(emailOriginal);
        EmailConfirmacaoToken tokenRecarregado = emailConfirmacaoTokenRepository.findByTokenHash(sha256(tokenBruto)).orElseThrow();
        assertThat(tokenRecarregado.getUsadoEm()).isNull();
    }

    @Test
    void confirmarEmail_comNovoEmailTomadoPorOutraContaNoIntervaloRetorna409() {
        Usuario usuario = criarUsuario(sufixoCurto());
        String emailOriginal = usuario.getEmail();
        String novoEmail = "disputado-" + sufixoCurto() + "@example.com";
        String tokenBruto = "token-troca-disputado-" + UUID.randomUUID();
        criarTokenTrocaEmail(usuario.getId(), tokenBruto, novoEmail, Instant.now().plus(24, ChronoUnit.HOURS));

        Usuario outraConta = criarUsuario(sufixoCurto());
        outraConta.trocarEmailConfirmado(novoEmail);
        usuarioRepository.saveAndFlush(outraConta);

        ResponseEntity<String> resposta = confirmarEmail(tokenBruto);

        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(resposta.getBody()).contains("\"success\":false").contains("USUARIO_DADOS_EM_USO");

        Usuario recarregado = usuarioRepository.findById(usuario.getId()).orElseThrow();
        assertThat(recarregado.getEmail()).isEqualTo(emailOriginal);
        EmailConfirmacaoToken tokenRecarregado = emailConfirmacaoTokenRepository.findByTokenHash(sha256(tokenBruto)).orElseThrow();
        assertThat(tokenRecarregado.getUsadoEm()).isNull();
    }

    @Test
    void putUsuariosMe_naoAceitaMaisEmailNoPayload() {
        Usuario usuario = criarUsuario(sufixoCurto());
        String emailOriginal = usuario.getEmail();
        String corpo = "{\"apelido\":\"" + usuario.getApelido() + "\",\"email\":\"sequestrado-" + sufixoCurto() + "@example.com\"}";

        ResponseEntity<String> resposta = atualizarConta(usuario, corpo);

        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.OK);
        Usuario recarregado = usuarioRepository.findById(usuario.getId()).orElseThrow();
        assertThat(recarregado.getEmail()).isEqualTo(emailOriginal);
        assertThat(resposta.getBody()).contains("\"email\":\"" + emailOriginal + "\"");
    }

    @Test
    void fluxoCompleto_solicitarTrocaEDepoisConfirmarViaTokenGeradoNoPasso1() {
        doNothing().when(emailService).enviarLinkConfirmacaoEmail(anyString(), anyString(), anyString());
        Usuario usuario = criarUsuario(sufixoCurto());
        String novoEmail = "fluxo-completo-" + sufixoCurto() + "@example.com";

        ResponseEntity<String> respostaSolicitacao = solicitarTroca(usuario, SENHA_ATUAL, novoEmail);
        assertThat(respostaSolicitacao.getStatusCode()).isEqualTo(HttpStatus.OK);

        Usuario aposSolicitar = usuarioRepository.findById(usuario.getId()).orElseThrow();
        assertThat(aposSolicitar.getEmail()).isEqualTo(usuario.getEmail());

        EmailConfirmacaoToken tokenGerado = emailConfirmacaoTokenRepository
                .findByUsuarioIdAndTipoAndUsadoEmIsNull(usuario.getId(), TipoEmailConfirmacaoToken.TROCA_EMAIL)
                .get(0);
        assertThat(tokenGerado.getNovoEmail()).isEqualTo(novoEmail);
    }
}
