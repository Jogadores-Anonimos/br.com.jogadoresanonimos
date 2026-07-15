package br.com.jogadoresanonimos.auth.dto;

import java.time.Instant;

public record LoginResponse(boolean mfaRequerido, String token, Instant expiraEm, UsuarioResumo usuario) {

    public static LoginResponse comDesafioMfa() {
        return new LoginResponse(true, null, null, null);
    }

    public static LoginResponse autenticado(String token, Instant expiraEm, UsuarioResumo usuario) {
        return new LoginResponse(false, token, expiraEm, usuario);
    }

    public record UsuarioResumo(String id, String apelido, String role, boolean deveTrocarSenha) {
    }
}
