package site.psi.ads3.dto.request;
import site.psi.ads3.entity.Feedback;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;

public record FeedbackRequest(
        @NotBlank(message="Nome não pode ser vazio")
        String nome,

        @NotNull(message="Idade não pode ser nulo")
        @Min(value=18, message="É necessário ser maior de idade para realizar um feedback")
        @Max(value=120, message="Idade maxima 120 Anos")
        Integer idade,

        @NotBlank(message="Descrição não pode ser vazia")
        String descricao
) {
    public void updateFeedback(Feedback feedback) {
        if (nome != null) feedback.setNome(nome);
        if (idade != null) feedback.setIdade(idade);
        if (descricao != null) feedback.setDescricao(descricao);
    }
}