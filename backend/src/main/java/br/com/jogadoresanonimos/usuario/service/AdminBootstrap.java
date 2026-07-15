package br.com.jogadoresanonimos.usuario.service;

import br.com.jogadoresanonimos.usuario.entity.Role;
import br.com.jogadoresanonimos.usuario.entity.Usuario;
import br.com.jogadoresanonimos.usuario.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminBootstrap implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(AdminBootstrap.class);

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final String bootstrapApelido;
    private final String bootstrapEmail;
    private final String bootstrapSenha;

    public AdminBootstrap(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder,
                           @Value("${app.admin-bootstrap.apelido:}") String bootstrapApelido,
                           @Value("${app.admin-bootstrap.email:}") String bootstrapEmail,
                           @Value("${app.admin-bootstrap.senha:}") String bootstrapSenha) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.bootstrapApelido = bootstrapApelido;
        this.bootstrapEmail = bootstrapEmail;
        this.bootstrapSenha = bootstrapSenha;
    }

    @Override
    public void run(String... args) {
        if (usuarioRepository.existsByRole(Role.ADMIN)) {
            return;
        }
        if (bootstrapApelido.isBlank() || bootstrapEmail.isBlank() || bootstrapSenha.isBlank()) {
            log.warn("admin_bootstrap_ignorado motivo=variaveis_ausentes");
            return;
        }
        Usuario admin = new Usuario(bootstrapApelido, bootstrapEmail, passwordEncoder.encode(bootstrapSenha), Role.ADMIN, true);
        usuarioRepository.save(admin);
        log.info("admin_bootstrap_criado");
    }
}
