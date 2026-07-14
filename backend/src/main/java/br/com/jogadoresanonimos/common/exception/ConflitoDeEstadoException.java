package br.com.jogadoresanonimos.common.exception;

import org.springframework.http.HttpStatus;

public class ConflitoDeEstadoException extends ApiException {

    public ConflitoDeEstadoException(String message) {
        super(HttpStatus.CONFLICT, ErrorCode.CONFLICT, message);
    }
}
