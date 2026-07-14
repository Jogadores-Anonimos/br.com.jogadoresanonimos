package br.com.jogadoresanonimos.common.exception;

import br.com.jogadoresanonimos.common.dto.ApiError;
import org.springframework.http.HttpStatus;

import java.util.List;

public class ValidationException extends ApiException {

    private final List<ApiError.FieldError> fields;

    public ValidationException(String message) {
        this(message, null);
    }

    public ValidationException(String message, List<ApiError.FieldError> fields) {
        super(HttpStatus.BAD_REQUEST, ErrorCode.VALIDATION_ERROR, message);
        this.fields = fields;
    }

    public List<ApiError.FieldError> getFields() {
        return fields;
    }
}
