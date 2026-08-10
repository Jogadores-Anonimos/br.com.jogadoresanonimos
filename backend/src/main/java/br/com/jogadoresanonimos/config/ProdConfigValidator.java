package br.com.jogadoresanonimos.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("prod")
public class ProdConfigValidator implements ApplicationRunner {

    private static final String RECAPTCHA_TEST_SECRET_KEY = "6LeIxAcTAAAAAGG-vFI1TnRWxMZNFuojJ4WifJWe";
    private static final String CORS_ALLOWED_ORIGINS_DEFAULT = "http://localhost:4200";
    private static final String FRONTEND_URL_DEFAULT = "http://localhost:4200";

    private final String recaptchaSecretKey;
    private final String resendApiKey;
    private final String mfaSecretEncryptionKey;
    private final String logHmacSecret;
    private final String corsAllowedOrigins;
    private final String frontendUrl;

    public ProdConfigValidator(
            @Value("${app.recaptcha.secret-key}") String recaptchaSecretKey,
            @Value("${app.email.resend-api-key}") String resendApiKey,
            @Value("${app.mfa.secret-encryption-key}") String mfaSecretEncryptionKey,
            @Value("${app.log.hmac-secret}") String logHmacSecret,
            @Value("${app.cors.allowed-origins}") String corsAllowedOrigins,
            @Value("${app.frontend-url}") String frontendUrl) {
        this.recaptchaSecretKey = recaptchaSecretKey;
        this.resendApiKey = resendApiKey;
        this.mfaSecretEncryptionKey = mfaSecretEncryptionKey;
        this.logHmacSecret = logHmacSecret;
        this.corsAllowedOrigins = corsAllowedOrigins;
        this.frontendUrl = frontendUrl;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (recaptchaSecretKey == null
                || recaptchaSecretKey.isBlank()
                || recaptchaSecretKey.equals(RECAPTCHA_TEST_SECRET_KEY)) {
            throw new IllegalStateException(
                    "RECAPTCHA_SECRET_KEY não configurada com uma chave real de produção");
        }
        if (resendApiKey == null || resendApiKey.isBlank()) {
            throw new IllegalStateException(
                    "RESEND_API_KEY não configurada para o profile prod");
        }
        if (mfaSecretEncryptionKey == null || mfaSecretEncryptionKey.isBlank()) {
            throw new IllegalStateException(
                    "MFA_SECRET_ENCRYPTION_KEY não configurada para o profile prod");
        }
        if (logHmacSecret == null || logHmacSecret.isBlank()) {
            throw new IllegalStateException(
                    "LOG_HMAC_SECRET não configurada para o profile prod");
        }
        if (corsAllowedOrigins == null
                || corsAllowedOrigins.isBlank()
                || corsAllowedOrigins.equals(CORS_ALLOWED_ORIGINS_DEFAULT)) {
            throw new IllegalStateException(
                    "CORS_ALLOWED_ORIGINS não configurada com uma origem real de produção");
        }
        if (frontendUrl == null
                || frontendUrl.isBlank()
                || frontendUrl.equals(FRONTEND_URL_DEFAULT)) {
            throw new IllegalStateException(
                    "FRONTEND_URL não configurada com uma origem real de produção");
        }
    }
}
