package br.com.jogadoresanonimos.auth.controller;

import br.com.jogadoresanonimos.auth.entity.AdminResetCodigo;
import br.com.jogadoresanonimos.auth.repository.AdminResetCodigoRepository;
import br.com.jogadoresanonimos.common.util.HmacHasher;
import br.com.jogadoresanonimos.security.MfaSecretCipher;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("qa-integration")
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class AdminAuthControllerIT {

    @LocalServerPort
    int port;

    @Autowired
    UsuarioRepository usuarioRepository;

    @Autowired
    AdminResetCodigoRepository adminResetCodigoRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    MfaSecretCipher mfaSecretCipher;

    @Autowired
    HmacHasher hmacHasher;

    private final RestTemplate restTemplate = new RestTemplate();

    private String redefinirSenhaUrl() {
        return "http://localhost:" + port + "/api/v1/admin/auth/redefinir-senha";
    }

    private ResponseEntity<String> redefinirSenha(String codigo, String novaSenha, String confirmarNovaSenha) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String body = "{\"codigo\":\"" + codigo + "\",\"novaSenha\":\"" + novaSenha
                + "\",\"confirmarNovaSenha\":\"" + confirmarNovaSenha + "\"}";
        try {
            return restTemplate.postForEntity(redefinirSenhaUrl(), new HttpEntity<>(body, headers), String.class);
        } catch (org.springframework.web.client.HttpStatusCodeException ex) {
            return ResponseEntity.status(ex.getStatusCode()).body(ex.getResponseBodyAsString());
        }
    }

    private Usuario criarAdmin(String apelido, String email) {
        Usuario admin = new Usuario(apelido, email, passwordEncoder.encode("senhaAntiga123"), Role.ADMIN);
        return usuarioRepository.saveAndFlush(admin);
    }

    private AdminResetCodigo persistirCodigoComHashReal(UUID usuarioId, String codigoBruto) {
        AdminResetCodigo resetCodigo = new AdminResetCodigo(usuarioId, hmacHasher.hash(codigoBruto),
                Instant.now().plus(10, ChronoUnit.MINUTES));
        return adminResetCodigoRepository.saveAndFlush(resetCodigo);
    }

    private AdminResetCodigo persistirCodigoExpirado(UUID usuarioId, String codigoBruto) {
        AdminResetCodigo resetCodigo = new AdminResetCodigo(usuarioId, hmacHasher.hash(codigoBruto),
                Instant.now().minus(1, ChronoUnit.MINUTES));
        return adminResetCodigoRepository.saveAndFlush(resetCodigo);
    }

    private String normalizarTraceId(String corpoJson) {
        return corpoJson.replaceAll("\"traceId\":\"[^\"]*\"", "\"traceId\":\"NORMALIZADO\"");
    }

    @Test
    void deveTrocarSenhaNoBancoQuandoCodigoValido() {
        Usuario admin = criarAdmin("qa-admin-redefinir-1", "qa-admin-redefinir-1@example.com");
        String hashAntigo = admin.getSenhaHash();
        String codigoBruto = "111111";
        AdminResetCodigo resetCodigo = persistirCodigoComHashReal(admin.getId(), codigoBruto);
        ResponseEntity<String> resposta = redefinirSenha(codigoBruto, "senhaNovaValida123", "senhaNovaValida123");
        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resposta.getBody()).contains("\"success\":true").contains("Senha redefinida com sucesso.");
        Usuario recarregado = usuarioRepository.findById(admin.getId()).orElseThrow();
        assertThat(recarregado.getSenhaHash()).isNotEqualTo(hashAntigo);
        assertThat(passwordEncoder.matches("senhaNovaValida123", recarregado.getSenhaHash())).isTrue();
        AdminResetCodigo codigoRecarregado = adminResetCodigoRepository.findById(resetCodigo.getId()).orElseThrow();
        assertThat(codigoRecarregado.getUsadoEm()).isNotNull();
    }

    @Test
    void naoDeveAlterarMfaAtivoNemSecretQuandoAdminTemMfaAtivo() {
        Usuario admin = criarAdmin("qa-admin-redefinir-mfa-1", "qa-admin-redefinir-mfa-1@example.com");
        admin.iniciarEnrollmentMfa(mfaSecretCipher.cifrar("JBSWY3DPEHPK3PXP"));
        admin.confirmarMfa();
        usuarioRepository.saveAndFlush(admin);
        String secretCifradoAntes = admin.getMfaSecretCifrado();
        String codigoBruto = "222222";
        persistirCodigoComHashReal(admin.getId(), codigoBruto);
        ResponseEntity<String> resposta = redefinirSenha(codigoBruto, "senhaNovaValida123", "senhaNovaValida123");
        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.OK);
        Usuario recarregado = usuarioRepository.findById(admin.getId()).orElseThrow();
        assertThat(recarregado.isMfaAtivo()).isTrue();
        assertThat(recarregado.getMfaSecretCifrado()).isEqualTo(secretCifradoAntes);
    }

    @Test
    void deveRetornar401AdminResetCodigoInvalidoQuandoCodigoInexistente() {
        ResponseEntity<String> resposta = redefinirSenha("888888", "senhaNovaValida123", "senhaNovaValida123");
        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(resposta.getBody()).contains("\"success\":false").contains("ADMIN_RESET_CODIGO_INVALIDO");
    }

    @Test
    void deveRetornar401AdminResetCodigoInvalidoQuandoCodigoExpirado() {
        Usuario admin = criarAdmin("qa-admin-redefinir-2", "qa-admin-redefinir-2@example.com");
        String codigoBruto = "333333";
        persistirCodigoExpirado(admin.getId(), codigoBruto);
        ResponseEntity<String> resposta = redefinirSenha(codigoBruto, "senhaNovaValida123", "senhaNovaValida123");
        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(resposta.getBody()).contains("\"success\":false").contains("ADMIN_RESET_CODIGO_INVALIDO");
    }

    @Test
    void deveRetornar401AdminResetCodigoInvalidoQuandoCodigoJaUsado_mesmoCorpoDeErroNosTresCasos() {
        Usuario admin = criarAdmin("qa-admin-redefinir-3", "qa-admin-redefinir-3@example.com");
        String codigoBruto = "444444";
        persistirCodigoComHashReal(admin.getId(), codigoBruto);
        ResponseEntity<String> sucesso = redefinirSenha(codigoBruto, "senhaNovaValida123", "senhaNovaValida123");
        assertThat(sucesso.getStatusCode()).isEqualTo(HttpStatus.OK);
        ResponseEntity<String> erroInexistente = redefinirSenha("000001", "outraSenhaValida123", "outraSenhaValida123");
        ResponseEntity<String> erroCodigoJaUsado = redefinirSenha(codigoBruto, "outraSenhaValida123", "outraSenhaValida123");
        assertThat(erroInexistente.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(erroCodigoJaUsado.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(normalizarTraceId(erroInexistente.getBody())).isEqualTo(normalizarTraceId(erroCodigoJaUsado.getBody()));
    }

    @Test
    void segundaTentativaComMesmoCodigoJaUsadoDeveFalhar() {
        Usuario admin = criarAdmin("qa-admin-redefinir-4", "qa-admin-redefinir-4@example.com");
        String codigoBruto = "555555";
        persistirCodigoComHashReal(admin.getId(), codigoBruto);
        ResponseEntity<String> primeira = redefinirSenha(codigoBruto, "senhaNovaValida123", "senhaNovaValida123");
        assertThat(primeira.getStatusCode()).isEqualTo(HttpStatus.OK);
        ResponseEntity<String> segunda = redefinirSenha(codigoBruto, "senhaTerceiraValida123", "senhaTerceiraValida123");
        assertThat(segunda.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(segunda.getBody()).contains("ADMIN_RESET_CODIGO_INVALIDO");
    }

    @Test
    void deveRetornarValidationErrorQuandoSenhasNaoConferem() {
        ResponseEntity<String> resposta = redefinirSenha("666666", "senhaValida123", "diferente123");
        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(resposta.getBody()).contains("AUTH_SENHA_CONFIRMACAO_DIVERGENTE");
    }

    @Test
    void deveRetornarValidationErrorQuandoCodigoNaoTem6Digitos() {
        ResponseEntity<String> resposta = redefinirSenha("123", "senhaNovaValida123", "senhaNovaValida123");
        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(resposta.getBody()).contains("VALIDATION_ERROR");
    }

    @Test
    void nenhumTokenOuCodigoTrafegaEmQueryStringOuUrl() {
        assertThatThrownBy(() -> restTemplate.getForEntity(redefinirSenhaUrl() + "?codigo=999999", String.class))
                .isInstanceOf(org.springframework.web.client.HttpClientErrorException.class);
    }
}
