package br.com.jogadoresanonimos.escritorio.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.time.Instant;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.DecimalMax;

public record EditarGrupoRequest(
        @NotBlank @Size(max = 200) String nome,
        @NotBlank @Size(max = 300) String endereco,
        @Size(max = 100) String bairro,
        @NotBlank @Size(max = 100) String cidade,
        @NotEmpty @Size(max = 336) @Valid List<AgendaItemRequest> agenda,
        @Valid List<GrupoTelefoneRequest> telefones,
        @Size(max = 9) @Pattern(regexp = "^$|^\\d{5}-?\\d{3}$") String cep,
        @Size(max = 20) String numero,
        @Size(max = 200) String logradouro,
        @Size(max = 2) @Pattern(regexp = "^$|^[A-Za-z]{2}$") String estado,
        @DecimalMin("-90.0") @DecimalMax("90.0") Double latitude,
        @DecimalMin("-180.0") @DecimalMax("180.0") Double longitude,
        Instant atualizadoEm
) {
    public EditarGrupoRequest(String nome, String endereco, String bairro, String cidade,
                              List<AgendaItemRequest> agenda, List<GrupoTelefoneRequest> telefones) {
        this(nome, endereco, bairro, cidade, agenda, telefones,
                null, null, null, null, null, null, null);
    }

    public EditarGrupoRequest(String nome, String endereco, String bairro, String cidade,
                              List<AgendaItemRequest> agenda, List<GrupoTelefoneRequest> telefones,
                              Instant atualizadoEm) {
        this(nome, endereco, bairro, cidade, agenda, telefones,
                null, null, null, null, null, null, atualizadoEm);
    }
}
