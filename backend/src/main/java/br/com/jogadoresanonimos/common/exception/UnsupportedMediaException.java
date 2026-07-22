package br.com.jogadoresanonimos.common.exception;

import org.springframework.http.HttpStatus;

public class UnsupportedMediaException extends ApiException {

    public UnsupportedMediaException(String code, String message) {
        super(HttpStatus.UNSUPPORTED_MEDIA_TYPE, code, message);
    }
}
