package br.com.jogadoresanonimos.common.dto;

import java.util.List;
import java.util.Map;

public record ApiError(String code, String message, List<FieldError> fields, Map<String, Object> details, String traceId) {

    public record FieldError(String campo, String mensagem) {
    }

    public static ApiError of(String code, String message, String traceId) {
        return new ApiError(code, message, null, null, traceId);
    }

    public static ApiError of(String code, String message, List<FieldError> fields, String traceId) {
        return new ApiError(code, message, fields, null, traceId);
    }

    public static ApiError of(String code, String message, Map<String, Object> details, List<FieldError> fields, String traceId) {
        return new ApiError(code, message, fields, details, traceId);
    }
}
