package site.psi.ads3.dto.response;
import site.psi.ads3.entity.Cidade;

public record CidadeResponse(
        Long id,
        String nome,
        String estado
) {
    public static CidadeResponse fromEntity(Cidade cidade) {
        return new CidadeResponse(
                cidade.getId(),
                cidade.getNome(),
                cidade.getEstado()
        );
    }
}