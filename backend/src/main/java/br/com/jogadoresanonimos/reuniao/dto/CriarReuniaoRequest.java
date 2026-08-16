package br.com.jogadoresanonimos.reuniao.dto;

import br.com.jogadoresanonimos.reuniao.entity.CategoriaReuniao;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CriarReuniaoRequest(
        @NotBlank @Size(max = 200) String nome,
        @NotNull CategoriaReuniao categoria,
        @NotBlank @Size(max = 2000) String descricao,
        @NotBlank @Size(max = 100) String dia,
        @NotBlank @Size(max = 50) String horario,
        @Size(max = 300) String local,
        @Size(max = 20) @Pattern(regexp = "^$|^(\\+?55)?\\s?\\(?\\d{2}\\)?[\\s-]?\\d{4,5}[\\s-]?\\d{4}$", message = "WhatsApp deve ser um número de telefone brasileiro válido") String whatsapp,
        @Size(max = 20) String diaSemana,
        @Size(max = 20) String periodo,
        @DecimalMin("-90.0") @DecimalMax("90.0") Double latitude,
        @DecimalMin("-180.0") @DecimalMax("180.0") Double longitude,
        @Size(max = 9) @Pattern(regexp = "^$|^\\d{5}-?\\d{3}$", message = "CEP deve conter 8 dígitos") String cep,
        @Size(max = 20) String numero,
        @Size(max = 200) String logradouro,
        @Size(max = 100) String bairro,
        @Size(max = 100) String cidade,
        @Size(max = 2) @Pattern(regexp = "^$|^[A-Za-z]{2}$", message = "Estado deve ser a sigla da UF com 2 letras") String estado,
        @Size(max = 500) String linkReuniao
) {
}
