package br.com.jogadoresanonimos.usuario.dto;

import br.com.jogadoresanonimos.usuario.entity.Role;
import jakarta.validation.constraints.NotNull;

public record EditarRoleRequest(@NotNull Role role) {
}
