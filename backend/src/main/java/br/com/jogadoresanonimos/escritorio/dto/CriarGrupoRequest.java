package br.com.jogadoresanonimos.escritorio.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

public record CriarGrupoRequest(
        @NotBlank @Size(max = 200) String nome,
        @NotBlank @Size(max = 300) String endereco,
        @Size(max = 150) String bairro,
        @NotBlank @Size(max = 150) String cidade,
        @NotEmpty @Valid List<AgendaItemRequest> agenda,
        @Valid List<GrupoTelefoneRequest> telefones
) {
}
