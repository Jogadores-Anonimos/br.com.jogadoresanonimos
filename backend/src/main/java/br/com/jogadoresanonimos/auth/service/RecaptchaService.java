package br.com.jogadoresanonimos.auth.service;

import br.com.jogadoresanonimos.auth.dto.RecaptchaVerifyResponse;
import br.com.jogadoresanonimos.common.dto.ApiError;
import br.com.jogadoresanonimos.common.exception.UpstreamException;
import br.com.jogadoresanonimos.common.exception.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class RecaptchaService {

    private static final Logger log = LoggerFactory.getLogger(RecaptchaService.class);
    private static final String VERIFY_URL = "https://www.google.com/recaptcha/api/siteverify";

    private final RestTemplate restTemplate;
    private final String secretKey;

    public RecaptchaService(RestTemplate restTemplate, @Value("${app.recaptcha.secret-key}") String secretKey) {
        this.restTemplate = restTemplate;
        this.secretKey = secretKey;
    }

    public void validar(String token) {
        RecaptchaVerifyResponse resposta;
        try {
            MultiValueMap<String, String> corpo = new LinkedMultiValueMap<>();
            corpo.add("secret", secretKey);
            corpo.add("response", token);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(corpo, headers);
            resposta = restTemplate.postForObject(VERIFY_URL, request, RecaptchaVerifyResponse.class);
        } catch (RestClientException e) {
            log.warn("recaptcha_upstream_falha erro={}", e.getClass().getSimpleName());
            throw new UpstreamException("Não foi possível validar o reCAPTCHA no momento, tente novamente");
        }
        if (resposta == null || !resposta.success()) {
            throw new ValidationException("Falha na verificação do reCAPTCHA",
                    List.of(new ApiError.FieldError("recaptchaToken", "Verificação do reCAPTCHA inválida ou expirada")));
        }
    }
}
