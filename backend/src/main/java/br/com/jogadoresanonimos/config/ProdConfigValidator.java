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

    private final String recaptchaSecretKey;

    public ProdConfigValidator(@Value("${app.recaptcha.secret-key}") String recaptchaSecretKey) {
        this.recaptchaSecretKey = recaptchaSecretKey;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (recaptchaSecretKey == null
                || recaptchaSecretKey.isBlank()
                || recaptchaSecretKey.equals(RECAPTCHA_TEST_SECRET_KEY)) {
            throw new IllegalStateException(
                    "RECAPTCHA_SECRET_KEY não configurada com uma chave real de produção");
        }
    }
}
