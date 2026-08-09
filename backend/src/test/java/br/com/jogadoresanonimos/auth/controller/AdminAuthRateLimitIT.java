package br.com.jogadoresanonimos.auth.controller;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("qa-integration")
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class AdminAuthRateLimitIT {

    @LocalServerPort
    int port;

    private final RestTemplate restTemplate = new RestTemplate();

    private ResponseEntity<String> post(String path, String corpoJson) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        try {
            return restTemplate.postForEntity("http://localhost:" + port + path, new HttpEntity<>(corpoJson, headers), String.class);
        } catch (HttpStatusCodeException ex) {
            return ResponseEntity.status(ex.getStatusCode()).body(ex.getResponseBodyAsString());
        }
    }

    @Test
    void esqueciSenha_excessoDeRequisicoesParaMesmoIdentificadorDeveRetornar429() {
        String identificador = "qa-rate-limit-esqueci-" + UUID.randomUUID();
        String body = "{\"identificador\":\"" + identificador + "\"}";
        for (int i = 0; i < 5; i++) {
            ResponseEntity<String> resposta = post("/api/v1/admin/auth/esqueci-senha", body);
            assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.OK);
        }
        ResponseEntity<String> excedente = post("/api/v1/admin/auth/esqueci-senha", body);
        assertThat(excedente.getStatusCode()).isEqualTo(HttpStatus.TOO_MANY_REQUESTS);
        assertThat(excedente.getBody()).contains("\"success\":false").contains("AUTH_RATE_LIMITED");
    }

    @Test
    void redefinirSenha_excessoDeRequisicoesParaMesmoCodigoDeveRetornar429() {
        String body = "{\"codigo\":\"777777\",\"novaSenha\":\"senhaQualquerValida1\",\"confirmarNovaSenha\":\"senhaQualquerValida1\"}";
        for (int i = 0; i < 5; i++) {
            ResponseEntity<String> resposta = post("/api/v1/admin/auth/redefinir-senha", body);
            assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        }
        ResponseEntity<String> excedente = post("/api/v1/admin/auth/redefinir-senha", body);
        assertThat(excedente.getStatusCode()).isEqualTo(HttpStatus.TOO_MANY_REQUESTS);
        assertThat(excedente.getBody()).contains("\"success\":false").contains("AUTH_RATE_LIMITED");
    }
}
