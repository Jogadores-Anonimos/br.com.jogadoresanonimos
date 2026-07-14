package br.com.jogadoresanonimos.usuario.dto;

import br.com.jogadoresanonimos.usuario.entity.Role;
import br.com.jogadoresanonimos.usuario.entity.Usuario;

import java.time.Instant;
import java.util.UUID;

public record UsuarioAdminResponse(UUID id, String apelido, String email, Role role, Instant criadoEm) {

    public static UsuarioAdminResponse from(Usuario usuario) {
        return new UsuarioAdminResponse(usuario.getId(), usuario.getApelido(), usuario.getEmail(),
                usuario.getRole(), usuario.getCriadoEm());
    }
}
