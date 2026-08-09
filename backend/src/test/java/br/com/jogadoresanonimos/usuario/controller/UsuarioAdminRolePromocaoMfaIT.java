package br.com.jogadoresanonimos.usuario.controller;

import br.com.jogadoresanonimos.security.JwtService;
import br.com.jogadoresanonimos.security.MfaSecretCipher;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("qa-integration")
class UsuarioAdminRolePromocaoMfaIT {

    @LocalServerPort
    int port;

    @Autowired
    UsuarioRepository usuarioRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    JwtService jwtService;

    @Autowired
    MfaSecretCipher mfaSecretCipher;

    private final RestTemplate restTemplate = new RestTemplate(new JdkClientHttpRequestFactory());
    private final List<UUID> usuariosCriadosNoTeste = new ArrayList<>();

    @AfterEach
    void limparUsuariosDeTeste() {
        usuariosCriadosNoTeste.forEach(usuarioRepository::deleteById);
        usuariosCriadosNoTeste.clear();
    }

    private String roleUrl(UUID usuarioId) {
        return "http://localhost:" + port + "/api/v1/admin/usuarios/" + usuarioId + "/role";
    }

    private Usuario criarUsuario(String apelido, Role role) {
        Usuario usuario = new Usuario(apelido, apelido + "@example.com", passwordEncoder.encode("senhaValida123"), role);
        Usuario salvo = usuarioRepository.saveAndFlush(usuario);
        usuariosCriadosNoTeste.add(salvo.getId());
        return salvo;
    }

    private Usuario criarAdminComMfaAtivo(String apelido) {
        Usuario admin = criarUsuario(apelido, Role.ADMIN);
        admin.iniciarEnrollmentMfa(mfaSecretCipher.cifrar("JBSWY3DPEHPK3PXP"));
        admin.confirmarMfa();
        return usuarioRepository.saveAndFlush(admin);
    }

    private String tokenDe(Usuario usuario) {
        return jwtService.gerarToken(usuario.getId(), usuario.getRole().name(), usuario.getSenhaAlteradaEm());
    }

    private static final String BASE32_ALFABETO = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567";

    private byte[] decodificarBase32(String base32) {
        String limpo = base32.trim().toUpperCase().replace("=", "");
        int buffer = 0;
        int bitsNoBuffer = 0;
        java.io.ByteArrayOutputStream saida = new java.io.ByteArrayOutputStream();
        for (char c : limpo.toCharArray()) {
            int indice = BASE32_ALFABETO.indexOf(c);
            if (indice < 0) {
                continue;
            }
            buffer = (buffer << 5) | indice;
            bitsNoBuffer += 5;
            if (bitsNoBuffer >= 8) {
                saida.write((buffer >> (bitsNoBuffer - 8)) & 0xFF);
                bitsNoBuffer -= 8;
            }
        }
        return saida.toByteArray();
    }

    private String codigoTotpValidoPara(String secretBase32) {
        try {
            byte[] chave = decodificarBase32(secretBase32);
            long step = System.currentTimeMillis() / 1000 / 30;
            ByteBuffer buffer = ByteBuffer.allocate(8);
            buffer.putLong(step);
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(new SecretKeySpec(chave, "HmacSHA1"));
            byte[] hash = mac.doFinal(buffer.array());
            int offset = hash[hash.length - 1] & 0x0F;
            int binario = ((hash[offset] & 0x7F) << 24)
                    | ((hash[offset + 1] & 0xFF) << 16)
                    | ((hash[offset + 2] & 0xFF) << 8)
                    | (hash[offset + 3] & 0xFF);
            int codigo = binario % 1_000_000;
            return String.format("%06d", codigo);
        } catch (Exception e) {
            throw new IllegalStateException("Falha ao calcular TOTP de teste", e);
        }
    }

