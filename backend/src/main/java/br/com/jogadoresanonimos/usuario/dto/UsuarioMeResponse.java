package br.com.jogadoresanonimos.usuario.dto;

import br.com.jogadoresanonimos.usuario.entity.Role;
import br.com.jogadoresanonimos.usuario.entity.Usuario;

import java.time.Instant;
import java.util.UUID;

public record UsuarioMeResponse(UUID id, String apelido, String email, Role role, Instant criadoEm) {

    public static UsuarioMeResponse from(Usuario usuario) {
        return new UsuarioMeResponse(usuario.getId(), usuario.getApelido(), usuario.getEmail(),
                usuario.getRole(), usuario.getCriadoEm());
    }
}
