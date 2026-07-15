package br.com.jogadoresanonimos.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.AEADBadTagException;
import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Base64;

@Component
public class MfaSecretCipher {

    private static final int NONCE_LENGTH_BYTES = 12;
    private static final int TAG_LENGTH_BITS = 128;

    private final String secretBase64;
    private SecretKeySpec keySpec;
    private final SecureRandom secureRandom = new SecureRandom();

    public MfaSecretCipher(@Value("${app.mfa.secret-encryption-key}") String secretBase64) {
        this.secretBase64 = secretBase64;
    }

    public String cifrar(String textoClaro) {
        try {
            byte[] nonce = new byte[NONCE_LENGTH_BYTES];
            secureRandom.nextBytes(nonce);
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, chave(), new GCMParameterSpec(TAG_LENGTH_BITS, nonce));
            byte[] cifrado = cipher.doFinal(textoClaro.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            ByteBuffer buffer = ByteBuffer.allocate(nonce.length + cifrado.length);
            buffer.put(nonce);
            buffer.put(cifrado);
            return Base64.getEncoder().encodeToString(buffer.array());
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Falha ao cifrar secret de MFA", e);
        }
    }

    public String decifrar(String valorCifrado) {
        try {
            byte[] dados = Base64.getDecoder().decode(valorCifrado);
            ByteBuffer buffer = ByteBuffer.wrap(dados);
            byte[] nonce = new byte[NONCE_LENGTH_BYTES];
            buffer.get(nonce);
            byte[] cifrado = new byte[buffer.remaining()];
            buffer.get(cifrado);
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, chave(), new GCMParameterSpec(TAG_LENGTH_BITS, nonce));
            byte[] textoClaro = cipher.doFinal(cifrado);
            return new String(textoClaro, java.nio.charset.StandardCharsets.UTF_8);
        } catch (AEADBadTagException e) {
            throw new IllegalStateException("Secret de MFA corrompido ou chave incorreta", e);
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Falha ao decifrar secret de MFA", e);
        }
    }

    private SecretKeySpec chave() {
        if (keySpec == null) {
            if (secretBase64 == null || secretBase64.isBlank()) {
                throw new IllegalStateException("MFA_SECRET_ENCRYPTION_KEY não configurada");
            }
            keySpec = new SecretKeySpec(Base64.getDecoder().decode(secretBase64), "AES");
        }
        return keySpec;
    }
}
