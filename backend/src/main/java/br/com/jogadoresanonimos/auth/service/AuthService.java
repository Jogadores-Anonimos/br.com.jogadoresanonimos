package br.com.jogadoresanonimos.auth.service;

import br.com.jogadoresanonimos.auth.dto.*;
import br.com.jogadoresanonimos.auth.entity.AdminResetCodigo;
import br.com.jogadoresanonimos.auth.entity.EmailConfirmacaoToken;
import br.com.jogadoresanonimos.auth.entity.PasswordResetToken;
import br.com.jogadoresanonimos.auth.repository.AdminResetCodigoRepository;
import br.com.jogadoresanonimos.auth.repository.EmailConfirmacaoTokenRepository;
import br.com.jogadoresanonimos.auth.repository.PasswordResetTokenRepository;
import br.com.jogadoresanonimos.common.dto.ApiError;
import br.com.jogadoresanonimos.common.util.HmacHasher;
import br.com.jogadoresanonimos.common.exception.ConflitoDeEstadoException;
import br.com.jogadoresanonimos.common.exception.ErrorCode;
import br.com.jogadoresanonimos.common.exception.RateLimitedException;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
    private static final String MENSAGEM_REENVIAR_CONFIRMACAO_EMAIL_GENERICA =
            "Se o apelido ou e-mail existir em nossa base e o e-mail ainda não estiver confirmado, enviaremos um novo link de confirmação.";
    private static final int REGISTRO_VARREDURA_GLOBAL_CAPACIDADE = 30;
    private static final int REENVIAR_CONFIRMACAO_EMAIL_VARREDURA_GLOBAL_CAPACIDADE = 30;
    private static final int REENVIAR_CONFIRMACAO_EMAIL_POR_USUARIO_CAPACIDADE = 3;
    private static final int MAX_TENTATIVAS_GERACAO_CODIGO_ADMIN = 5;

    private final UsuarioRepository usuarioRepository;
    private final PasswordResetTokenRepository resetTokenRepository;
    private final AdminResetCodigoRepository adminResetCodigoRepository;
    private final EmailConfirmacaoTokenRepository emailConfirmacaoTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final EmailService emailService;
    private final RateLimiterService rateLimiterService;
    private final HmacHasher hmacHasher;
    private final RecaptchaService recaptchaService;
    private final TotpService totpService;
    private final MfaSecretCipher mfaSecretCipher;
    private final JwtDenylistRepository jwtDenylistRepository;
    private final String frontendUrl;
    private final SecureRandom secureRandom = new SecureRandom();

    public AuthService(UsuarioRepository usuarioRepository, PasswordResetTokenRepository resetTokenRepository,
                        AdminResetCodigoRepository adminResetCodigoRepository,
                        EmailConfirmacaoTokenRepository emailConfirmacaoTokenRepository, PasswordEncoder passwordEncoder,
                        JwtService jwtService, EmailService emailService, RateLimiterService rateLimiterService,
                        HmacHasher hmacHasher, RecaptchaService recaptchaService, TotpService totpService,
                        MfaSecretCipher mfaSecretCipher, JwtDenylistRepository jwtDenylistRepository,
                        @Value("${app.frontend-url}") String frontendUrl) {
        this.usuarioRepository = usuarioRepository;
        this.resetTokenRepository = resetTokenRepository;
        this.adminResetCodigoRepository = adminResetCodigoRepository;
        this.emailConfirmacaoTokenRepository = emailConfirmacaoTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.emailService = emailService;
        this.rateLimiterService = rateLimiterService;
        this.hmacHasher = hmacHasher;
        this.recaptchaService = recaptchaService;
        this.totpService = totpService;
        this.mfaSecretCipher = mfaSecretCipher;
        this.jwtDenylistRepository = jwtDenylistRepository;
        this.frontendUrl = frontendUrl;
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
        long inicioHash = System.nanoTime();
        String senhaHash = passwordEncoder.encode(request.senha());
        log.info("registro_hash_senha duracaoMs={}", duracaoMsDesde(inicioHash));
        Usuario usuario = new Usuario(request.apelido(), request.email(), senhaHash, Role.PARTICIPANTE);
        long inicioSave = System.nanoTime();
        try {
            usuario = usuarioRepository.save(usuario);
        } catch (DataIntegrityViolationException e) {
            throw new ConflitoDeEstadoException(ErrorCode.AUTH_CADASTRO_CONFLITO,
                    "Apelido ou e-mail já cadastrado. Tente outro apelido ou recupere sua conta.");
        } finally {
            log.info("registro_persistencia_usuario duracaoMs={}", duracaoMsDesde(inicioSave));
        }
        gerarEEnviarTokenConfirmacaoEmail(usuario);
        return UsuarioResponse.from(usuario);
    }

    @Transactional
    public LoginResponse login(LoginRequest request) {
        String identificadorNormalizado = request.identificador().trim().toLowerCase();
        checarRateLimitConta("login", identificadorNormalizado, ErrorCode.AUTH_RATE_LIMITED);
        recaptchaService.validar(request.recaptchaToken());
        long inicioBusca = System.nanoTime();
        Optional<Usuario> usuarioOpt = usuarioRepository.findByApelidoIgnoreCaseOrEmailIgnoreCase(
                request.identificador(), request.identificador());
        log.info("login_busca_usuario duracaoMs={}", duracaoMsDesde(inicioBusca));
        String hashParaComparar = usuarioOpt.map(Usuario::getSenhaHash).orElse(HASH_DUMMY_TIMING);
        long inicioHash = System.nanoTime();
        boolean senhaConfere = passwordEncoder.matches(request.senha(), hashParaComparar);
        log.info("login_verificacao_senha duracaoMs={}", duracaoMsDesde(inicioHash));
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
        long inicioBusca = System.nanoTime();
        Optional<Usuario> usuarioOpt = usuarioRepository.findByApelidoIgnoreCaseOrEmailIgnoreCase(
                request.identificador(), request.identificador());
        log.info("login_mfa_busca_usuario duracaoMs={}", duracaoMsDesde(inicioBusca));
        long inicioHash = System.nanoTime();
        boolean senhaConfere = usuarioOpt.isPresent() && passwordEncoder.matches(request.senha(), usuarioOpt.get().getSenhaHash());
        log.info("login_mfa_verificacao_senha duracaoMs={}", duracaoMsDesde(inicioHash));
        if (!senhaConfere) {
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
        if (usuarioOpt.isPresent() && usuarioOpt.get().getRole() == Role.ADMIN) {
            gerarEEnviarCodigoAdmin(usuarioOpt.get());
        } else {
            usuarioOpt.ifPresent(this::gerarEEnviarTokenComum);
        }
        return new MensagemResponse(MENSAGEM_ESQUECI_SENHA_GENERICA);
    }

    private void gerarEEnviarTokenComum(Usuario usuario) {
        if (usuario.getEmailConfirmadoEm() == null) {
            return;
        }
        String tokenBruto = gerarTokenBruto();
        String tokenHash = hmacHasher.hash(tokenBruto);
        PasswordResetToken resetToken = new PasswordResetToken(usuario.getId(), tokenHash, Instant.now().plus(Duration.ofMinutes(30)));
        resetTokenRepository.save(resetToken);
        String usuarioIdHash = hmacHasher.hash(usuario.getId().toString());
        String link = construirLink("/redefinir-senha", tokenBruto);
        try {
            emailService.enviarLinkRedefinicaoSenha(usuarioIdHash, usuario.getEmail(), link);
        } catch (Exception ex) {
            log.warn("falha_disparo_envio_email_redefinicao_senha usuarioHash={} causa={}", usuarioIdHash, ex.getClass().getSimpleName());
        }
    }

    private void gerarEEnviarCodigoAdmin(Usuario usuario) {
        String codigo = gerarCodigoNumericoAdmin();
        String codigoHash = hmacHasher.hash(codigo);
        AdminResetCodigo resetCodigo = new AdminResetCodigo(usuario.getId(), codigoHash, Instant.now().plus(Duration.ofMinutes(10)));
        int tentativa = 0;
        while (true) {
            try {
                adminResetCodigoRepository.save(resetCodigo);
                break;
            } catch (DataIntegrityViolationException e) {
                tentativa++;
                if (tentativa >= MAX_TENTATIVAS_GERACAO_CODIGO_ADMIN) {
                    throw e;
                }
                codigo = gerarCodigoNumericoAdmin();
                codigoHash = hmacHasher.hash(codigo);
                resetCodigo = new AdminResetCodigo(usuario.getId(), codigoHash, Instant.now().plus(Duration.ofMinutes(10)));
            }
        }
        String usuarioIdHash = hmacHasher.hash(usuario.getId().toString());
        try {
            emailService.enviarCodigoRedefinicaoSenhaAdmin(usuarioIdHash, usuario.getEmail(), codigo);
        } catch (Exception ex) {
            log.warn("falha_disparo_envio_email_codigo_admin usuarioHash={} causa={}", usuarioIdHash, ex.getClass().getSimpleName());
        }
    }

    private String gerarCodigoNumericoAdmin() {
        return String.format("%06d", secureRandom.nextInt(1_000_000));
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

    private void gerarEEnviarTokenConfirmacaoEmail(Usuario usuario) {
        String tokenBruto = gerarTokenBruto();
        String tokenHash = sha256Hash(tokenBruto);
        EmailConfirmacaoToken token = new EmailConfirmacaoToken(usuario.getId(), tokenHash, Instant.now().plus(Duration.ofHours(24)));
        emailConfirmacaoTokenRepository.save(token);
        String usuarioIdHash = hmacHasher.hash(usuario.getId().toString());
        String link = construirLink("/confirmar-email", tokenBruto);
        try {
            emailService.enviarLinkConfirmacaoEmail(usuarioIdHash, usuario.getEmail(), link);
        } catch (Exception ex) {
            log.warn("falha_disparo_envio_email_confirmacao usuarioHash={} causa={}", usuarioIdHash, ex.getClass().getSimpleName());
        }
    }

    @Transactional
    public MensagemResponse confirmarEmail(String token) {
        if (token == null || token.isBlank()) {
            throw new ValidationException(ErrorCode.VALIDATION_ERROR, "Token é obrigatório");
        }
        String tokenHash = sha256Hash(token);
        checarRateLimitConta("confirmar-email", tokenHash, ErrorCode.AUTH_RATE_LIMITED);
        EmailConfirmacaoToken confirmacaoToken = emailConfirmacaoTokenRepository.findByTokenHash(tokenHash)
                .orElseThrow(() -> new UnauthenticatedException(ErrorCode.AUTH_EMAIL_CONFIRMACAO_TOKEN_INVALIDO,
                        "Link de confirmação inválido ou expirado"));
        Usuario usuario = usuarioRepository.findById(confirmacaoToken.getUsuarioId())
                .orElseThrow(() -> new UnauthenticatedException(ErrorCode.AUTH_EMAIL_CONFIRMACAO_TOKEN_INVALIDO,
                        "Link de confirmação inválido ou expirado"));
        if (usuario.getEmailConfirmadoEm() != null) {
            return new MensagemResponse("E-mail confirmado com sucesso.");
        }
        if (confirmacaoToken.getUsadoEm() != null || confirmacaoToken.expirado()) {
            throw new UnauthenticatedException(ErrorCode.AUTH_EMAIL_CONFIRMACAO_TOKEN_INVALIDO,
                    "Link de confirmação inválido ou expirado");
        }
        usuario.confirmarEmail();
        confirmacaoToken.marcarUsado();
        return new MensagemResponse("E-mail confirmado com sucesso.");
    }

    @Transactional
    public MensagemResponse reenviarConfirmacaoEmail(ReenviarConfirmacaoEmailRequest request) {
        rateLimiterService.check("reenviar-confirmacao-email-varredura-global",
                REENVIAR_CONFIRMACAO_EMAIL_VARREDURA_GLOBAL_CAPACIDADE, Duration.ofMinutes(1), ErrorCode.AUTH_RATE_LIMITED);
        Optional<Usuario> usuarioOpt = usuarioRepository.findByApelidoIgnoreCaseOrEmailIgnoreCase(
                request.identificador(), request.identificador());
        if (usuarioOpt.isPresent() && usuarioOpt.get().getEmailConfirmadoEm() == null) {
            Usuario usuario = usuarioOpt.get();
            try {
                rateLimiterService.check("reenviar-confirmacao-email:" + hmacHasher.hash(usuario.getId().toString()),
                        REENVIAR_CONFIRMACAO_EMAIL_POR_USUARIO_CAPACIDADE, Duration.ofMinutes(10), ErrorCode.AUTH_RATE_LIMITED);
                gerarEEnviarTokenConfirmacaoEmail(usuario);
            } catch (RateLimitedException ignoradoParaAntiEnumeration) {
            }
        }
        return new MensagemResponse(MENSAGEM_REENVIAR_CONFIRMACAO_EMAIL_GENERICA);
    }

    private String sha256Hash(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] result = digest.digest(value.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(result);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Falha ao calcular SHA-256", e);
        }
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

    private String construirLink(String path, String tokenBruto) {
        return UriComponentsBuilder.fromUriString(frontendUrl)
                .path(path)
                .queryParam("token", tokenBruto)
                .build()
                .toUriString();
    }

    private String gerarTokenBruto() {
        byte[] bytes = new byte[32];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private void checarRateLimitConta(String acao, String identificadorNormalizado, String code) {
        rateLimiterService.check(acao + ":" + hmacHasher.hash(identificadorNormalizado), 5, Duration.ofMinutes(1), code);
    }

    private static long duracaoMsDesde(long inicioNanoTime) {
        return (System.nanoTime() - inicioNanoTime) / 1_000_000;
    }
}
