package br.com.jogadoresanonimos.auth.dto;

import java.time.Instant;

public record LoginResponse(String token, Instant expiraEm, UsuarioResumo usuario) {

    public record UsuarioResumo(String id, String apelido, String role) {
    }
}
