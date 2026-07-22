package br.com.jogadoresanonimos.auth.service;

import br.com.jogadoresanonimos.auth.dto.*;
import br.com.jogadoresanonimos.auth.entity.PasswordResetToken;
import br.com.jogadoresanonimos.auth.repository.PasswordResetTokenRepository;
import br.com.jogadoresanonimos.common.dto.ApiError;
import br.com.jogadoresanonimos.common.util.HmacHasher;
import br.com.jogadoresanonimos.common.exception.ConflitoDeEstadoException;
import br.com.jogadoresanonimos.common.exception.ErrorCode;
import br.com.jogadoresanonimos.common.exception.UnauthenticatedException;
import br.com.jogadoresanonimos.common.exception.ValidationException;
import br.com.jogadoresanonimos.common.ratelimit.RateLimiterService;
import br.com.jogadoresanonimos.security.AuthenticatedUser;
import br.com.jogadoresanonimos.security.JwtService;
import br.com.jogadoresanonimos.security.MfaSecretCipher;
import br.com.jogadoresanonimos.security.TotpService;
import br.com.jogadoresanonimos.security.entity.JwtDenylistEntry;
import br.com.jogadoresanonimos.security.repository.JwtDenylistRepository;
import br.com.jogadoresanonimos.usuario.entity.Role;
import br.com.jogadoresanonimos.usuario.entity.Usuario;
import br.com.jogadoresanonimos.usuario.repository.UsuarioRepository;
import br.com.jogadoresanonimos.usuario.dto.UsuarioResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);
    private static final String MENSAGEM_ESQUECI_SENHA_GENERICA =
            "Se o apelido ou e-mail existir em nossa base, enviaremos um link de redefinição.";
    private static final String HASH_DUMMY_TIMING =
            "$argon2id$v=19$m=16384,t=2,p=1$c29tZXNhbHRzb21lc2FsdA$b3V0cHV0Zml4b2Zmc2V0Y29uc3RhbnRmb3J0aW1pbmc";
    private static final int REGISTRO_VARREDURA_GLOBAL_CAPACIDADE = 30;

    private final UsuarioRepository usuarioRepository;
    private final PasswordResetTokenRepository resetTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final EmailService emailService;
    private final RateLimiterService rateLimiterService;
    private final HmacHasher hmacHasher;
    private final RecaptchaService recaptchaService;
    private final TotpService totpService;
    private final MfaSecretCipher mfaSecretCipher;
    private final JwtDenylistRepository jwtDenylistRepository;
    private final SecureRandom secureRandom = new SecureRandom();

    public AuthService(UsuarioRepository usuarioRepository, PasswordResetTokenRepository resetTokenRepository,
                        PasswordEncoder passwordEncoder, JwtService jwtService, EmailService emailService,
                        RateLimiterService rateLimiterService, HmacHasher hmacHasher, RecaptchaService recaptchaService,
                        TotpService totpService, MfaSecretCipher mfaSecretCipher, JwtDenylistRepository jwtDenylistRepository) {
        this.usuarioRepository = usuarioRepository;
        this.resetTokenRepository = resetTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.emailService = emailService;
        this.rateLimiterService = rateLimiterService;
        this.hmacHasher = hmacHasher;
        this.recaptchaService = recaptchaService;
        this.totpService = totpService;
        this.mfaSecretCipher = mfaSecretCipher;
        this.jwtDenylistRepository = jwtDenylistRepository;
    }

    @Transactional
    public UsuarioResponse registrar(RegistroRequest request) {
        checarRateLimitConta("registro", request.apelido() + "|" + request.email(), ErrorCode.AUTH_RATE_LIMITED);
        rateLimiterService.check("registro-varredura-global", REGISTRO_VARREDURA_GLOBAL_CAPACIDADE, Duration.ofMinutes(1),
                ErrorCode.AUTH_RATE_LIMITED);
        recaptchaService.validar(request.recaptchaToken());
        if (!request.senha().equals(request.confirmarSenha())) {
            throw new ValidationException(ErrorCode.AUTH_SENHA_CONFIRMACAO_DIVERGENTE, "Senha e confirmação não coincidem",
                    List.of(new ApiError.FieldError("confirmarSenha", "A confirmação deve ser igual à senha digitada")));
        }
        boolean apelidoExiste = usuarioRepository.existsByApelidoIgnoreCase(request.apelido());
        if (apelidoExiste) {
            throw new ConflitoDeEstadoException(ErrorCode.AUTH_APELIDO_EM_USO,
                    "Esse apelido já está em uso, escolha outro",
                    List.of(new ApiError.FieldError("apelido", "Esse apelido já está em uso, escolha outro")));
        }
        boolean emailExiste = usuarioRepository.existsByEmailIgnoreCase(request.email());
        if (emailExiste) {
            throw new ConflitoDeEstadoException(ErrorCode.AUTH_EMAIL_EM_USO,
                    "Esse e-mail já está cadastrado",
                    List.of(new ApiError.FieldError("email", "Esse e-mail já está cadastrado")));
        }
        String senhaHash = passwordEncoder.encode(request.senha());
        Usuario usuario = new Usuario(request.apelido(), request.email(), senhaHash, Role.PARTICIPANTE);
        try {
            usuario = usuarioRepository.save(usuario);
        } catch (DataIntegrityViolationException e) {
            throw new ConflitoDeEstadoException(ErrorCode.AUTH_CADASTRO_CONFLITO,
                    "Apelido ou e-mail já cadastrado. Tente outro apelido ou recupere sua conta.");
        }
        return UsuarioResponse.from(usuario);
    }

    @Transactional
    public LoginResponse login(LoginRequest request) {
        String identificadorNormalizado = request.identificador().trim().toLowerCase();
        checarRateLimitConta("login", identificadorNormalizado, ErrorCode.AUTH_RATE_LIMITED);
        recaptchaService.validar(request.recaptchaToken());
        Optional<Usuario> usuarioOpt = usuarioRepository.findByApelidoIgnoreCaseOrEmailIgnoreCase(
                request.identificador(), request.identificador());
        String hashParaComparar = usuarioOpt.map(Usuario::getSenhaHash).orElse(HASH_DUMMY_TIMING);
        boolean senhaConfere = passwordEncoder.matches(request.senha(), hashParaComparar);
        if (usuarioOpt.isEmpty() || !senhaConfere) {
            log.warn("login_falho identificadorHash={}", hmacHasher.hash(identificadorNormalizado));
            throw new UnauthenticatedException(ErrorCode.AUTH_INVALID_CREDENTIALS, "Identificador ou senha inválidos");
        }
        Usuario usuario = usuarioOpt.get();
        if (usuario.getRole() == Role.ADMIN && usuario.isMfaAtivo()) {
            return LoginResponse.comDesafioMfa();
        }
        return emitirLoginAutenticado(usuario);
    }

    @Transactional
    public LoginResponse loginComMfa(MfaLoginRequest request) {
        String identificadorNormalizado = request.identificador().trim().toLowerCase();
        checarRateLimitConta("login-mfa", identificadorNormalizado, ErrorCode.AUTH_RATE_LIMITED);
        recaptchaService.validar(request.recaptchaToken());
        Optional<Usuario> usuarioOpt = usuarioRepository.findByApelidoIgnoreCaseOrEmailIgnoreCase(
                request.identificador(), request.identificador());
        if (usuarioOpt.isEmpty() || !passwordEncoder.matches(request.senha(), usuarioOpt.get().getSenhaHash())) {
            log.warn("login_falho identificadorHash={}", hmacHasher.hash(identificadorNormalizado));
            throw new UnauthenticatedException(ErrorCode.AUTH_INVALID_CREDENTIALS, "Identificador ou senha inválidos");
        }
        Usuario usuario = usuarioOpt.get();
        if (usuario.getRole() != Role.ADMIN || !usuario.isMfaAtivo()) {
            log.warn("login_mfa_fluxo_invalido identificadorHash={}", hmacHasher.hash(identificadorNormalizado));
            throw new UnauthenticatedException(ErrorCode.AUTH_INVALID_CREDENTIALS, "Identificador ou senha inválidos");
        }
        String secret = mfaSecretCipher.decifrar(usuario.getMfaSecretCifrado());
        if (!totpService.validar(secret, request.codigoTotp())) {
            log.warn("login_mfa_codigo_invalido identificadorHash={}", hmacHasher.hash(identificadorNormalizado));
            throw new UnauthenticatedException(ErrorCode.AUTH_INVALID_CREDENTIALS, "Identificador ou senha inválidos");
        }
        return emitirLoginAutenticado(usuario);
    }

    private LoginResponse emitirLoginAutenticado(Usuario usuario) {
        String token = jwtService.gerarToken(usuario.getId(), usuario.getRole().name(), usuario.getSenhaAlteradaEm());
        Instant expiraEm = jwtService.expiracaoDe(token);
        return LoginResponse.autenticado(token, expiraEm,
                new LoginResponse.UsuarioResumo(usuario.getId().toString(), usuario.getApelido(),
                        usuario.getRole().name(), usuario.isDeveTrocarSenha()));
    }

    @Transactional
    public MensagemResponse esqueciSenha(EsqueciSenhaRequest request) {
        String identificadorNormalizado = request.identificador().trim().toLowerCase();
        checarRateLimitConta("esqueci-senha", identificadorNormalizado, ErrorCode.AUTH_RATE_LIMITED);
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
        checarRateLimitConta("redefinir-senha", request.token(), ErrorCode.AUTH_RATE_LIMITED);
        if (!request.novaSenha().equals(request.confirmarNovaSenha())) {
            throw new ValidationException(ErrorCode.AUTH_SENHA_CONFIRMACAO_DIVERGENTE, "Nova senha e confirmação não coincidem",
                    List.of(new ApiError.FieldError("confirmarNovaSenha", "Deve ser igual à nova senha")));
        }
        String tokenHash = hmacHasher.hash(request.token());
        PasswordResetToken resetToken = resetTokenRepository.findByTokenHash(tokenHash)
                .filter(PasswordResetToken::valido)
                .orElseThrow(() -> new UnauthenticatedException(ErrorCode.AUTH_RESET_TOKEN_INVALIDO, "Link inválido ou expirado"));
        Usuario usuario = usuarioRepository.findById(resetToken.getUsuarioId())
                .orElseThrow(() -> new UnauthenticatedException(ErrorCode.AUTH_RESET_TOKEN_INVALIDO, "Link inválido ou expirado"));
        usuario.setSenhaHash(passwordEncoder.encode(request.novaSenha()));
        resetToken.marcarUsado();
        return new MensagemResponse("Senha redefinida com sucesso.");
    }

    @Transactional
    public MensagemResponse logout(AuthenticatedUser principal) {
        if (principal == null || principal.jti() == null) {
            throw new UnauthenticatedException(ErrorCode.AUTH_TOKEN_AUSENTE_OU_INVALIDO, "Token ausente ou inválido");
        }
        if (!jwtDenylistRepository.existsByJti(principal.jti())) {
            jwtDenylistRepository.save(new JwtDenylistEntry(principal.jti(), principal.expiraEm()));
        }
        return new MensagemResponse("Sessão encerrada com sucesso.");
    }

    private String gerarTokenBruto() {
        byte[] bytes = new byte[32];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private void checarRateLimitConta(String acao, String identificadorNormalizado, String code) {
        rateLimiterService.check(acao + ":" + hmacHasher.hash(identificadorNormalizado), 5, Duration.ofMinutes(1), code);
    }
}
