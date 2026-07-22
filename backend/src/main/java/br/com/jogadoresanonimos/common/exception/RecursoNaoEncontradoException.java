package br.com.jogadoresanonimos.common.exception;

import org.springframework.http.HttpStatus;

public class RecursoNaoEncontradoException extends ApiException {

    public RecursoNaoEncontradoException(String code, String message) {
        super(HttpStatus.NOT_FOUND, code, message);
    }
}
