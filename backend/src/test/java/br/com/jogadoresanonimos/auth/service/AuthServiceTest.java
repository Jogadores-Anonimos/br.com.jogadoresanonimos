package br.com.jogadoresanonimos.auth.service;

import br.com.jogadoresanonimos.auth.dto.*;
import br.com.jogadoresanonimos.auth.entity.AdminResetCodigo;
import br.com.jogadoresanonimos.auth.entity.EmailConfirmacaoToken;
import br.com.jogadoresanonimos.auth.entity.PasswordResetToken;
import br.com.jogadoresanonimos.auth.entity.TipoEmailConfirmacaoToken;
import br.com.jogadoresanonimos.auth.repository.AdminResetCodigoRepository;
import br.com.jogadoresanonimos.auth.repository.EmailConfirmacaoTokenRepository;
import br.com.jogadoresanonimos.auth.repository.PasswordResetTokenRepository;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    UsuarioRepository usuarioRepository;
    @Mock
    PasswordResetTokenRepository resetTokenRepository;
    @Mock
    AdminResetCodigoRepository adminResetCodigoRepository;
    @Mock
    EmailConfirmacaoTokenRepository emailConfirmacaoTokenRepository;
    @Mock
    PasswordEncoder passwordEncoder;
    @Mock
    JwtService jwtService;
    @Mock
    EmailService emailService;
    @Mock
    HmacHasher hmacHasher;
    @Mock
    RecaptchaService recaptchaService;
    @Mock
    TotpService totpService;
    @Mock
    MfaSecretCipher mfaSecretCipher;
    @Mock
    JwtDenylistRepository jwtDenylistRepository;

    RateLimiterService rateLimiterService;

    @InjectMocks
    AuthService authService;

    @BeforeEach
    void setUp() {
        rateLimiterService = new RateLimiterService();
        authService = new AuthService(usuarioRepository, resetTokenRepository, adminResetCodigoRepository,
                emailConfirmacaoTokenRepository, passwordEncoder, jwtService, emailService, rateLimiterService,
                hmacHasher, recaptchaService, totpService, mfaSecretCipher, jwtDenylistRepository,
                "http://localhost:4200");
        lenientHmac();
    }

    private void lenientHmac() {
        lenient().when(hmacHasher.hash(anyString())).thenAnswer(inv -> "hash:" + inv.getArgument(0));
    }

    @Test
    void registrar_deveFalharQuandoRecaptchaInvalido() {
        RegistroRequest request = new RegistroRequest("novato", "novato@example.com", "senhaForte123", "senhaForte123", "captcha-token");
        doThrow(new ValidationException(ErrorCode.AUTH_RECAPTCHA_INVALIDO, "Falha na verificação do reCAPTCHA"))
                .when(recaptchaService).validar("captcha-token");
        assertThatThrownBy(() -> authService.registrar(request))
                .isInstanceOf(ValidationException.class);
        verifyNoInteractions(usuarioRepository);
    }

    @Test
    void login_deveFalharQuandoRecaptchaInvalido() {
        LoginRequest request = new LoginRequest("apelido", "senhaCorreta123", "captcha-token");
        doThrow(new ValidationException(ErrorCode.AUTH_RECAPTCHA_INVALIDO, "Falha na verificação do reCAPTCHA"))
                .when(recaptchaService).validar("captcha-token");
        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(ValidationException.class);
        verifyNoInteractions(usuarioRepository);
    }

    @Test
    void registrar_deveCriarUsuarioComSenhaHasheada() {
        RegistroRequest request = new RegistroRequest("novato", "novato@example.com", "senhaForte123", "senhaForte123", "captcha-token");
        when(usuarioRepository.existsByApelidoIgnoreCase("novato")).thenReturn(false);
        when(usuarioRepository.existsByEmailIgnoreCase("novato@example.com")).thenReturn(false);
        when(passwordEncoder.encode("senhaForte123")).thenReturn("hash-da-senha");
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(inv -> {
            Usuario usuario = inv.getArgument(0);
            setId(usuario, UUID.randomUUID());
            return usuario;
        });
        UsuarioResponse response = authService.registrar(request);
        assertThat(response.apelido()).isEqualTo("novato");
        assertThat(response.role()).isEqualTo(Role.PARTICIPANTE);
        verify(usuarioRepository).save(any(Usuario.class));
        verify(emailConfirmacaoTokenRepository).save(any(EmailConfirmacaoToken.class));
        verify(emailService).enviarLinkConfirmacaoEmail(anyString(), anyString(), anyString(), eq(TipoEmailConfirmacaoToken.CADASTRO));
    }

    @Test
    void registrar_devePersistirTokenDeConfirmacaoDeFormaSincronaAntesDoEnvio() {
        RegistroRequest request = new RegistroRequest("novato", "novato@example.com", "senhaForte123", "senhaForte123", "captcha-token");
        when(usuarioRepository.existsByApelidoIgnoreCase("novato")).thenReturn(false);
        when(usuarioRepository.existsByEmailIgnoreCase("novato@example.com")).thenReturn(false);
        when(passwordEncoder.encode("senhaForte123")).thenReturn("hash-da-senha");
        UUID usuarioId = UUID.randomUUID();
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(inv -> {
            Usuario usuario = inv.getArgument(0);
            setId(usuario, usuarioId);
            return usuario;
        });
        authService.registrar(request);
        InOrder ordem = inOrder(emailConfirmacaoTokenRepository, emailService);
        ordem.verify(emailConfirmacaoTokenRepository).save(argThat(token -> token.getUsuarioId().equals(usuarioId)));
        ordem.verify(emailService).enviarLinkConfirmacaoEmail(anyString(), anyString(), anyString(), eq(TipoEmailConfirmacaoToken.CADASTRO));
    }

    @Test
    void registrar_naoDevePropagarExcecaoQuandoEnvioAssincronoDeEmailDeConfirmacaoFalha() {
        RegistroRequest request = new RegistroRequest("novato", "novato@example.com", "senhaForte123", "senhaForte123", "captcha-token");
        when(usuarioRepository.existsByApelidoIgnoreCase("novato")).thenReturn(false);
        when(usuarioRepository.existsByEmailIgnoreCase("novato@example.com")).thenReturn(false);
        when(passwordEncoder.encode("senhaForte123")).thenReturn("hash-da-senha");
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(inv -> {
            Usuario usuario = inv.getArgument(0);
            setId(usuario, UUID.randomUUID());
            return usuario;
        });
        doThrow(new RuntimeException("falha de envio simulada"))
                .when(emailService).enviarLinkConfirmacaoEmail(anyString(), anyString(), anyString(), any(TipoEmailConfirmacaoToken.class));
        UsuarioResponse response = authService.registrar(request);
        assertThat(response.apelido()).isEqualTo("novato");
        verify(emailConfirmacaoTokenRepository).save(any(EmailConfirmacaoToken.class));
    }

    @Test
    void registrar_deveGerarTokenDeConfirmacaoComHashSha256ETtlDe24Horas() {
        RegistroRequest request = new RegistroRequest("novato", "novato@example.com", "senhaForte123", "senhaForte123", "captcha-token");
        when(usuarioRepository.existsByApelidoIgnoreCase("novato")).thenReturn(false);
        when(usuarioRepository.existsByEmailIgnoreCase("novato@example.com")).thenReturn(false);
        when(passwordEncoder.encode("senhaForte123")).thenReturn("hash-da-senha");
        UUID usuarioId = UUID.randomUUID();
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(inv -> {
            Usuario usuario = inv.getArgument(0);
            setId(usuario, usuarioId);
            return usuario;
        });
        Instant antes = Instant.now();
        authService.registrar(request);
        Instant depois = Instant.now();
        verify(emailConfirmacaoTokenRepository).save(argThat(token -> {
            boolean usuarioCorreto = token.getUsuarioId().equals(usuarioId);
            boolean naoEhHmac = !token.getTokenHash().startsWith("hash:");
            boolean ttlProximoDe24h = !token.getExpiraEm().isBefore(antes.plus(Duration.ofHours(24)).minusSeconds(5))
                    && !token.getExpiraEm().isAfter(depois.plus(Duration.ofHours(24)).plusSeconds(5));
            return usuarioCorreto && naoEhHmac && ttlProximoDe24h;
        }));
    }

    @Test
    void registrar_deveFalharQuandoSenhasNaoCoincidem() {
        RegistroRequest request = new RegistroRequest("novato", "novato@example.com", "senhaForte123", "outraSenha123", "captcha-token");
        assertThatThrownBy(() -> authService.registrar(request))
                .isInstanceOf(ValidationException.class);
        verifyNoInteractions(usuarioRepository);
    }

    @Test
    void registrar_deveFalharComMensagemEspecificaQuandoApelidoJaExiste() {
        RegistroRequest request = new RegistroRequest("existente", "novo@example.com", "senhaForte123", "senhaForte123", "captcha-token");
        when(usuarioRepository.existsByApelidoIgnoreCase("existente")).thenReturn(true);
        assertThatThrownBy(() -> authService.registrar(request))
                .isInstanceOf(ConflitoDeEstadoException.class)
                .hasMessage("Esse apelido já está em uso, escolha outro");
        verify(usuarioRepository, never()).existsByEmailIgnoreCase(anyString());
    }

    @Test
    void registrar_deveFalharComMensagemEspecificaQuandoEmailJaExiste() {
        RegistroRequest request = new RegistroRequest("novato", "existente@example.com", "senhaForte123", "senhaForte123", "captcha-token");
        when(usuarioRepository.existsByApelidoIgnoreCase("novato")).thenReturn(false);
        when(usuarioRepository.existsByEmailIgnoreCase("existente@example.com")).thenReturn(true);
        assertThatThrownBy(() -> authService.registrar(request))
                .isInstanceOf(ConflitoDeEstadoException.class)
                .hasMessage("Esse e-mail já está cadastrado");
    }

    @Test
    void registrar_deveFalharComConflitoQuandoSaveViolaConstraintUnicaConcorrente() {
        RegistroRequest request = new RegistroRequest("novato", "novato@example.com", "senhaForte123", "senhaForte123", "captcha-token");
        when(usuarioRepository.existsByApelidoIgnoreCase("novato")).thenReturn(false);
        when(usuarioRepository.existsByEmailIgnoreCase("novato@example.com")).thenReturn(false);
        when(passwordEncoder.encode("senhaForte123")).thenReturn("hash-da-senha");
        when(usuarioRepository.save(any(Usuario.class))).thenThrow(new DataIntegrityViolationException("duplicate key"));
        assertThatThrownBy(() -> authService.registrar(request))
                .isInstanceOf(ConflitoDeEstadoException.class)
                .hasMessage("Apelido ou e-mail já cadastrado. Tente outro apelido ou recupere sua conta.");
    }

    @Test
    void login_deveFalharComMensagemGenericaQuandoUsuarioNaoExiste() {
        LoginRequest request = new LoginRequest("desconhecido", "qualquerSenha", "captcha-token");
        when(usuarioRepository.findByApelidoIgnoreCaseOrEmailIgnoreCase("desconhecido", "desconhecido"))
                .thenReturn(Optional.empty());
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);
        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(UnauthenticatedException.class)
                .hasMessage("Identificador ou senha inválidos");
        verify(passwordEncoder).matches(eq("qualquerSenha"), anyString());
    }

    @Test
    void login_deveFalharComMensagemGenericaQuandoSenhaIncorreta() {
        Usuario usuario = new Usuario("apelido", "email@example.com", "hash-real", Role.PARTICIPANTE);
        LoginRequest request = new LoginRequest("apelido", "senhaErrada", "captcha-token");
        when(usuarioRepository.findByApelidoIgnoreCaseOrEmailIgnoreCase("apelido", "apelido"))
                .thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("senhaErrada", "hash-real")).thenReturn(false);
        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(UnauthenticatedException.class);
    }

    @Test
    void login_deveEmitirTokenQuandoCredenciaisCorretas() {
        Usuario usuario = new Usuario("apelido", "email@example.com", "hash-real", Role.PARTICIPANTE);
        setId(usuario, UUID.randomUUID());
        LoginRequest request = new LoginRequest("apelido", "senhaCorreta123", "captcha-token");
        when(usuarioRepository.findByApelidoIgnoreCaseOrEmailIgnoreCase("apelido", "apelido"))
                .thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("senhaCorreta123", "hash-real")).thenReturn(true);
        when(jwtService.gerarToken(any(), anyString(), any())).thenReturn("jwt-token");
        when(jwtService.expiracaoDe("jwt-token")).thenReturn(Instant.now().plusSeconds(3600));
        LoginResponse response = authService.login(request);
        assertThat(response.token()).isEqualTo("jwt-token");
        assertThat(response.usuario().apelido()).isEqualTo("apelido");
    }

    @Test
    void esqueciSenha_deveFalharQuandoRecaptchaInvalido() {
        EsqueciSenhaRequest request = new EsqueciSenhaRequest("apelido", "captcha-token-invalido");
        doThrow(new ValidationException(ErrorCode.AUTH_RECAPTCHA_INVALIDO, "Falha na verificação do reCAPTCHA"))
                .when(recaptchaService).validar("captcha-token-invalido");
        assertThatThrownBy(() -> authService.esqueciSenha(request))
                .isInstanceOf(ValidationException.class);
        verifyNoInteractions(usuarioRepository, emailService, resetTokenRepository, adminResetCodigoRepository);
    }

    @Test
    void esqueciSenha_deveValidarRecaptchaAntesDeBuscarUsuario() {
        EsqueciSenhaRequest request = new EsqueciSenhaRequest("apelido", "captcha-token");
        when(usuarioRepository.findByApelidoIgnoreCaseOrEmailIgnoreCase(anyString(), anyString()))
                .thenReturn(Optional.empty());
        authService.esqueciSenha(request);
        InOrder ordem = inOrder(recaptchaService, usuarioRepository);
        ordem.verify(recaptchaService).validar("captcha-token");
        ordem.verify(usuarioRepository).findByApelidoIgnoreCaseOrEmailIgnoreCase(anyString(), anyString());
    }

    @Test
    void esqueciSenha_deveRetornarMensagemGenericaMesmoSemUsuarioEncontrado() {
        EsqueciSenhaRequest request = new EsqueciSenhaRequest("naoexiste@example.com", "captcha-token");
        when(usuarioRepository.findByApelidoIgnoreCaseOrEmailIgnoreCase(anyString(), anyString()))
                .thenReturn(Optional.empty());
        MensagemResponse response = authService.esqueciSenha(request);
        assertThat(response.mensagem()).contains("Se o apelido ou e-mail existir");
        verifyNoInteractions(emailService);
        verify(resetTokenRepository, never()).save(any());
        verifyNoInteractions(adminResetCodigoRepository);
    }

    @Test
    void esqueciSenha_devePersistirTokenEDispararEmailQuandoUsuarioExisteEEmailConfirmado() {
        UUID usuarioId = UUID.randomUUID();
        Usuario usuario = new Usuario("apelido", "email@example.com", "hash", Role.PARTICIPANTE);
        setId(usuario, usuarioId);
        usuario.confirmarEmail();
        EsqueciSenhaRequest request = new EsqueciSenhaRequest("apelido", "captcha-token");
        when(usuarioRepository.findByApelidoIgnoreCaseOrEmailIgnoreCase(anyString(), anyString()))
                .thenReturn(Optional.of(usuario));
        authService.esqueciSenha(request);
        verify(resetTokenRepository).save(any(PasswordResetToken.class));
        verify(emailService).enviarLinkRedefinicaoSenha(anyString(), anyString(), anyString());
        verifyNoInteractions(adminResetCodigoRepository);
    }

    @Test
    void esqueciSenha_naoDeveGerarNemEnviarTokenQuandoUsuarioComumComEmailNaoConfirmado() {
        UUID usuarioId = UUID.randomUUID();
        Usuario usuario = new Usuario("apelido", "email@example.com", "hash", Role.PARTICIPANTE);
        setId(usuario, usuarioId);
        EsqueciSenhaRequest request = new EsqueciSenhaRequest("apelido", "captcha-token");
        when(usuarioRepository.findByApelidoIgnoreCaseOrEmailIgnoreCase(anyString(), anyString()))
                .thenReturn(Optional.of(usuario));
        MensagemResponse response = authService.esqueciSenha(request);
        assertThat(response.mensagem()).isEqualTo(new MensagemResponse(
                "Se o apelido ou e-mail existir em nossa base, enviaremos um link de redefinição.").mensagem());
        verify(resetTokenRepository, never()).save(any());
        verifyNoInteractions(emailService);
    }

    @Test
    void esqueciSenha_respostaEIdenticaComEmailConfirmadoOuNaoParaNaoVazarStatusDeConfirmacao() {
        Usuario usuarioConfirmado = new Usuario("confirmado", "confirmado@example.com", "hash", Role.PARTICIPANTE);
        setId(usuarioConfirmado, UUID.randomUUID());
        usuarioConfirmado.confirmarEmail();
        Usuario usuarioPendente = new Usuario("pendente", "pendente@example.com", "hash", Role.PARTICIPANTE);
        setId(usuarioPendente, UUID.randomUUID());
        when(usuarioRepository.findByApelidoIgnoreCaseOrEmailIgnoreCase(eq("confirmado"), eq("confirmado")))
                .thenReturn(Optional.of(usuarioConfirmado));
        when(usuarioRepository.findByApelidoIgnoreCaseOrEmailIgnoreCase(eq("pendente"), eq("pendente")))
                .thenReturn(Optional.of(usuarioPendente));
        MensagemResponse respostaConfirmado = authService.esqueciSenha(new EsqueciSenhaRequest("confirmado", "captcha-token"));
        MensagemResponse respostaPendente = authService.esqueciSenha(new EsqueciSenhaRequest("pendente", "captcha-token"));
        assertThat(respostaConfirmado.mensagem()).isEqualTo(respostaPendente.mensagem());
    }

    @Test
    void esqueciSenha_devePersistirCodigoEEnviarEmailAdminQuandoUsuarioEAdmin() {
        UUID usuarioId = UUID.randomUUID();
        Usuario usuario = new Usuario("admin", "admin@example.com", "hash", Role.ADMIN);
        setId(usuario, usuarioId);
        EsqueciSenhaRequest request = new EsqueciSenhaRequest("admin", "captcha-token");
        when(usuarioRepository.findByApelidoIgnoreCaseOrEmailIgnoreCase(anyString(), anyString()))
                .thenReturn(Optional.of(usuario));
        MensagemResponse response = authService.esqueciSenha(request);
        assertThat(response.mensagem()).contains("Se o apelido ou e-mail existir");
        verify(adminResetCodigoRepository).save(argThat(codigo ->
                codigo.getUsuarioId().equals(usuarioId)
                        && codigo.getCodigoHash() != null
                        && codigo.getCodigoHash().startsWith("hash:")
        ));
        verify(emailService).enviarCodigoRedefinicaoSenhaAdmin(anyString(), anyString(), anyString());
        verifyNoInteractions(resetTokenRepository);
        verify(emailService, never()).enviarLinkRedefinicaoSenha(anyString(), anyString(), anyString());
    }

    @Test
    void esqueciSenha_respostaAdminEComumSaoIdenticasParaNaoVazarQuemEAdmin() {
        Usuario usuarioComum = new Usuario("comum", "comum@example.com", "hash", Role.PARTICIPANTE);
        setId(usuarioComum, UUID.randomUUID());
        Usuario admin = new Usuario("admin", "admin@example.com", "hash", Role.ADMIN);
        setId(admin, UUID.randomUUID());
        when(usuarioRepository.findByApelidoIgnoreCaseOrEmailIgnoreCase(eq("comum"), eq("comum")))
                .thenReturn(Optional.of(usuarioComum));
        when(usuarioRepository.findByApelidoIgnoreCaseOrEmailIgnoreCase(eq("admin"), eq("admin")))
                .thenReturn(Optional.of(admin));
        MensagemResponse respostaComum = authService.esqueciSenha(new EsqueciSenhaRequest("comum", "captcha-token"));
        MensagemResponse respostaAdmin = authService.esqueciSenha(new EsqueciSenhaRequest("admin", "captcha-token"));
        assertThat(respostaComum.mensagem()).isEqualTo(respostaAdmin.mensagem());
    }

    @Test
    void esqueciSenha_deveRetentarGeracaoDeCodigoAdminQuandoColisaoDeHashNaPrimeiraTentativa() {
        UUID usuarioId = UUID.randomUUID();
        Usuario usuario = new Usuario("admin", "admin@example.com", "hash", Role.ADMIN);
        setId(usuario, usuarioId);
        EsqueciSenhaRequest request = new EsqueciSenhaRequest("admin", "captcha-token");
        when(usuarioRepository.findByApelidoIgnoreCaseOrEmailIgnoreCase(anyString(), anyString()))
                .thenReturn(Optional.of(usuario));
        when(adminResetCodigoRepository.save(any(AdminResetCodigo.class)))
                .thenThrow(new DataIntegrityViolationException("duplicate key"))
                .thenAnswer(inv -> inv.getArgument(0));
        MensagemResponse response = authService.esqueciSenha(request);
        assertThat(response.mensagem()).contains("Se o apelido ou e-mail existir");
        verify(adminResetCodigoRepository, times(2)).save(any(AdminResetCodigo.class));
        verify(emailService).enviarCodigoRedefinicaoSenhaAdmin(anyString(), anyString(), anyString());
    }

    @Test
    void esqueciSenha_devePropagarExcecaoQuandoColisaoDeHashPersisteEmTodasAsTentativas() {
        UUID usuarioId = UUID.randomUUID();
        Usuario usuario = new Usuario("admin", "admin@example.com", "hash", Role.ADMIN);
        setId(usuario, usuarioId);
        EsqueciSenhaRequest request = new EsqueciSenhaRequest("admin", "captcha-token");
        when(usuarioRepository.findByApelidoIgnoreCaseOrEmailIgnoreCase(anyString(), anyString()))
                .thenReturn(Optional.of(usuario));
        when(adminResetCodigoRepository.save(any(AdminResetCodigo.class)))
                .thenThrow(new DataIntegrityViolationException("duplicate key"));
        assertThatThrownBy(() -> authService.esqueciSenha(request))
                .isInstanceOf(DataIntegrityViolationException.class);
        verify(adminResetCodigoRepository, times(5)).save(any(AdminResetCodigo.class));
        verifyNoInteractions(emailService);
    }

    @Test
    void esqueciSenha_deveEstourarRateLimitPorIdentificadorNaSextaChamadaParaUsuarioComum() {
        Usuario usuario = new Usuario("comum", "comum@example.com", "hash", Role.PARTICIPANTE);
        setId(usuario, UUID.randomUUID());
        usuario.confirmarEmail();
        EsqueciSenhaRequest request = new EsqueciSenhaRequest("comum", "captcha-token");
        when(usuarioRepository.findByApelidoIgnoreCaseOrEmailIgnoreCase(anyString(), anyString()))
                .thenReturn(Optional.of(usuario));
        for (int i = 0; i < 5; i++) {
            authService.esqueciSenha(request);
        }
        assertThatThrownBy(() -> authService.esqueciSenha(request))
                .isInstanceOf(br.com.jogadoresanonimos.common.exception.RateLimitedException.class);
        verify(resetTokenRepository, times(5)).save(any(PasswordResetToken.class));
    }

    @Test
    void esqueciSenha_deveEstourarRateLimitPorIdentificadorNaSextaChamadaParaAdmin() {
        Usuario usuario = new Usuario("admin", "admin@example.com", "hash", Role.ADMIN);
        setId(usuario, UUID.randomUUID());
        EsqueciSenhaRequest request = new EsqueciSenhaRequest("admin", "captcha-token");
        when(usuarioRepository.findByApelidoIgnoreCaseOrEmailIgnoreCase(anyString(), anyString()))
                .thenReturn(Optional.of(usuario));
        for (int i = 0; i < 5; i++) {
            authService.esqueciSenha(request);
        }
        assertThatThrownBy(() -> authService.esqueciSenha(request))
                .isInstanceOf(br.com.jogadoresanonimos.common.exception.RateLimitedException.class);
        verify(adminResetCodigoRepository, times(5)).save(any(AdminResetCodigo.class));
    }

    @Test
    void redefinirSenha_deveFalharQuandoConfirmacaoNaoCoincide() {
        RedefinirSenhaRequest request = new RedefinirSenhaRequest("token", "novaSenha1234", "outraSenha1234");
        assertThatThrownBy(() -> authService.redefinirSenha(request))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    void redefinirSenha_deveFalharComMensagemGenericaQuandoTokenInvalido() {
        RedefinirSenhaRequest request = new RedefinirSenhaRequest("token-invalido", "novaSenha1234", "novaSenha1234");
        when(resetTokenRepository.findByTokenHash(anyString())).thenReturn(Optional.empty());
        assertThatThrownBy(() -> authService.redefinirSenha(request))
                .isInstanceOf(UnauthenticatedException.class)
                .hasMessage("Link inválido ou expirado");
    }

    @Test
    void redefinirSenha_deveAtualizarSenhaEConsumirTokenQuandoValido() {
        UUID usuarioId = UUID.randomUUID();
        Usuario usuario = new Usuario("apelido", "email@example.com", "hash-antigo", Role.PARTICIPANTE);
        setId(usuario, usuarioId);
        PasswordResetToken resetToken = new PasswordResetToken(usuarioId, "hash:token-valido", Instant.now().plusSeconds(600));
        RedefinirSenhaRequest request = new RedefinirSenhaRequest("token-valido", "novaSenha1234", "novaSenha1234");
        when(resetTokenRepository.findByTokenHash(anyString())).thenReturn(Optional.of(resetToken));
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));
        when(passwordEncoder.encode("novaSenha1234")).thenReturn("hash-nova");
        MensagemResponse response = authService.redefinirSenha(request);
        assertThat(response.mensagem()).isEqualTo("Senha redefinida com sucesso.");
        assertThat(usuario.getSenhaHash()).isEqualTo("hash-nova");
        assertThat(resetToken.getUsadoEm()).isNotNull();
    }

    @Test
    void login_deveRetornarDesafioMfaQuandoAdminComMfaAtivo() {
        Usuario usuario = new Usuario("admin", "admin@example.com", "hash-real", Role.ADMIN);
        setId(usuario, UUID.randomUUID());
        usuario.iniciarEnrollmentMfa("secret-cifrado");
        usuario.confirmarMfa();
        LoginRequest request = new LoginRequest("admin", "senhaCorreta123", "captcha-token");
        when(usuarioRepository.findByApelidoIgnoreCaseOrEmailIgnoreCase("admin", "admin"))
                .thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("senhaCorreta123", "hash-real")).thenReturn(true);
        LoginResponse response = authService.login(request);
        assertThat(response.mfaRequerido()).isTrue();
        assertThat(response.token()).isNull();
        verifyNoInteractions(jwtService);
    }

    @Test
    void loginComMfa_deveFalharQuandoUsuarioNaoEAdmin() {
        Usuario usuario = new Usuario("participante", "p@example.com", "hash-real", Role.PARTICIPANTE);
        setId(usuario, UUID.randomUUID());
        MfaLoginRequest request = new MfaLoginRequest("participante", "senhaCorreta123", "captcha-token", "123456");
        when(usuarioRepository.findByApelidoIgnoreCaseOrEmailIgnoreCase("participante", "participante"))
                .thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("senhaCorreta123", "hash-real")).thenReturn(true);
        assertThatThrownBy(() -> authService.loginComMfa(request))
                .isInstanceOf(UnauthenticatedException.class)
                .hasMessage("Identificador ou senha inválidos");
        verifyNoInteractions(mfaSecretCipher, totpService);
    }

    @Test
    void loginComMfa_deveFalharQuandoAdminComMfaInativo() {
        Usuario usuario = new Usuario("admin", "admin@example.com", "hash-real", Role.ADMIN);
        setId(usuario, UUID.randomUUID());
        MfaLoginRequest request = new MfaLoginRequest("admin", "senhaCorreta123", "captcha-token", "123456");
        when(usuarioRepository.findByApelidoIgnoreCaseOrEmailIgnoreCase("admin", "admin"))
                .thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("senhaCorreta123", "hash-real")).thenReturn(true);
        assertThatThrownBy(() -> authService.loginComMfa(request))
                .isInstanceOf(UnauthenticatedException.class)
                .hasMessage("Identificador ou senha inválidos");
        verifyNoInteractions(mfaSecretCipher, totpService);
    }

    @Test
    void loginComMfa_deveFalharQuandoCodigoTotpInvalido() {
        Usuario usuario = new Usuario("admin", "admin@example.com", "hash-real", Role.ADMIN);
        setId(usuario, UUID.randomUUID());
        usuario.iniciarEnrollmentMfa("secret-cifrado");
        usuario.confirmarMfa();
        MfaLoginRequest request = new MfaLoginRequest("admin", "senhaCorreta123", "captcha-token", "000000");
        when(usuarioRepository.findByApelidoIgnoreCaseOrEmailIgnoreCase("admin", "admin"))
                .thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("senhaCorreta123", "hash-real")).thenReturn(true);
        when(mfaSecretCipher.decifrar("secret-cifrado")).thenReturn("JBSWY3DPEHPK3PXP");
        when(totpService.validar("JBSWY3DPEHPK3PXP", "000000")).thenReturn(false);
        assertThatThrownBy(() -> authService.loginComMfa(request))
                .isInstanceOf(UnauthenticatedException.class)
                .hasMessage("Identificador ou senha inválidos");
        verifyNoInteractions(jwtService);
    }

    @Test
    void loginComMfa_deveEmitirTokenQuandoCodigoTotpValido() {
        Usuario usuario = new Usuario("admin", "admin@example.com", "hash-real", Role.ADMIN);
        setId(usuario, UUID.randomUUID());
        usuario.iniciarEnrollmentMfa("secret-cifrado");
        usuario.confirmarMfa();
        MfaLoginRequest request = new MfaLoginRequest("admin", "senhaCorreta123", "captcha-token", "123456");
        when(usuarioRepository.findByApelidoIgnoreCaseOrEmailIgnoreCase("admin", "admin"))
                .thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("senhaCorreta123", "hash-real")).thenReturn(true);
        when(mfaSecretCipher.decifrar("secret-cifrado")).thenReturn("JBSWY3DPEHPK3PXP");
        when(totpService.validar("JBSWY3DPEHPK3PXP", "123456")).thenReturn(true);
        when(jwtService.gerarToken(any(), anyString(), any())).thenReturn("jwt-token");
        when(jwtService.expiracaoDe("jwt-token")).thenReturn(Instant.now().plusSeconds(3600));
        LoginResponse response = authService.loginComMfa(request);
        assertThat(response.mfaRequerido()).isFalse();
        assertThat(response.token()).isEqualTo("jwt-token");
        assertThat(response.usuario().apelido()).isEqualTo("admin");
    }

    @Test
    void confirmarEmail_deveFalharQuandoTokenVazio() {
        assertThatThrownBy(() -> authService.confirmarEmail(" "))
                .isInstanceOf(ValidationException.class);
        verifyNoInteractions(emailConfirmacaoTokenRepository);
    }

    @Test
    void confirmarEmail_deveFalharQuandoTokenNaoEncontrado() {
        when(emailConfirmacaoTokenRepository.findByTokenHash(anyString())).thenReturn(Optional.empty());
        assertThatThrownBy(() -> authService.confirmarEmail("token-inexistente"))
                .isInstanceOf(UnauthenticatedException.class)
                .hasMessage("Link de confirmação inválido ou expirado");
    }

    @Test
    void confirmarEmail_deveFalharQuandoUsuarioDoTokenNaoExisteMais() {
        UUID usuarioId = UUID.randomUUID();
        EmailConfirmacaoToken token = new EmailConfirmacaoToken(usuarioId, "hash:token-valido", Instant.now().plusSeconds(3600));
        when(emailConfirmacaoTokenRepository.findByTokenHash(anyString())).thenReturn(Optional.of(token));
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> authService.confirmarEmail("token-valido"))
                .isInstanceOf(UnauthenticatedException.class)
                .hasMessage("Link de confirmação inválido ou expirado");
    }

    @Test
    void confirmarEmail_deveFalharQuandoTokenExpiradoENuncaUsado() {
        UUID usuarioId = UUID.randomUUID();
        Usuario usuario = new Usuario("apelido", "email@example.com", "hash", Role.PARTICIPANTE);
        setId(usuario, usuarioId);
        EmailConfirmacaoToken token = new EmailConfirmacaoToken(usuarioId, "hash:token-expirado", Instant.now().minusSeconds(60));
        when(emailConfirmacaoTokenRepository.findByTokenHash(anyString())).thenReturn(Optional.of(token));
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));
        assertThatThrownBy(() -> authService.confirmarEmail("token-expirado"))
                .isInstanceOf(UnauthenticatedException.class)
                .hasMessage("Link de confirmação inválido ou expirado");
        assertThat(usuario.getEmailConfirmadoEm()).isNull();
    }

    @Test
    void confirmarEmail_deveConfirmarEMarcarTokenUsadoQuandoValido() {
        UUID usuarioId = UUID.randomUUID();
        Usuario usuario = new Usuario("apelido", "email@example.com", "hash", Role.PARTICIPANTE);
        setId(usuario, usuarioId);
        EmailConfirmacaoToken token = new EmailConfirmacaoToken(usuarioId, "hash:token-valido", Instant.now().plusSeconds(3600));
        when(emailConfirmacaoTokenRepository.findByTokenHash(anyString())).thenReturn(Optional.of(token));
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));
        MensagemResponse response = authService.confirmarEmail("token-valido");
        assertThat(response.mensagem()).isEqualTo("E-mail confirmado com sucesso.");
        assertThat(usuario.getEmailConfirmadoEm()).isNotNull();
        assertThat(token.getUsadoEm()).isNotNull();
    }

    @Test
    void confirmarEmail_deveSerIdempotenteQuandoContaJaConfirmadaComTokenJaUsado() {
        UUID usuarioId = UUID.randomUUID();
        Usuario usuario = new Usuario("apelido", "email@example.com", "hash", Role.PARTICIPANTE);
        setId(usuario, usuarioId);
        usuario.confirmarEmail();
        EmailConfirmacaoToken token = new EmailConfirmacaoToken(usuarioId, "hash:token-ja-usado", Instant.now().plusSeconds(3600));
        token.marcarUsado();
        when(emailConfirmacaoTokenRepository.findByTokenHash(anyString())).thenReturn(Optional.of(token));
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));
        MensagemResponse response = authService.confirmarEmail("token-ja-usado");
        assertThat(response.mensagem()).isEqualTo("E-mail confirmado com sucesso.");
    }

    @Test
    void confirmarEmail_deveTrocarEmailQuandoTipoTrocaEmailEValido() {
        UUID usuarioId = UUID.randomUUID();
        Usuario usuario = new Usuario("apelido", "antigo@example.com", "hash", Role.PARTICIPANTE);
        setId(usuario, usuarioId);
        EmailConfirmacaoToken token = new EmailConfirmacaoToken(usuarioId, "hash:token-valido", Instant.now().plusSeconds(3600),
                TipoEmailConfirmacaoToken.TROCA_EMAIL, "novo@example.com");
        when(emailConfirmacaoTokenRepository.findByTokenHash(anyString())).thenReturn(Optional.of(token));
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.existsByEmailIgnoreCase("novo@example.com")).thenReturn(false);
        MensagemResponse response = authService.confirmarEmail("token-valido");
        assertThat(response.mensagem()).isEqualTo("E-mail confirmado com sucesso.");
        assertThat(usuario.getEmail()).isEqualTo("novo@example.com");
        assertThat(usuario.getEmailConfirmadoEm()).isNotNull();
        assertThat(token.getUsadoEm()).isNotNull();
    }

    @Test
    void confirmarEmail_deveFalharQuandoTipoTrocaEmailComNovoEmailJaEmUso() {
        UUID usuarioId = UUID.randomUUID();
        Usuario usuario = new Usuario("apelido", "antigo@example.com", "hash", Role.PARTICIPANTE);
        setId(usuario, usuarioId);
        EmailConfirmacaoToken token = new EmailConfirmacaoToken(usuarioId, "hash:token-valido", Instant.now().plusSeconds(3600),
                TipoEmailConfirmacaoToken.TROCA_EMAIL, "novo@example.com");
        when(emailConfirmacaoTokenRepository.findByTokenHash(anyString())).thenReturn(Optional.of(token));
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.existsByEmailIgnoreCase("novo@example.com")).thenReturn(true);
        assertThatThrownBy(() -> authService.confirmarEmail("token-valido"))
                .isInstanceOf(ConflitoDeEstadoException.class);
        assertThat(usuario.getEmail()).isEqualTo("antigo@example.com");
    }

    @Test
    void confirmarEmail_deveFalharComConflitoQuandoFlushViolaConstraintUnicaConcorrente() {
        UUID usuarioId = UUID.randomUUID();
        Usuario usuario = new Usuario("apelido", "antigo@example.com", "hash", Role.PARTICIPANTE);
        setId(usuario, usuarioId);
        EmailConfirmacaoToken token = new EmailConfirmacaoToken(usuarioId, "hash:token-valido", Instant.now().plusSeconds(3600),
                TipoEmailConfirmacaoToken.TROCA_EMAIL, "novo@example.com");
        when(emailConfirmacaoTokenRepository.findByTokenHash(anyString())).thenReturn(Optional.of(token));
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.existsByEmailIgnoreCase("novo@example.com")).thenReturn(false);
        doThrow(new DataIntegrityViolationException("duplicate key")).when(usuarioRepository).flush();
        assertThatThrownBy(() -> authService.confirmarEmail("token-valido"))
                .isInstanceOf(ConflitoDeEstadoException.class)
                .hasMessage("Esse e-mail já está em uso por outra conta");
        assertThat(token.getUsadoEm()).isNull();
    }

    @Test
    void confirmarEmail_deveFalharQuandoTipoTrocaEmailComTokenExpiradoENuncaUsado() {
        UUID usuarioId = UUID.randomUUID();
        Usuario usuario = new Usuario("apelido", "antigo@example.com", "hash", Role.PARTICIPANTE);
        setId(usuario, usuarioId);
        EmailConfirmacaoToken token = new EmailConfirmacaoToken(usuarioId, "hash:token-expirado", Instant.now().minusSeconds(60),
                TipoEmailConfirmacaoToken.TROCA_EMAIL, "novo@example.com");
        when(emailConfirmacaoTokenRepository.findByTokenHash(anyString())).thenReturn(Optional.of(token));
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));
        assertThatThrownBy(() -> authService.confirmarEmail("token-expirado"))
                .isInstanceOf(UnauthenticatedException.class)
                .hasMessage("Link de confirmação inválido ou expirado");
        assertThat(usuario.getEmail()).isEqualTo("antigo@example.com");
        verify(usuarioRepository, never()).existsByEmailIgnoreCase(anyString());
    }

    @Test
    void confirmarEmail_trocaEmailDeveSerIdempotenteQuandoEmailJaIgualAoNovoEmail() {
        UUID usuarioId = UUID.randomUUID();
        Usuario usuario = new Usuario("apelido", "novo@example.com", "hash", Role.PARTICIPANTE);
        setId(usuario, usuarioId);
        EmailConfirmacaoToken token = new EmailConfirmacaoToken(usuarioId, "hash:token-ja-usado", Instant.now().plusSeconds(3600),
                TipoEmailConfirmacaoToken.TROCA_EMAIL, "novo@example.com");
        token.marcarUsado();
        when(emailConfirmacaoTokenRepository.findByTokenHash(anyString())).thenReturn(Optional.of(token));
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));
        MensagemResponse response = authService.confirmarEmail("token-ja-usado");
        assertThat(response.mensagem()).isEqualTo("E-mail confirmado com sucesso.");
    }

    @Test
    void gerarEEnviarTokenTrocaEmail_deveInvalidarTokenPendenteAnteriorEDispararNovoEnvio() {
        UUID usuarioId = UUID.randomUUID();
        Usuario usuario = new Usuario("apelido", "atual@example.com", "hash", Role.PARTICIPANTE);
        setId(usuario, usuarioId);
        EmailConfirmacaoToken tokenAnterior = new EmailConfirmacaoToken(usuarioId, "hash:token-anterior", Instant.now().plusSeconds(3600),
                TipoEmailConfirmacaoToken.TROCA_EMAIL, "outro@example.com");
        when(emailConfirmacaoTokenRepository.findByUsuarioIdAndTipoAndUsadoEmIsNull(usuarioId, TipoEmailConfirmacaoToken.TROCA_EMAIL))
                .thenReturn(java.util.List.of(tokenAnterior));
        authService.gerarEEnviarTokenTrocaEmail(usuario, "novo@example.com");
        assertThat(tokenAnterior.getUsadoEm()).isNotNull();
        verify(emailConfirmacaoTokenRepository).save(argThat(token ->
                token.getTipo() == TipoEmailConfirmacaoToken.TROCA_EMAIL && "novo@example.com".equals(token.getNovoEmail())));
        verify(emailService).enviarLinkConfirmacaoEmail(anyString(), eq("atual@example.com"), anyString(), eq(TipoEmailConfirmacaoToken.TROCA_EMAIL));
    }

    @Test
    void gerarEEnviarTokenTrocaEmail_deveGerarTokenComTtlDe30Minutos() {
        UUID usuarioId = UUID.randomUUID();
        Usuario usuario = new Usuario("apelido", "atual@example.com", "hash", Role.PARTICIPANTE);
        setId(usuario, usuarioId);
        when(emailConfirmacaoTokenRepository.findByUsuarioIdAndTipoAndUsadoEmIsNull(usuarioId, TipoEmailConfirmacaoToken.TROCA_EMAIL))
                .thenReturn(java.util.List.of());
        Instant antes = Instant.now();
        authService.gerarEEnviarTokenTrocaEmail(usuario, "novo@example.com");
        Instant depois = Instant.now();
        verify(emailConfirmacaoTokenRepository).save(argThat(token ->
                !token.getExpiraEm().isBefore(antes.plus(Duration.ofMinutes(30)).minusSeconds(5))
                        && !token.getExpiraEm().isAfter(depois.plus(Duration.ofMinutes(30)).plusSeconds(5))));
    }

    @Test
    void reenviarConfirmacaoEmail_respostaGenericaQuandoContaNaoExiste() {
        ReenviarConfirmacaoEmailRequest request = new ReenviarConfirmacaoEmailRequest("naoexiste@example.com");
        when(usuarioRepository.findByApelidoIgnoreCaseOrEmailIgnoreCase(anyString(), anyString()))
                .thenReturn(Optional.empty());
        MensagemResponse response = authService.reenviarConfirmacaoEmail(request);
        assertThat(response.mensagem()).contains("Se o apelido ou e-mail existir");
        verifyNoInteractions(emailService, emailConfirmacaoTokenRepository);
    }

    @Test
    void reenviarConfirmacaoEmail_respostaGenericaQuandoContaJaConfirmada() {
        Usuario usuario = new Usuario("apelido", "email@example.com", "hash", Role.PARTICIPANTE);
        setId(usuario, UUID.randomUUID());
        usuario.confirmarEmail();
        ReenviarConfirmacaoEmailRequest request = new ReenviarConfirmacaoEmailRequest("apelido");
        when(usuarioRepository.findByApelidoIgnoreCaseOrEmailIgnoreCase(anyString(), anyString()))
                .thenReturn(Optional.of(usuario));
        MensagemResponse response = authService.reenviarConfirmacaoEmail(request);
        assertThat(response.mensagem()).contains("Se o apelido ou e-mail existir");
        verifyNoInteractions(emailService, emailConfirmacaoTokenRepository);
    }

    @Test
    void reenviarConfirmacaoEmail_disparaNovoTokenQuandoContaPendente() {
        UUID usuarioId = UUID.randomUUID();
        Usuario usuario = new Usuario("apelido", "email@example.com", "hash", Role.PARTICIPANTE);
        setId(usuario, usuarioId);
        ReenviarConfirmacaoEmailRequest request = new ReenviarConfirmacaoEmailRequest("apelido");
        when(usuarioRepository.findByApelidoIgnoreCaseOrEmailIgnoreCase(anyString(), anyString()))
                .thenReturn(Optional.of(usuario));
        MensagemResponse response = authService.reenviarConfirmacaoEmail(request);
        assertThat(response.mensagem()).contains("Se o apelido ou e-mail existir");
        verify(emailConfirmacaoTokenRepository).save(any(EmailConfirmacaoToken.class));
        verify(emailService).enviarLinkConfirmacaoEmail(anyString(), anyString(), anyString(), eq(TipoEmailConfirmacaoToken.CADASTRO));
    }

    @Test
    void reenviarConfirmacaoEmail_respostaGenericaQuandoRateLimitPorUsuarioEstourado() {
        UUID usuarioId = UUID.randomUUID();
        Usuario usuario = new Usuario("apelido", "email@example.com", "hash", Role.PARTICIPANTE);
        setId(usuario, usuarioId);
        ReenviarConfirmacaoEmailRequest request = new ReenviarConfirmacaoEmailRequest("apelido");
        when(usuarioRepository.findByApelidoIgnoreCaseOrEmailIgnoreCase(anyString(), anyString()))
                .thenReturn(Optional.of(usuario));
        for (int i = 0; i < 3; i++) {
            authService.reenviarConfirmacaoEmail(request);
        }
        MensagemResponse response = authService.reenviarConfirmacaoEmail(request);
        assertThat(response.mensagem()).contains("Se o apelido ou e-mail existir");
        verify(emailConfirmacaoTokenRepository, times(3)).save(any(EmailConfirmacaoToken.class));
        verify(emailService, times(3)).enviarLinkConfirmacaoEmail(anyString(), anyString(), anyString(), eq(TipoEmailConfirmacaoToken.CADASTRO));
    }

    @Test
    void reenviarConfirmacaoEmail_deveEstourarRateLimitGlobalDeVarredura() {
        ReenviarConfirmacaoEmailRequest request = new ReenviarConfirmacaoEmailRequest("qualquer@example.com");
        when(usuarioRepository.findByApelidoIgnoreCaseOrEmailIgnoreCase(anyString(), anyString()))
                .thenReturn(Optional.empty());
        for (int i = 0; i < 30; i++) {
            authService.reenviarConfirmacaoEmail(request);
        }
        assertThatThrownBy(() -> authService.reenviarConfirmacaoEmail(request))
                .isInstanceOf(RateLimitedException.class);
    }

    @Test
    void logout_deveFalharQuandoPrincipalNulo() {
        assertThatThrownBy(() -> authService.logout(null))
                .isInstanceOf(UnauthenticatedException.class);
    }

    @Test
    void logout_deveInserirJtiNaDenylistQuandoAindaNaoRevogado() {
        AuthenticatedUser principal = new AuthenticatedUser(UUID.randomUUID(), "apelido", "PARTICIPANTE",
                "jti-123", Instant.now().plusSeconds(3600));
        when(jwtDenylistRepository.existsByJti("jti-123")).thenReturn(false);
        MensagemResponse response = authService.logout(principal);
        assertThat(response.mensagem()).isEqualTo("Sessão encerrada com sucesso.");
        verify(jwtDenylistRepository).save(any(JwtDenylistEntry.class));
    }

    @Test
    void logout_naoDeveDuplicarQuandoJtiJaRevogado() {
        AuthenticatedUser principal = new AuthenticatedUser(UUID.randomUUID(), "apelido", "PARTICIPANTE",
                "jti-123", Instant.now().plusSeconds(3600));
        when(jwtDenylistRepository.existsByJti("jti-123")).thenReturn(true);
        authService.logout(principal);
        verify(jwtDenylistRepository, never()).save(any());
    }

    private void setId(Usuario usuario, UUID id) {
        try {
            var field = Usuario.class.getDeclaredField("id");
            field.setAccessible(true);
            field.set(usuario, id);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }
}
