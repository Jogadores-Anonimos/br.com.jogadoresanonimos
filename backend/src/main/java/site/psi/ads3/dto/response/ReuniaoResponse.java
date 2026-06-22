package site.psi.ads3.dto.response;
import java.time.LocalDateTime;
import site.psi.ads3.entity.Reuniao;

public record ReuniaoResponse(
        Long id,
        String titulo,
        String descricao,
        String endereco,
        Long idCidade,
        String nomeCidade,
        String uf,
        LocalDateTime dataHora
       
) {
    public static ReuniaoResponse fromEntity(Reuniao reuniao) {
        return new ReuniaoResponse(
                reuniao.getId(),
                reuniao.getTitulo(),
                reuniao.getDescricao(),
                reuniao.getEndereco(),
                reuniao.getCidade().getId(),
                reuniao.getCidade().getNome(),
                reuniao.getCidade().getEstado(),
                reuniao.getDataHora()
        );
    }
}
