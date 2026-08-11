package br.com.jogadoresanonimos.config;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.boot.EnvironmentPostProcessor;
import org.springframework.boot.SpringApplication;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

public class DockerSecretsEnvironmentPostProcessor implements EnvironmentPostProcessor, Ordered {

    private static final List<String> SECRET_NAMES = List.of(
            "DB_PASSWORD",
            "JWT_SECRET",
            "LOG_HMAC_SECRET",
            "MFA_SECRET_ENCRYPTION_KEY",
            "RECAPTCHA_SECRET_KEY",
            "RESEND_API_KEY",
            "ADMIN_BOOTSTRAP_SENHA");

    private static final Path DOCKER_SECRETS_DIR = Path.of("/run/secrets");

    private static final String PROPERTY_SOURCE_NAME = "dockerSecrets";

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        Map<String, Object> resolvedSecrets = new LinkedHashMap<>();
        for (String secretName : SECRET_NAMES) {
            Path secretFile = DOCKER_SECRETS_DIR.resolve(toSnakeCaseFileName(secretName));
            if (!Files.isRegularFile(secretFile)) {
                continue;
            }
            resolvedSecrets.put(secretName, readSecretFile(secretName, secretFile));
        }
        if (!resolvedSecrets.isEmpty()) {
            environment.getPropertySources()
                    .addFirst(new MapPropertySource(PROPERTY_SOURCE_NAME, resolvedSecrets));
        }
    }

    private String toSnakeCaseFileName(String secretName) {
        return secretName.toLowerCase();
    }

    private String readSecretFile(String secretName, Path secretFile) {
        try {
            return Files.readString(secretFile, StandardCharsets.UTF_8).trim();
        } catch (IOException e) {
            throw new IllegalStateException(
                    "Falha ao ler o arquivo de secret para " + secretName + " em " + secretFile, e);
        }
    }
}
