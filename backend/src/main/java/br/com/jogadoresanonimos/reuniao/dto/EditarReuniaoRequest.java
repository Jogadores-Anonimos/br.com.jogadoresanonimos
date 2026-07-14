package br.com.jogadoresanonimos.reuniao.dto;

import br.com.jogadoresanonimos.reuniao.entity.CategoriaReuniao;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record EditarReuniaoRequest(
        @NotBlank @Size(max = 200) String nome,
        @NotNull CategoriaReuniao categoria,
        @NotBlank @Size(max = 2000) String descricao,
        @NotBlank @Size(max = 100) String dia,
        @NotBlank @Size(max = 50) String horario,
        @NotBlank @Size(max = 300) String local,
        @Size(max = 20) String whatsapp,
        @Size(max = 20) String diaSemana,
        @Size(max = 20) String periodo,
        @DecimalMin("-90.0") @DecimalMax("90.0") Double latitude,
        @DecimalMin("-180.0") @DecimalMax("180.0") Double longitude
) {
}
