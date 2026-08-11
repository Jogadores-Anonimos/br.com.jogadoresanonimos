package br.com.jogadoresanonimos.auth.controller;

import br.com.jogadoresanonimos.auth.entity.EmailConfirmacaoToken;
import br.com.jogadoresanonimos.auth.repository.EmailConfirmacaoTokenRepository;
import br.com.jogadoresanonimos.auth.service.EmailService;
import br.com.jogadoresanonimos.auth.service.RecaptchaService;
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
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("qa-integration")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class AuthConfirmacaoEmailControllerIT {

    private static final String MENSAGEM_CONFIRMACAO_SUCESSO = "E-mail confirmado com sucesso.";
    private static final String MENSAGEM_REENVIAR_GENERICA =
            "Se o apelido ou e-mail existir em nossa base e o e-mail ainda não estiver confirmado, enviaremos um novo link de confirmação.";

    @LocalServerPort
    int port;

    @Autowired
    UsuarioRepository usuarioRepository;

    @Autowired
    EmailConfirmacaoTokenRepository emailConfirmacaoTokenRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @MockitoBean
    RecaptchaService recaptchaService;

    @MockitoBean
    EmailService emailService;

    private final RestTemplate restTemplate = new RestTemplate();

    private String url(String path) {
        return "http://localhost:" + port + path;
    }

    private ResponseEntity<String> get(String path) {
        try {
            return restTemplate.getForEntity(url(path), String.class);
        } catch (HttpStatusCodeException ex) {
            return ResponseEntity.status(ex.getStatusCode()).body(ex.getResponseBodyAsString());
        }
    }

    private ResponseEntity<String> post(String path, String corpoJson) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        try {
            return restTemplate.postForEntity(url(path), new HttpEntity<>(corpoJson, headers), String.class);
        } catch (HttpStatusCodeException ex) {
            return ResponseEntity.status(ex.getStatusCode()).body(ex.getResponseBodyAsString());
        }
    }

    private String sufixoCurto() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

    private Usuario criarUsuario(String sufixo, boolean emailConfirmado) {
        Usuario usuario = new Usuario("qa-confe-" + sufixo, "qa-confemail-" + sufixo + "@example.com",
                passwordEncoder.encode("senhaAtual123"), Role.PARTICIPANTE);
        usuario = usuarioRepository.saveAndFlush(usuario);
        if (emailConfirmado) {
            usuario.confirmarEmail();
            usuario = usuarioRepository.saveAndFlush(usuario);
        }
        return usuario;
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

    private EmailConfirmacaoToken criarTokenParaUsuario(UUID usuarioId, String tokenBruto, Instant expiraEm) {
        EmailConfirmacaoToken token = new EmailConfirmacaoToken(usuarioId, sha256(tokenBruto), expiraEm);
        return emailConfirmacaoTokenRepository.saveAndFlush(token);
    }

    @Test
    void confirmarEmail_deveConfirmarComTokenValidoNaoUsadoNaoExpirado() {
        Usuario usuario = criarUsuario(sufixoCurto(), false);
        String tokenBruto = "token-valido-" + UUID.randomUUID();
        criarTokenParaUsuario(usuario.getId(), tokenBruto, Instant.now().plus(24, ChronoUnit.HOURS));
        ResponseEntity<String> resposta = get("/api/v1/auth/confirmar-email?token=" + tokenBruto);
        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resposta.getBody()).contains("\"success\":true").contains(MENSAGEM_CONFIRMACAO_SUCESSO);
        Usuario recarregado = usuarioRepository.findById(usuario.getId()).orElseThrow();
        assertThat(recarregado.getEmailConfirmadoEm()).isNotNull();
        EmailConfirmacaoToken tokenRecarregado = emailConfirmacaoTokenRepository.findByTokenHash(sha256(tokenBruto)).orElseThrow();
        assertThat(tokenRecarregado.getUsadoEm()).isNotNull();
    }

    @Test
    void confirmarEmail_deveSerIdempotenteQuandoTokenJaUsadoEContaJaConfirmada() {
        Usuario usuario = criarUsuario(sufixoCurto(), false);
        String tokenBruto = "token-idempotente-" + UUID.randomUUID();
        criarTokenParaUsuario(usuario.getId(), tokenBruto, Instant.now().plus(24, ChronoUnit.HOURS));
        ResponseEntity<String> primeira = get("/api/v1/auth/confirmar-email?token=" + tokenBruto);
        assertThat(primeira.getStatusCode()).isEqualTo(HttpStatus.OK);
        ResponseEntity<String> segunda = get("/api/v1/auth/confirmar-email?token=" + tokenBruto);
        assertThat(segunda.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(segunda.getBody()).contains("\"success\":true").contains(MENSAGEM_CONFIRMACAO_SUCESSO);
    }

    @Test
    void confirmarEmail_deveRetornar401QuandoTokenExpiradoENuncaUsado() {
        Usuario usuario = criarUsuario(sufixoCurto(), false);
        String tokenBruto = "token-expirado-" + UUID.randomUUID();
        criarTokenParaUsuario(usuario.getId(), tokenBruto, Instant.now().minus(1, ChronoUnit.HOURS));
        ResponseEntity<String> resposta = get("/api/v1/auth/confirmar-email?token=" + tokenBruto);
        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(resposta.getBody()).contains("\"success\":false").contains("AUTH_EMAIL_CONFIRMACAO_TOKEN_INVALIDO");
        Usuario recarregado = usuarioRepository.findById(usuario.getId()).orElseThrow();
        assertThat(recarregado.getEmailConfirmadoEm()).isNull();
    }

    @Test
    void confirmarEmail_deveRetornar401QuandoTokenInexistente() {
        ResponseEntity<String> resposta = get("/api/v1/auth/confirmar-email?token=token-que-nunca-existiu-" + UUID.randomUUID());
        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(resposta.getBody()).contains("\"success\":false").contains("AUTH_EMAIL_CONFIRMACAO_TOKEN_INVALIDO");
    }

    @Test
    void confirmarEmail_deveRetornarValidationErrorQuandoTokenVazio() {
        ResponseEntity<String> resposta = get("/api/v1/auth/confirmar-email?token=");
        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(resposta.getBody()).contains("\"success\":false").contains("VALIDATION_ERROR");
    }

    @Test
    void confirmarEmail_deveRetornar429AoEstourarRateLimitPorToken() {
        Usuario usuario = criarUsuario(sufixoCurto(), false);
        String tokenBruto = "token-rate-limit-" + UUID.randomUUID();
        criarTokenParaUsuario(usuario.getId(), tokenBruto, Instant.now().plus(24, ChronoUnit.HOURS));
        for (int i = 0; i < 5; i++) {
            ResponseEntity<String> resposta = get("/api/v1/auth/confirmar-email?token=" + tokenBruto);
            assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.OK);
        }
        ResponseEntity<String> excedente = get("/api/v1/auth/confirmar-email?token=" + tokenBruto);
        assertThat(excedente.getStatusCode()).isEqualTo(HttpStatus.TOO_MANY_REQUESTS);
        assertThat(excedente.getBody()).contains("\"success\":false").contains("AUTH_RATE_LIMITED");
    }

    @Test
    void reenviarConfirmacaoEmail_deveRetornarMensagemGenericaQuandoContaComEmailPendente() {
        Usuario usuario = criarUsuario(sufixoCurto(), false);
        ResponseEntity<String> resposta = post("/api/v1/auth/reenviar-confirmacao-email",
                "{\"identificador\":\"" + usuario.getApelido() + "\"}");
        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resposta.getBody()).contains("\"success\":true").contains(MENSAGEM_REENVIAR_GENERICA);
    }

    @Test
    void reenviarConfirmacaoEmail_deveGerarNovoTokenEChamarEnvioQuandoContaComEmailPendente() {
        Usuario usuario = criarUsuario(sufixoCurto(), false);
        long tokensAntes = emailConfirmacaoTokenRepository.findAll().stream()
                .filter(t -> t.getUsuarioId().equals(usuario.getId())).count();
        ResponseEntity<String> resposta = post("/api/v1/auth/reenviar-confirmacao-email",
                "{\"identificador\":\"" + usuario.getApelido() + "\"}");
        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.OK);
        long tokensDepois = emailConfirmacaoTokenRepository.findAll().stream()
                .filter(t -> t.getUsuarioId().equals(usuario.getId())).count();
        assertThat(tokensDepois).isEqualTo(tokensAntes + 1);
    }

    @Test
    void reenviarConfirmacaoEmail_naoDeveGerarNovoTokenQuandoContaJaConfirmada() {
        Usuario usuario = criarUsuario(sufixoCurto(), true);
        long tokensAntes = emailConfirmacaoTokenRepository.findAll().stream()
                .filter(t -> t.getUsuarioId().equals(usuario.getId())).count();
        ResponseEntity<String> resposta = post("/api/v1/auth/reenviar-confirmacao-email",
                "{\"identificador\":\"" + usuario.getApelido() + "\"}");
        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resposta.getBody()).contains(MENSAGEM_REENVIAR_GENERICA);
        long tokensDepois = emailConfirmacaoTokenRepository.findAll().stream()
                .filter(t -> t.getUsuarioId().equals(usuario.getId())).count();
        assertThat(tokensDepois).isEqualTo(tokensAntes);
    }

    @Test
    void reenviarConfirmacaoEmail_deveRetornarMesmaMensagemQuandoContaInexistente() {
        ResponseEntity<String> resposta = post("/api/v1/auth/reenviar-confirmacao-email",
                "{\"identificador\":\"apelido-que-nao-existe-" + UUID.randomUUID() + "\"}");
        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resposta.getBody()).contains("\"success\":true").contains(MENSAGEM_REENVIAR_GENERICA);
    }

    @Test
    void reenviarConfirmacaoEmail_deveRetornarValidationErrorQuandoIdentificadorVazio() {
        ResponseEntity<String> resposta = post("/api/v1/auth/reenviar-confirmacao-email", "{\"identificador\":\"\"}");
        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(resposta.getBody()).contains("\"success\":false").contains("VALIDATION_ERROR");
    }

    @Test
    void reenviarConfirmacaoEmail_rateLimitPorUsuarioEstouradoAindaAssimRetorna200Generico() {
        Usuario usuario = criarUsuario(sufixoCurto(), false);
        String corpo = "{\"identificador\":\"" + usuario.getApelido() + "\"}";
        for (int i = 0; i < 3; i++) {
            ResponseEntity<String> resposta = post("/api/v1/auth/reenviar-confirmacao-email", corpo);
            assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.OK);
        }
        ResponseEntity<String> quartaChamada = post("/api/v1/auth/reenviar-confirmacao-email", corpo);
        assertThat(quartaChamada.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(quartaChamada.getBody()).contains("\"success\":true").contains(MENSAGEM_REENVIAR_GENERICA);
        long tokensParaUsuario = emailConfirmacaoTokenRepository.findAll().stream()
                .filter(t -> t.getUsuarioId().equals(usuario.getId())).count();
        assertThat(tokensParaUsuario).isEqualTo(3);
    }

    @Test
    void reenviarConfirmacaoEmail_deveRetornar429AoEstourarRateLimitGlobal() {
        String prefixo = "qa-varredura-" + UUID.randomUUID();
        for (int i = 0; i < 30; i++) {
            ResponseEntity<String> resposta = post("/api/v1/auth/reenviar-confirmacao-email",
                    "{\"identificador\":\"" + prefixo + "-" + i + "\"}");
            assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.OK);
        }
        ResponseEntity<String> excedente = post("/api/v1/auth/reenviar-confirmacao-email",
                "{\"identificador\":\"" + prefixo + "-excedente\"}");
        assertThat(excedente.getStatusCode()).isEqualTo(HttpStatus.TOO_MANY_REQUESTS);
        assertThat(excedente.getBody()).contains("\"success\":false").contains("AUTH_RATE_LIMITED");
    }

    @Test
    @Transactional
    void registro_devePersistirTokenDeConfirmacaoNaMesmaTransacaoDoCadastro() {
        doNothing().when(recaptchaService).validar(anyString());
        String apelido = "qa-reg-tk-" + sufixoCurto();
        String corpo = "{\"apelido\":\"" + apelido + "\",\"email\":\"" + apelido + "@example.com\"," +
                "\"senha\":\"senhaValida123\",\"confirmarSenha\":\"senhaValida123\",\"recaptchaToken\":\"qualquer\"}";
        ResponseEntity<String> resposta = post("/api/v1/auth/registro", corpo);
        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        Usuario usuario = usuarioRepository.findByApelidoIgnoreCaseOrEmailIgnoreCase(apelido, apelido).orElseThrow();
        assertThat(emailConfirmacaoTokenRepository.findAll())
                .anyMatch(t -> t.getUsuarioId().equals(usuario.getId()));
    }

    @Test
    @Transactional
    void registro_naoDeveMudarContratoDeRespostaQuandoEnvioDeEmailFalha() {
        doNothing().when(recaptchaService).validar(anyString());
        doThrow(new RuntimeException("falha simulada de envio"))
                .when(emailService).enviarLinkConfirmacaoEmail(anyString(), anyString(), anyString(), any());
        String apelido = "qa-reg-falha-" + sufixoCurto();
        String corpo = "{\"apelido\":\"" + apelido + "\",\"email\":\"" + apelido + "@example.com\"," +
                "\"senha\":\"senhaValida123\",\"confirmarSenha\":\"senhaValida123\",\"recaptchaToken\":\"qualquer\"}";
        ResponseEntity<String> resposta = post("/api/v1/auth/registro", corpo);
        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(resposta.getBody()).contains("\"success\":true").contains(apelido);
        Usuario usuario = usuarioRepository.findByApelidoIgnoreCaseOrEmailIgnoreCase(apelido, apelido).orElseThrow();
        assertThat(emailConfirmacaoTokenRepository.findAll())
                .anyMatch(t -> t.getUsuarioId().equals(usuario.getId()));
    }
}
