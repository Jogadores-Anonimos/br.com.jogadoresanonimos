package br.com.jogadoresanonimos.common.exception;

import br.com.jogadoresanonimos.common.dto.ApiError;

import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Map;

public class ApiException extends RuntimeException {

    private final HttpStatus status;
    private final String code;
    private final List<ApiError.FieldError> fields;
    private final Map<String, Object> details;

    public ApiException(HttpStatus status, String code, String message) {
        this(status, code, message, null, null);
    }

    public ApiException(HttpStatus status, String code, String message, List<ApiError.FieldError> fields) {
        this(status, code, message, fields, null);
    }

    public ApiException(HttpStatus status, String code, String message, List<ApiError.FieldError> fields,
                         Map<String, Object> details) {
        super(message);
        this.status = status;
        this.code = code;
        this.fields = fields;
        this.details = details;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getCode() {
        return code;
    }

    public List<ApiError.FieldError> getFields() {
        return fields;
    }

    public Map<String, Object> getDetails() {
        return details;
    }
}
