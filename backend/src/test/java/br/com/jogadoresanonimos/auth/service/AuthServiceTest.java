package br.com.jogadoresanonimos.auth.service;

import br.com.jogadoresanonimos.auth.dto.*;
import br.com.jogadoresanonimos.auth.entity.PasswordResetToken;
import br.com.jogadoresanonimos.auth.repository.PasswordResetTokenRepository;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;

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
        authService = new AuthService(usuarioRepository, resetTokenRepository, passwordEncoder, jwtService,
                emailService, rateLimiterService, hmacHasher, recaptchaService, totpService, mfaSecretCipher,
                jwtDenylistRepository);
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
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));
        UsuarioResponse response = authService.registrar(request);
        assertThat(response.apelido()).isEqualTo("novato");
        assertThat(response.role()).isEqualTo(Role.PARTICIPANTE);
        verify(usuarioRepository).save(any(Usuario.class));
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
    void esqueciSenha_deveRetornarMensagemGenericaMesmoSemUsuarioEncontrado() {
        EsqueciSenhaRequest request = new EsqueciSenhaRequest("naoexiste@example.com");
        when(usuarioRepository.findByApelidoIgnoreCaseOrEmailIgnoreCase(anyString(), anyString()))
                .thenReturn(Optional.empty());
        MensagemResponse response = authService.esqueciSenha(request);
        assertThat(response.mensagem()).contains("Se o apelido ou e-mail existir");
        verifyNoInteractions(emailService);
        verify(resetTokenRepository, never()).save(any());
    }

    @Test
    void esqueciSenha_devePersistirTokenEDispararEmailQuandoUsuarioExiste() {
        UUID usuarioId = UUID.randomUUID();
        Usuario usuario = new Usuario("apelido", "email@example.com", "hash", Role.PARTICIPANTE);
        setId(usuario, usuarioId);
        EsqueciSenhaRequest request = new EsqueciSenhaRequest("apelido");
        when(usuarioRepository.findByApelidoIgnoreCaseOrEmailIgnoreCase(anyString(), anyString()))
                .thenReturn(Optional.of(usuario));
        authService.esqueciSenha(request);
        verify(resetTokenRepository).save(any(PasswordResetToken.class));
        verify(emailService).enviarLinkRedefinicaoSenha(anyString(), anyString());
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
