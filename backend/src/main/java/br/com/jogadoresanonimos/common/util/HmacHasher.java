package br.com.jogadoresanonimos.common.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.InvalidKeyException;
import java.util.Base64;

/**
 * HMAC não-reversível para correlacionar tentativas de login/reset em log
 * sem expor apelido/e-mail em texto puro (ver seção 10 do doc de decisões).
 * Chave exata/rotação a confirmar com security.
 */
@Component
public class HmacHasher {

    private final SecretKeySpec keySpec;

    public HmacHasher(@Value("${app.log.hmac-secret}") String secret) {
        this.keySpec = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
    }

    public String hash(String value) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(keySpec);
            byte[] result = mac.doFinal(value.trim().toLowerCase().getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(result);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new IllegalStateException("Falha ao calcular HMAC", e);
        }
    }
}
