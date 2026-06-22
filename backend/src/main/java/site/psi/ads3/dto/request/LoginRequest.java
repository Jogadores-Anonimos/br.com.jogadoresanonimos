package site.psi.ads3.dto.request;
import jakarta.validation.constraints.NotNull;

public record LoginRequest(
        @NotNull(message = "Login não pode ser nulo")
        String login,

        @NotNull(message = "Senha não pode ser nulo")
        String password
) {}