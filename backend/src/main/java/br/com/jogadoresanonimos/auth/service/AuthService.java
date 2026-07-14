package br.com.jogadoresanonimos.auth.service;

import br.com.jogadoresanonimos.auth.dto.*;
import br.com.jogadoresanonimos.auth.entity.PasswordResetToken;
import br.com.jogadoresanonimos.auth.repository.PasswordResetTokenRepository;
import br.com.jogadoresanonimos.common.util.HmacHasher;
import br.com.jogadoresanonimos.common.exception.ConflitoDeEstadoException;
import br.com.jogadoresanonimos.common.exception.UnauthenticatedException;
import br.com.jogadoresanonimos.common.exception.ValidationException;
import br.com.jogadoresanonimos.common.ratelimit.RateLimiterService;
import br.com.jogadoresanonimos.security.JwtService;
import br.com.jogadoresanonimos.usuario.entity.Role;
import br.com.jogadoresanonimos.usuario.entity.Usuario;
import br.com.jogadoresanonimos.usuario.repository.UsuarioRepository;
import br.com.jogadoresanonimos.usuario.dto.UsuarioResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Optional;

@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);
    private static final String MENSAGEM_ESQUECI_SENHA_GENERICA =
            "Se o apelido ou e-mail existir em nossa base, enviaremos um link de redefinição.";

    private final UsuarioRepository usuarioRepository;
    private final PasswordResetTokenRepository resetTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final EmailService emailService;
    private final RateLimiterService rateLimiterService;
    private final HmacHasher hmacHasher;
    private final SecureRandom secureRandom = new SecureRandom();

    public AuthService(UsuarioRepository usuarioRepository, PasswordResetTokenRepository resetTokenRepository,
                        PasswordEncoder passwordEncoder, JwtService jwtService, EmailService emailService,
                        RateLimiterService rateLimiterService, HmacHasher hmacHasher) {
        this.usuarioRepository = usuarioRepository;
        this.resetTokenRepository = resetTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.emailService = emailService;
        this.rateLimiterService = rateLimiterService;
        this.hmacHasher = hmacHasher;
    }

    @Transactional
    public UsuarioResponse registrar(RegistroRequest request) {
        checarRateLimitConta("registro", request.apelido() + "|" + request.email());

        if (!request.senha().equals(request.confirmarSenha())) {
            throw new ValidationException("Senha e confirmação não coincidem",
                    java.util.List.of(new br.com.jogadoresanonimos.common.dto.ApiError.FieldError(
                            "confirmarSenha", "Deve ser igual à senha")));
        }

        boolean apelidoExiste = usuarioRepository.existsByApelidoIgnoreCase(request.apelido());
        boolean emailExiste = usuarioRepository.existsByEmailIgnoreCase(request.email());
        if (apelidoExiste || emailExiste) {
            throw new ConflitoDeEstadoException("Não foi possível concluir o cadastro com os dados informados");
        }

        String senhaHash = passwordEncoder.encode(request.senha());
        Usuario usuario = new Usuario(request.apelido(), request.email(), senhaHash, Role.PARTICIPANTE);
        usuario = usuarioRepository.save(usuario);
        return UsuarioResponse.from(usuario);
    }

    @Transactional
    public LoginResponse login(LoginRequest request) {
        String identificadorNormalizado = request.identificador().trim().toLowerCase();
        checarRateLimitConta("login", identificadorNormalizado);

        Optional<Usuario> usuarioOpt = usuarioRepository.findByApelidoIgnoreCaseOrEmailIgnoreCase(
                request.identificador(), request.identificador());

        if (usuarioOpt.isEmpty() || !passwordEncoder.matches(request.senha(), usuarioOpt.get().getSenhaHash())) {
            log.warn("login_falho identificadorHash={}", hmacHasher.hash(identificadorNormalizado));
            throw new UnauthenticatedException("Identificador ou senha inválidos");
        }

        Usuario usuario = usuarioOpt.get();
        String token = jwtService.gerarToken(usuario.getId(), usuario.getRole().name(), usuario.getSenhaAlteradaEm());
        Instant expiraEm = jwtService.expiracaoDe(token);

        return new LoginResponse(token, expiraEm,
                new LoginResponse.UsuarioResumo(usuario.getId().toString(), usuario.getApelido(), usuario.getRole().name()));
    }

    @Transactional
    public MensagemResponse esqueciSenha(EsqueciSenhaRequest request) {
        String identificadorNormalizado = request.identificador().trim().toLowerCase();
        checarRateLimitConta("esqueci-senha", identificadorNormalizado);

        Optional<Usuario> usuarioOpt = usuarioRepository.findByApelidoIgnoreCaseOrEmailIgnoreCase(
                request.identificador(), request.identificador());

        usuarioOpt.ifPresent(usuario -> {
            String tokenBruto = gerarTokenBruto();
            String tokenHash = hmacHasher.hash(tokenBruto);
            PasswordResetToken resetToken = new PasswordResetToken(usuario.getId(), tokenHash, Instant.now().plus(Duration.ofMinutes(30)));
            resetTokenRepository.save(resetToken);

            String usuarioIdHash = hmacHasher.hash(usuario.getId().toString());
            emailService.enviarLinkRedefinicaoSenha(usuarioIdHash, tokenBruto);
        });

        return new MensagemResponse(MENSAGEM_ESQUECI_SENHA_GENERICA);
    }

    @Transactional
    public MensagemResponse redefinirSenha(RedefinirSenhaRequest request) {
        if (!request.novaSenha().equals(request.confirmarNovaSenha())) {
            throw new ValidationException("Nova senha e confirmação não coincidem",
                    java.util.List.of(new br.com.jogadoresanonimos.common.dto.ApiError.FieldError(
                            "confirmarNovaSenha", "Deve ser igual à nova senha")));
        }

        String tokenHash = hmacHasher.hash(request.token());
        PasswordResetToken resetToken = resetTokenRepository.findByTokenHash(tokenHash)
                .filter(PasswordResetToken::valido)
                .orElseThrow(() -> new UnauthenticatedException("Link inválido ou expirado"));

        Usuario usuario = usuarioRepository.findById(resetToken.getUsuarioId())
                .orElseThrow(() -> new UnauthenticatedException("Link inválido ou expirado"));

        usuario.setSenhaHash(passwordEncoder.encode(request.novaSenha()));
        resetToken.marcarUsado();

        return new MensagemResponse("Senha redefinida com sucesso.");
    }

    private String gerarTokenBruto() {
        byte[] bytes = new byte[32];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private void checarRateLimitConta(String acao, String identificadorNormalizado) {
        rateLimiterService.check(acao + ":" + hmacHasher.hash(identificadorNormalizado), 5, Duration.ofMinutes(1));
    }
}
