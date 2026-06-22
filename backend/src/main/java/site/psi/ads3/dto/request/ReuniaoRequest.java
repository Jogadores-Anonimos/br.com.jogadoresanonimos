package site.psi.ads3.dto.request;
import java.time.LocalDateTime;
import site.psi.ads3.entity.Reuniao;
import jakarta.validation.constraints.NotNull;

public record ReuniaoRequest(
        @NotNull(message="Titulo obrigatorio")
        String titulo,
        String descricao,

        @NotNull(message="Endereço obrigatorio")
        String endereco,

        @NotNull(message="Cidade obrigatorio")
        Long idCidade,

        @NotNull(message="Data e Hora obrigatorio")
        LocalDateTime dataHora
) {
    public void updateReuniao(Reuniao reuniao) {
        if (titulo != null) reuniao.setTitulo(titulo);
        if (descricao != null) reuniao.setDescricao(descricao);
        if (endereco != null) reuniao.setEndereco(endereco);
        if (dataHora != null) reuniao.setDataHora(dataHora);
    }
}
