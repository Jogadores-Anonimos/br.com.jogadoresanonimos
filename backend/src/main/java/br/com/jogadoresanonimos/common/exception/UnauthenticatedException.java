package br.com.jogadoresanonimos.common.exception;

import org.springframework.http.HttpStatus;

public class UnauthenticatedException extends ApiException {

    public UnauthenticatedException(String code, String message) {
        super(HttpStatus.UNAUTHORIZED, code, message);
    }
}
