package br.com.jogadoresanonimos.common.exception;

import org.springframework.http.HttpStatus;

public class UpstreamException extends ApiException {

    public UpstreamException(String code, String message) {
        super(HttpStatus.BAD_GATEWAY, code, message);
    }
}
