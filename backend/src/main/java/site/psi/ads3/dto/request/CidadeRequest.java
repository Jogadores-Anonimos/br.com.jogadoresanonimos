package site.psi.ads3.dto.request;
import site.psi.ads3.entity.Cidade;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CidadeRequest(
        @NotNull(message = "Nome não pode ser nulo")
        String nome,
        
        @NotNull(message = "Cidade não pode ser nula")
        @Size(min = 2, max = 2, message="UF não pode ter menos ou mais que dois caracteres, exemplo=SP")
        String estado
) {
    public void updateCidade(Cidade cidade) {
        if (nome != null) cidade.setNome(nome);
        if (estado != null) cidade.setEstado(estado);
    }
}