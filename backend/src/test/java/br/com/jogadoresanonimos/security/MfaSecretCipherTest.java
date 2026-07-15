package br.com.jogadoresanonimos.security;

import org.junit.jupiter.api.Test;

import java.security.SecureRandom;
import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MfaSecretCipherTest {

    private String gerarChaveBase64() {
        byte[] chave = new byte[32];
        new SecureRandom().nextBytes(chave);
        return Base64.getEncoder().encodeToString(chave);
    }

    @Test
    void cifrarEDecifrar_deveRetornarTextoOriginal() {
        MfaSecretCipher cipher = new MfaSecretCipher(gerarChaveBase64());
        String textoClaro = "JBSWY3DPEHPK3PXP";
        String cifrado = cipher.cifrar(textoClaro);
        String decifrado = cipher.decifrar(cifrado);
        assertThat(decifrado).isEqualTo(textoClaro);
    }

    @Test
    void cifrar_deveGerarValoresDiferentesParaMesmoTextoDevidoAoNonceAleatorio() {
        MfaSecretCipher cipher = new MfaSecretCipher(gerarChaveBase64());
        String textoClaro = "JBSWY3DPEHPK3PXP";
        String cifrado1 = cipher.cifrar(textoClaro);
        String cifrado2 = cipher.cifrar(textoClaro);
        assertThat(cifrado1).isNotEqualTo(cifrado2);
    }

    @Test
    void decifrar_deveFalharQuandoChaveDiferenteDaUsadaParaCifrar() {
        MfaSecretCipher cipherOriginal = new MfaSecretCipher(gerarChaveBase64());
        MfaSecretCipher cipherOutraChave = new MfaSecretCipher(gerarChaveBase64());
        String cifrado = cipherOriginal.cifrar("JBSWY3DPEHPK3PXP");
        assertThatThrownBy(() -> cipherOutraChave.decifrar(cifrado))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("corrompido ou chave incorreta");
    }

    @Test
    void decifrar_deveFalharQuandoDadoCorrompido() {
        MfaSecretCipher cipher = new MfaSecretCipher(gerarChaveBase64());
        String cifrado = cipher.cifrar("JBSWY3DPEHPK3PXP");
        byte[] bytes = Base64.getDecoder().decode(cifrado);
        bytes[bytes.length - 1] ^= 0xFF;
        String cifradoCorrompido = Base64.getEncoder().encodeToString(bytes);
        assertThatThrownBy(() -> cipher.decifrar(cifradoCorrompido))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("corrompido ou chave incorreta");
    }

    @Test
    void cifrar_deveFalharQuandoChaveNulaFoiConfigurada() {
        MfaSecretCipher cipher = new MfaSecretCipher(null);
        assertThatThrownBy(() -> cipher.cifrar("JBSWY3DPEHPK3PXP"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("MFA_SECRET_ENCRYPTION_KEY não configurada");
    }

    @Test
    void cifrar_deveFalharQuandoChaveEmBrancoFoiConfigurada() {
        MfaSecretCipher cipher = new MfaSecretCipher("   ");
        assertThatThrownBy(() -> cipher.cifrar("JBSWY3DPEHPK3PXP"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("MFA_SECRET_ENCRYPTION_KEY não configurada");
    }
}
