package br.com.jogadoresanonimos.escritorio.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.DecimalMax;

import java.util.List;

public record CriarEnderecoGrupoRequest(
        @NotBlank @Size(max = 2) String uf,
        @NotBlank @Size(max = 200) String nome,
        @NotBlank @Size(max = 300) String endereco,
        @Size(max = 100) String bairro,
        @NotBlank @Size(max = 100) String cidade,
        @NotEmpty @Size(max = 336) @Valid List<AgendaItemRequest> agenda,
        @Valid List<GrupoTelefoneRequest> telefones,
        @Size(max = 9) @Pattern(regexp = "^$|^\\d{5}-?\\d{3}$") String cep,
        @Size(max = 20) String numero,
        @Size(max = 200) String logradouro,
        @DecimalMin("-90.0") @DecimalMax("90.0") Double latitude,
        @DecimalMin("-180.0") @DecimalMax("180.0") Double longitude
) {
    public CriarEnderecoGrupoRequest(String uf, String nome, String endereco, String bairro, String cidade,
                                      List<AgendaItemRequest> agenda, List<GrupoTelefoneRequest> telefones) {
        this(uf, nome, endereco, bairro, cidade, agenda, telefones, null, null, null, null, null);
    }
}
