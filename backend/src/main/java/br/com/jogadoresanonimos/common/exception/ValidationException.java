package br.com.jogadoresanonimos.common.exception;

import br.com.jogadoresanonimos.common.dto.ApiError;

import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Map;

public class ValidationException extends ApiException {

    public ValidationException(String code, String message) {
        this(code, message, null, null);
    }

    public ValidationException(String code, String message, List<ApiError.FieldError> fields) {
        this(code, message, fields, null);
    }

    public ValidationException(String code, String message, List<ApiError.FieldError> fields, Map<String, Object> details) {
        super(HttpStatus.BAD_REQUEST, code, message, fields, details);
    }
}
