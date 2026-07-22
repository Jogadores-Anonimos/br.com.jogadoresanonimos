package br.com.jogadoresanonimos.common.exception;

import org.springframework.http.HttpStatus;

public class ConflitoDeRegiaoHotlineException extends ApiException {

    public ConflitoDeRegiaoHotlineException(String message) {
        super(HttpStatus.CONFLICT, ErrorCode.HOTLINE_REGIAO_EM_USO, message);
    }
}
