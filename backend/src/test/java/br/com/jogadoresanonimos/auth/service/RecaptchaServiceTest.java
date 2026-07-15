package br.com.jogadoresanonimos.auth.service;

import br.com.jogadoresanonimos.auth.dto.RecaptchaVerifyResponse;
import br.com.jogadoresanonimos.common.exception.UpstreamException;
import br.com.jogadoresanonimos.common.exception.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RecaptchaServiceTest {

    private static final String VERIFY_URL = "https://www.google.com/recaptcha/api/siteverify";

    @Mock
    RestTemplate restTemplate;

    RecaptchaService recaptchaService;

    @BeforeEach
    void setUp() {
        recaptchaService = new RecaptchaService(restTemplate, "secret-de-teste");
    }

    @Test
    void validar_naoLancaQuandoSuccessTrue() {
        when(restTemplate.postForObject(eq(VERIFY_URL), any(HttpEntity.class), any(Class.class)))
                .thenReturn(respostaSucesso(true));
        assertThatCode(() -> recaptchaService.validar("token-valido")).doesNotThrowAnyException();
    }

    @Test
    void validar_lancaValidationExceptionQuandoSuccessFalse() {
        when(restTemplate.postForObject(eq(VERIFY_URL), any(HttpEntity.class), any(Class.class)))
                .thenReturn(respostaSucesso(false));
        assertThatThrownBy(() -> recaptchaService.validar("token-invalido"))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    void validar_lancaValidationExceptionQuandoRespostaNula() {
        when(restTemplate.postForObject(eq(VERIFY_URL), any(HttpEntity.class), any(Class.class)))
                .thenReturn(null);
        assertThatThrownBy(() -> recaptchaService.validar("token"))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    void validar_lancaUpstreamExceptionQuandoFalhaDeComunicacao() {
        when(restTemplate.postForObject(eq(VERIFY_URL), any(HttpEntity.class), any(Class.class)))
                .thenThrow(new ResourceAccessException("timeout"));
        assertThatThrownBy(() -> recaptchaService.validar("token"))
                .isInstanceOf(UpstreamException.class);
    }

    @Test
    void validar_nuncaEnviaRemoteip() {
        when(restTemplate.postForObject(eq(VERIFY_URL), any(HttpEntity.class), any(Class.class)))
                .thenAnswer(inv -> {
                    HttpEntity<?> entity = inv.getArgument(1);
                    Object corpo = entity.getBody();
                    assertThatCode(() -> {
                        if (corpo instanceof org.springframework.util.MultiValueMap<?, ?> map) {
                            if (map.containsKey("remoteip")) {
                                throw new AssertionError("remoteip não deveria ser enviado ao Google");
                            }
                        }
                    }).doesNotThrowAnyException();
                    return respostaSucesso(true);
                });
        recaptchaService.validar("token-valido");
    }

    private RecaptchaVerifyResponse respostaSucesso(boolean success) {
        return new RecaptchaVerifyResponse(success);
    }
}
