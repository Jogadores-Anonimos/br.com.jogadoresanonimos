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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
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

    RateLimiterService rateLimiterService;

    @InjectMocks
    AuthService authService;

    @BeforeEach
    void setUp() {
        rateLimiterService = new RateLimiterService();
        authService = new AuthService(usuarioRepository, resetTokenRepository, passwordEncoder, jwtService,
                emailService, rateLimiterService, hmacHasher);
        lenientHmac();
    }

    private void lenientHmac() {
        lenient().when(hmacHasher.hash(anyString())).thenAnswer(inv -> "hash:" + inv.getArgument(0));
    }

    @Test
    void registrar_deveCriarUsuarioComSenhaHasheada() {
        RegistroRequest request = new RegistroRequest("novato", "novato@example.com", "senhaForte123", "senhaForte123");
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
        RegistroRequest request = new RegistroRequest("novato", "novato@example.com", "senhaForte123", "outraSenha123");

        assertThatThrownBy(() -> authService.registrar(request))
                .isInstanceOf(ValidationException.class);
        verifyNoInteractions(usuarioRepository);
    }

    @Test
    void registrar_deveFalharComConflitoGenericoQuandoApelidoOuEmailJaExiste() {
        RegistroRequest request = new RegistroRequest("existente", "novo@example.com", "senhaForte123", "senhaForte123");
        when(usuarioRepository.existsByApelidoIgnoreCase("existente")).thenReturn(true);

        assertThatThrownBy(() -> authService.registrar(request))
                .isInstanceOf(ConflitoDeEstadoException.class)
                .hasMessage("Não foi possível concluir o cadastro com os dados informados");
    }

    @Test
    void login_deveFalharComMensagemGenericaQuandoUsuarioNaoExiste() {
        LoginRequest request = new LoginRequest("desconhecido", "qualquerSenha");
        when(usuarioRepository.findByApelidoIgnoreCaseOrEmailIgnoreCase("desconhecido", "desconhecido"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(UnauthenticatedException.class)
                .hasMessage("Identificador ou senha inválidos");
    }

    @Test
    void login_deveFalharComMensagemGenericaQuandoSenhaIncorreta() {
        Usuario usuario = new Usuario("apelido", "email@example.com", "hash-real", Role.PARTICIPANTE);
        LoginRequest request = new LoginRequest("apelido", "senhaErrada");
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
        LoginRequest request = new LoginRequest("apelido", "senhaCorreta123");
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
