package br.com.jogadoresanonimos.reuniao.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.UUID;

public record CriarReuniaoOnlineRequest(
        @NotBlank @Size(max = 200) String nome,
        @Size(max = 20) @Pattern(regexp = "^$|^(\\+?55)?\\s?\\(?\\d{2}\\)?[\\s-]?\\d{4,5}[\\s-]?\\d{4}$", message = "Telefone deve ser um número de telefone brasileiro válido") String telefone,
        @Size(max = 2000) String descricao,
        @Size(max = 500) String videoUrl,
        UUID panfletoImagemId,
        @NotEmpty @Size(max = 336) List<@Valid ReuniaoOnlineAgendaItemRequest> agenda
) {
}
