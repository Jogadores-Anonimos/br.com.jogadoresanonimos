package br.com.jogadoresanonimos.common.dto;

import java.util.List;

public record ApiError(String code, String message, List<FieldError> fields, String traceId) {

    public record FieldError(String campo, String mensagem) {
    }

    public static ApiError of(String code, String message, String traceId) {
        return new ApiError(code, message, null, traceId);
    }

    public static ApiError of(String code, String message, List<FieldError> fields, String traceId) {
        return new ApiError(code, message, fields, traceId);
    }
}
