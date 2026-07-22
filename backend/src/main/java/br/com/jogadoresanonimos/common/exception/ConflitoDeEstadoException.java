package br.com.jogadoresanonimos.common.exception;

import br.com.jogadoresanonimos.common.dto.ApiError;

import org.springframework.http.HttpStatus;

import java.util.List;

public class ConflitoDeEstadoException extends ApiException {

    public ConflitoDeEstadoException(String code, String message) {
        super(HttpStatus.CONFLICT, code, message);
    }

    public ConflitoDeEstadoException(String code, String message, List<ApiError.FieldError> fields) {
        super(HttpStatus.CONFLICT, code, message, fields);
    }
}
