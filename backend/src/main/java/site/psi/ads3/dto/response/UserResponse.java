package site.psi.ads3.dto.response;
import site.psi.ads3.entity.User;

public record UserResponse(
        Long id,
        String login
) {
    public static UserResponse fromEntity(User user) {
        return new UserResponse(
                user.getId(),
                user.getLogin()
        );
    }
}
