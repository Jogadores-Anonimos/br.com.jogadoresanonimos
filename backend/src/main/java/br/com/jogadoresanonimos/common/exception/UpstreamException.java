package br.com.jogadoresanonimos.common.exception;

import org.springframework.http.HttpStatus;

public class UpstreamException extends ApiException {

    public UpstreamException(String message) {
        super(HttpStatus.BAD_GATEWAY, ErrorCode.UPSTREAM_ERROR, message);
    }
}
