package br.com.jogadoresanonimos.security;

import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.util.regex.Pattern;

@Service
public class TotpService {

    private static final String ALGORITMO_HMAC = "HmacSHA1";
    private static final int SECRET_LENGTH_BYTES = 20;
    private static final long PERIODO_SEGUNDOS = 30;
    private static final int DIGITOS = 6;
    private static final int JANELA_TOLERANCIA_STEPS = 1;
    private static final Pattern CODIGO_VALIDO = Pattern.compile("^[0-9]{6}$");
    private static final String BASE32_ALFABETO = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567";

    private final SecureRandom secureRandom = new SecureRandom();

    public String gerarSecretBase32() {
        byte[] bytes = new byte[SECRET_LENGTH_BYTES];
        secureRandom.nextBytes(bytes);
        return codificarBase32(bytes);
    }

    public boolean validar(String secretBase32, String codigo) {
        if (codigo == null || !CODIGO_VALIDO.matcher(codigo).matches()) {
            return false;
        }
        byte[] chave = decodificarBase32(secretBase32);
        long stepAtual = System.currentTimeMillis() / 1000 / PERIODO_SEGUNDOS;
        for (long delta = -JANELA_TOLERANCIA_STEPS; delta <= JANELA_TOLERANCIA_STEPS; delta++) {
            String codigoEsperado = gerarCodigo(chave, stepAtual + delta);
            if (constantTimeEquals(codigoEsperado, codigo)) {
                return true;
            }
        }
        return false;
    }

    public String construirOtpauthUri(String issuer, String apelido, String secretBase32) {
        String label = issuer + ":" + apelido;
        return "otpauth://totp/" + urlEncode(label)
                + "?secret=" + secretBase32
                + "&issuer=" + urlEncode(issuer)
                + "&algorithm=SHA1&digits=" + DIGITOS + "&period=" + PERIODO_SEGUNDOS;
    }

    private String gerarCodigo(byte[] chave, long step) {
        try {
            ByteBuffer buffer = ByteBuffer.allocate(8);
            buffer.putLong(step);
            Mac mac = Mac.getInstance(ALGORITMO_HMAC);
            mac.init(new SecretKeySpec(chave, ALGORITMO_HMAC));
            byte[] hash = mac.doFinal(buffer.array());
            int offset = hash[hash.length - 1] & 0x0F;
            int binario = ((hash[offset] & 0x7F) << 24)
                    | ((hash[offset + 1] & 0xFF) << 16)
                    | ((hash[offset + 2] & 0xFF) << 8)
                    | (hash[offset + 3] & 0xFF);
            int codigo = binario % (int) Math.pow(10, DIGITOS);
            return String.format("%0" + DIGITOS + "d", codigo);
        } catch (Exception e) {
            throw new IllegalStateException("Falha ao calcular código TOTP", e);
        }
    }

    private boolean constantTimeEquals(String a, String b) {
        if (a.length() != b.length()) {
            return false;
        }
        int resultado = 0;
        for (int i = 0; i < a.length(); i++) {
            resultado |= a.charAt(i) ^ b.charAt(i);
        }
        return resultado == 0;
    }

    private String urlEncode(String valor) {
        return java.net.URLEncoder.encode(valor, java.nio.charset.StandardCharsets.UTF_8).replace("+", "%20");
    }

    private String codificarBase32(byte[] dados) {
        StringBuilder resultado = new StringBuilder();
        int buffer = 0;
        int bitsNoBuffer = 0;
        for (byte b : dados) {
            buffer = (buffer << 8) | (b & 0xFF);
            bitsNoBuffer += 8;
            while (bitsNoBuffer >= 5) {
                int indice = (buffer >> (bitsNoBuffer - 5)) & 0x1F;
                resultado.append(BASE32_ALFABETO.charAt(indice));
                bitsNoBuffer -= 5;
            }
        }
        if (bitsNoBuffer > 0) {
            int indice = (buffer << (5 - bitsNoBuffer)) & 0x1F;
            resultado.append(BASE32_ALFABETO.charAt(indice));
        }
        return resultado.toString();
    }

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
}
