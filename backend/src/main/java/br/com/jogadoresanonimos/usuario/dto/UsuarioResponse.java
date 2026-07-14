package br.com.jogadoresanonimos.usuario.dto;

import br.com.jogadoresanonimos.usuario.entity.Role;
import br.com.jogadoresanonimos.usuario.entity.Usuario;

import java.time.Instant;
import java.util.UUID;

public record UsuarioResponse(UUID id, String apelido, Role role, Instant criadoEm) {

    public static UsuarioResponse from(Usuario usuario) {
        return new UsuarioResponse(usuario.getId(), usuario.getApelido(), usuario.getRole(), usuario.getCriadoEm());
    }
}
