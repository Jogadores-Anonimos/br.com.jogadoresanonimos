    package site.psi.ads3.dto.request;
    import site.psi.ads3.entity.User;

    public record UserRequest(
            String login,
            String password
    ) {
        public void updateUser(User user) {
            if (login != null) user.setLogin(login);
            if (password != null) user.setPasswordHash(password);
        }
    }