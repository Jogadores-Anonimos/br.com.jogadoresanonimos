package br.com.jogadoresanonimos.security;

import org.junit.jupiter.api.Test;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;

class TotpServiceTest {

    private final TotpService totpService = new TotpService();

    @Test
    void gerarSecretBase32_deveGerarSecretValidoParaAlfabetoBase32() {
        String secret = totpService.gerarSecretBase32();
        assertThat(secret).matches("^[A-Z2-7]+$");
        assertThat(secret).isNotBlank();
    }

    @Test
    void validar_deveAceitarCodigoValidoDoStepAtual() {
        String secret = totpService.gerarSecretBase32();
        String codigo = gerarCodigoEsperado(secret, 0);
        assertThat(totpService.validar(secret, codigo)).isTrue();
    }

    @Test
    void validar_deveAceitarCodigoDoStepAnteriorDentroDaJanelaDeTolerancia() {
        String secret = totpService.gerarSecretBase32();
        String codigo = gerarCodigoEsperado(secret, -1);
        assertThat(totpService.validar(secret, codigo)).isTrue();
    }

    @Test
    void validar_deveAceitarCodigoDoStepSeguinteDentroDaJanelaDeTolerancia() {
        String secret = totpService.gerarSecretBase32();
        String codigo = gerarCodigoEsperado(secret, 1);
        assertThat(totpService.validar(secret, codigo)).isTrue();
    }

    @Test
    void validar_deveRejeitarCodigoForaDaJanelaDeTolerancia() {
        String secret = totpService.gerarSecretBase32();
        String codigo = gerarCodigoEsperado(secret, 2);
        assertThat(totpService.validar(secret, codigo)).isFalse();
    }

    @Test
    void validar_deveRejeitarCodigoNulo() {
        String secret = totpService.gerarSecretBase32();
        assertThat(totpService.validar(secret, null)).isFalse();
    }

    @Test
    void validar_deveRejeitarCodigoComLetras() {
        String secret = totpService.gerarSecretBase32();
        assertThat(totpService.validar(secret, "12a456")).isFalse();
    }

    @Test
    void validar_deveRejeitarCodigoComTamanhoIncorreto() {
        String secret = totpService.gerarSecretBase32();
        assertThat(totpService.validar(secret, "12345")).isFalse();
        assertThat(totpService.validar(secret, "1234567")).isFalse();
    }

    @Test
    void validar_deveRejeitarCodigoVazio() {
        String secret = totpService.gerarSecretBase32();
        assertThat(totpService.validar(secret, "")).isFalse();
    }

    @Test
    void construirOtpauthUri_deveConterParametrosEsperados() {
        String uri = totpService.construirOtpauthUri("JogadoresAnonimos", "apelidoTeste", "JBSWY3DPEHPK3PXP");
        assertThat(uri).startsWith("otpauth://totp/");
        assertThat(uri).contains("secret=JBSWY3DPEHPK3PXP");
        assertThat(uri).contains("issuer=JogadoresAnonimos");
        assertThat(uri).contains("algorithm=SHA1&digits=6&period=30");
    }

    @Test
    void base32_roundTrip_deveDecodificarOMesmoValorQueFoiCodificado() {
        String secret = totpService.gerarSecretBase32();
        String codigoInicial = gerarCodigoEsperado(secret, 0);
        assertThat(totpService.validar(secret, codigoInicial)).isTrue();
    }

    private String gerarCodigoEsperado(String secretBase32, long deltaSteps) {
        try {
            byte[] chave = decodificarBase32(secretBase32);
            long stepAtual = System.currentTimeMillis() / 1000 / 30;
            long step = stepAtual + deltaSteps;
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
            int codigo = binario % (int) Math.pow(10, 6);
            return String.format("%06d", codigo);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private byte[] decodificarBase32(String base32) {
        String alfabeto = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567";
        String limpo = base32.trim().toUpperCase().replace("=", "");
        int buffer = 0;
        int bitsNoBuffer = 0;
        java.io.ByteArrayOutputStream saida = new java.io.ByteArrayOutputStream();
        for (char c : limpo.toCharArray()) {
            int indice = alfabeto.indexOf(c);
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
}
