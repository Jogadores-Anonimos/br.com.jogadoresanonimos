package br.com.jogadoresanonimos.common.exception;

import org.springframework.http.HttpStatus;

import java.util.Map;

public class RateLimitedException extends ApiException {

    private final long retryAfterSeconds;

    public RateLimitedException(String code, String message, long retryAfterSeconds) {
        super(HttpStatus.TOO_MANY_REQUESTS, code, message, null, Map.of("retryAfterSeconds", retryAfterSeconds));
        this.retryAfterSeconds = retryAfterSeconds;
    }

    public long getRetryAfterSeconds() {
        return retryAfterSeconds;
    }
}
