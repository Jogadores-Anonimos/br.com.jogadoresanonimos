package br.com.jogadoresanonimos.security;

import java.time.Instant;
import java.util.UUID;

public record AuthenticatedUser(UUID id, String apelido, String role, String jti, Instant expiraEm) {

    public AuthenticatedUser(UUID id, String apelido, String role) {
        this(id, apelido, role, null, null);
    }
}
