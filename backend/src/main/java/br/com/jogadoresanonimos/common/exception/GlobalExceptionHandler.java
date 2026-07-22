package br.com.jogadoresanonimos.common.exception;

import br.com.jogadoresanonimos.common.dto.ApiError;
import br.com.jogadoresanonimos.common.dto.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.catalina.connector.ClientAbortException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiResponse<Object>> handleApiException(ApiException ex, HttpServletRequest request) {
        String traceId = traceId();
        ApiError error = ApiError.of(ex.getCode(), ex.getMessage(), ex.getDetails(), ex.getFields(), traceId);
        HttpHeaders headers = new HttpHeaders();
        if (ex instanceof RateLimitedException rle) {
            headers.add(HttpHeaders.RETRY_AFTER, String.valueOf(rle.getRetryAfterSeconds()));
        }
        logOperational(ex.getStatus(), request, traceId, null);
        return ResponseEntity.status(ex.getStatus()).headers(headers).body(ApiResponse.fail(error));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        String traceId = traceId();
        List<ApiError.FieldError> fields = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> new ApiError.FieldError(fe.getField(), fe.getDefaultMessage()))
                .toList();
        ApiError error = ApiError.of(ErrorCode.VALIDATION_ERROR, "Dados informados são inválidos", fields, traceId);
        logOperational(HttpStatus.BAD_REQUEST, request, traceId, null);
        return ResponseEntity.badRequest().body(ApiResponse.fail(error));
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ApiResponse<Object>> handleUploadTooLarge(MaxUploadSizeExceededException ex, HttpServletRequest request) {
        String traceId = traceId();
        ApiError error = ApiError.of(ErrorCode.UPLOAD_TOO_LARGE, "Arquivo excede o tamanho máximo permitido", traceId);
        logOperational(HttpStatus.BAD_REQUEST, request, traceId, null);
        return ResponseEntity.badRequest().body(ApiResponse.fail(error));
    }

    @ExceptionHandler({BadCredentialsException.class})
    public ResponseEntity<ApiResponse<Object>> handleBadCredentials(Exception ex, HttpServletRequest request) {
        String traceId = traceId();
        ApiError error = ApiError.of(ErrorCode.AUTH_INVALID_CREDENTIALS, "Identificador ou senha inválidos", traceId);
        logOperational(HttpStatus.UNAUTHORIZED, request, traceId, null);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.fail(error));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Object>> handleAccessDenied(AccessDeniedException ex, HttpServletRequest request) {
        String traceId = traceId();
        ApiError error = ApiError.of(ErrorCode.FORBIDDEN, "Acesso negado", traceId);
        logOperational(HttpStatus.FORBIDDEN, request, traceId, null);
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ApiResponse.fail(error));
    }

    @ExceptionHandler(ClientAbortException.class)
    public ResponseEntity<Void> handleClientAbort(ClientAbortException ex, HttpServletRequest request) {
        if (log.isDebugEnabled()) {
            log.debug("method={} path={} clientDisconnected=true", request.getMethod(), request.getRequestURI());
        }
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleUnexpected(Exception ex, HttpServletRequest request) {
        if (isBrokenPipe(ex)) {
            if (log.isDebugEnabled()) {
                log.debug("method={} path={} clientDisconnected=true", request.getMethod(), request.getRequestURI());
            }
            return ResponseEntity.status(HttpStatus.OK).build();
        }
        String traceId = traceId();
        ApiError error = ApiError.of(ErrorCode.INTERNAL_ERROR, "Ocorreu um erro, tente novamente", traceId);
        logOperational(HttpStatus.INTERNAL_SERVER_ERROR, request, traceId, ex);
        return ResponseEntity.internalServerError().body(ApiResponse.fail(error));
    }

    private boolean isBrokenPipe(Throwable ex) {
        Throwable current = ex;
        while (current != null) {
            if (current instanceof ClientAbortException) {
                return true;
            }
            if (current instanceof IOException && current.getMessage() != null
                    && current.getMessage().toLowerCase().contains("broken pipe")) {
                return true;
            }
            current = current.getCause();
        }
        return false;
    }

    private String traceId() {
        return UUID.randomUUID().toString();
    }

    private void logOperational(HttpStatus status, HttpServletRequest request, String traceId, Exception unexpected) {
        if (unexpected != null) {
            log.error("method={} path={} status={} traceId={}", request.getMethod(), request.getRequestURI(),
                    status.value(), traceId, unexpected);
        } else {
            log.warn("method={} path={} status={} traceId={}", request.getMethod(), request.getRequestURI(),
                    status.value(), traceId);
        }
    }
}
