package br.com.jogadoresanonimos.security;

import java.util.UUID;

public record AuthenticatedUser(UUID id, String apelido, String role) {
}