    private ResponseEntity<String> editarRole(Usuario solicitante, UUID alvoId, String novaRole, String codigoTotp) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(tokenDe(solicitante));
        String corpoTotp = codigoTotp == null ? "" : ",\"codigoTotp\":\"" + codigoTotp + "\"";
        String body = "{\"role\":\"" + novaRole + "\"" + corpoTotp + "}";
        try {
            return restTemplate.exchange(roleUrl(alvoId), org.springframework.http.HttpMethod.PUT,
                    new HttpEntity<>(body, headers), String.class);
        } catch (HttpStatusCodeException ex) {
            return ResponseEntity.status(ex.getStatusCode()).body(ex.getResponseBodyAsString());
        }
    }

    @Test
    void devePromoverComSucessoQuandoTotpDoPromotorEstaCorreto() {
        Usuario promotor = criarAdminComMfaAtivo("qa-promotor-ok");
        Usuario alvo = criarUsuario("qa-alvo-promocao-ok", Role.PARTICIPANTE);
        String codigo = codigoTotpValidoPara("JBSWY3DPEHPK3PXP");
        ResponseEntity<String> resposta = editarRole(promotor, alvo.getId(), "ADMIN", codigo);
        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resposta.getBody()).contains("\"success\":true").contains("\"role\":\"ADMIN\"");
        Usuario recarregado = usuarioRepository.findById(alvo.getId()).orElseThrow();
        assertThat(recarregado.getRole()).isEqualTo(Role.ADMIN);
    }

    @Test
    void deveBloquearPromocaoQuandoPromotorNaoTemMfaConfigurado() {
        Usuario promotor = criarUsuario("qa-promotor-sem-mfa", Role.ADMIN);
        Usuario alvo = criarUsuario("qa-alvo-sem-mfa-promotor", Role.PARTICIPANTE);
        ResponseEntity<String> resposta = editarRole(promotor, alvo.getId(), "ADMIN", "123456");
        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(resposta.getBody())
                .contains("\"success\":false")
                .contains("USUARIO_PROMOCAO_MFA_NAO_CONFIGURADO");
        Usuario recarregado = usuarioRepository.findById(alvo.getId()).orElseThrow();
        assertThat(recarregado.getRole()).isEqualTo(Role.PARTICIPANTE);
    }

    @Test
    void deveBloquearPromocaoQuandoTotpIncorreto() {
        Usuario promotor = criarAdminComMfaAtivo("qa-promotor-totp-errado");
        Usuario alvo = criarUsuario("qa-alvo-totp-errado", Role.PARTICIPANTE);
        ResponseEntity<String> resposta = editarRole(promotor, alvo.getId(), "ADMIN", "999999");
        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(resposta.getBody())
                .contains("\"success\":false")
                .contains("USUARIO_PROMOCAO_TOTP_INVALIDO");
        Usuario recarregado = usuarioRepository.findById(alvo.getId()).orElseThrow();
        assertThat(recarregado.getRole()).isEqualTo(Role.PARTICIPANTE);
    }

    @Test
    void deveBloquearPromocaoQuandoTotpAusente() {
        Usuario promotor = criarAdminComMfaAtivo("qa-promotor-totp-ausente");
        Usuario alvo = criarUsuario("qa-alvo-totp-ausente", Role.PARTICIPANTE);
        ResponseEntity<String> resposta = editarRole(promotor, alvo.getId(), "ADMIN", null);
        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(resposta.getBody()).contains("USUARIO_PROMOCAO_TOTP_INVALIDO");
    }

    @Test
    void deveRetornarValidationErrorQuandoCodigoTotpNaoTem6DigitosNumericos() {
        Usuario promotor = criarAdminComMfaAtivo("qa-promotor-formato-invalido");
        Usuario alvo = criarUsuario("qa-alvo-formato-invalido", Role.PARTICIPANTE);
        ResponseEntity<String> resposta = editarRole(promotor, alvo.getId(), "ADMIN", "12a456");
        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(resposta.getBody())
                .contains("\"success\":false")
                .contains("VALIDATION_ERROR");
        Usuario recarregado = usuarioRepository.findById(alvo.getId()).orElseThrow();
        assertThat(recarregado.getRole()).isEqualTo(Role.PARTICIPANTE);
    }

    @Test
    void deveRebaixarSemExigirTotpMesmoQuandoPromotorNaoTemMfa() {
        Usuario promotor = criarUsuario("qa-promotor-rebaixa", Role.ADMIN);
        criarUsuario("qa-outro-admin-rebaixa", Role.ADMIN);
        Usuario alvo = criarUsuario("qa-alvo-rebaixamento", Role.ADMIN);
        ResponseEntity<String> resposta = editarRole(promotor, alvo.getId(), "PARTICIPANTE", null);
        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resposta.getBody()).contains("\"success\":true").contains("\"role\":\"PARTICIPANTE\"");
        Usuario recarregado = usuarioRepository.findById(alvo.getId()).orElseThrow();
        assertThat(recarregado.getRole()).isEqualTo(Role.PARTICIPANTE);
    }

    @Test
    void deveSerNoOpSemExigirTotpQuandoNovaRoleEhIgualAAtual() {
        Usuario promotor = criarUsuario("qa-promotor-noop", Role.ADMIN);
        Usuario alvo = criarUsuario("qa-alvo-noop", Role.PARTICIPANTE);
        ResponseEntity<String> resposta = editarRole(promotor, alvo.getId(), "PARTICIPANTE", null);
        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resposta.getBody()).contains("\"success\":true").contains("\"role\":\"PARTICIPANTE\"");
    }
}
