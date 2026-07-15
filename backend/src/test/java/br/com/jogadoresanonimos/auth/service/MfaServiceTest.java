package br.com.jogadoresanonimos.auth.service;

import br.com.jogadoresanonimos.auth.dto.MfaConfirmarRequest;
import br.com.jogadoresanonimos.auth.dto.MfaDesativarRequest;
import br.com.jogadoresanonimos.auth.dto.MfaEnrollResponse;
import br.com.jogadoresanonimos.auth.dto.MensagemResponse;
import br.com.jogadoresanonimos.common.exception.ConflitoDeEstadoException;
import br.com.jogadoresanonimos.common.exception.RateLimitedException;
import br.com.jogadoresanonimos.common.exception.RecursoNaoEncontradoException;
import br.com.jogadoresanonimos.common.exception.UnauthenticatedException;
import br.com.jogadoresanonimos.common.ratelimit.RateLimiterService;
import br.com.jogadoresanonimos.security.MfaSecretCipher;
import br.com.jogadoresanonimos.security.TotpService;
import br.com.jogadoresanonimos.usuario.entity.Role;
import br.com.jogadoresanonimos.usuario.entity.Usuario;
import br.com.jogadoresanonimos.usuario.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MfaServiceTest {

    @Mock
    UsuarioRepository usuarioRepository;
    @Mock
    TotpService totpService;
    @Mock
    MfaSecretCipher mfaSecretCipher;
    @Mock
    PasswordEncoder passwordEncoder;

    RateLimiterService rateLimiterService;
    MfaService mfaService;

    @BeforeEach
    void setUp() {
        rateLimiterService = new RateLimiterService();
        mfaService = new MfaService(usuarioRepository, totpService, mfaSecretCipher, passwordEncoder,
                rateLimiterService, "JogadoresAnonimos");
    }

    private Usuario usuarioComId(UUID id, Role role, boolean mfaAtivo) {
        Usuario usuario = new Usuario("apelido", "email@example.com", "hash-senha", role);
        setId(usuario, id);
        if (mfaAtivo) {
            usuario.iniciarEnrollmentMfa("secret-cifrado");
            usuario.confirmarMfa();
        }
        return usuario;
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

    @Test
    void enroll_deveFalharQuandoUsuarioNaoEncontrado() {
        UUID usuarioId = UUID.randomUUID();
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> mfaService.enroll(usuarioId))
                .isInstanceOf(RecursoNaoEncontradoException.class);
    }

    @Test
    void enroll_deveFalharComConflitoQuandoMfaJaAtivo() {
        UUID usuarioId = UUID.randomUUID();
        Usuario usuario = usuarioComId(usuarioId, Role.ADMIN, true);
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));
        assertThatThrownBy(() -> mfaService.enroll(usuarioId))
                .isInstanceOf(ConflitoDeEstadoException.class)
                .hasMessage("MFA já está ativo para este usuário");
    }

    @Test
    void enroll_deveIniciarEnrollmentERetornarUriQuandoMfaInativo() {
        UUID usuarioId = UUID.randomUUID();
        Usuario usuario = usuarioComId(usuarioId, Role.ADMIN, false);
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));
        when(totpService.gerarSecretBase32()).thenReturn("JBSWY3DPEHPK3PXP");
        when(mfaSecretCipher.cifrar("JBSWY3DPEHPK3PXP")).thenReturn("secret-cifrado");
        when(totpService.construirOtpauthUri("JogadoresAnonimos", "apelido", "JBSWY3DPEHPK3PXP"))
                .thenReturn("otpauth://totp/uri");
        MfaEnrollResponse response = mfaService.enroll(usuarioId);
        assertThat(response.otpauthUri()).isEqualTo("otpauth://totp/uri");
        assertThat(usuario.getMfaSecretCifrado()).isEqualTo("secret-cifrado");
        assertThat(usuario.isMfaAtivo()).isFalse();
    }

    @Test
    void enroll_deveFalharComRateLimitAposExcederTentativas() {
        UUID usuarioId = UUID.randomUUID();
        Usuario usuario = usuarioComId(usuarioId, Role.ADMIN, false);
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));
        when(totpService.gerarSecretBase32()).thenReturn("JBSWY3DPEHPK3PXP");
        when(mfaSecretCipher.cifrar("JBSWY3DPEHPK3PXP")).thenReturn("secret-cifrado");
        assertThatThrownBy(() -> {
            for (int i = 0; i < 10; i++) {
                usuario.desativarMfa();
                mfaService.enroll(usuarioId);
            }
        }).isInstanceOf(RateLimitedException.class);
    }

    @Test
    void confirmar_deveFalharQuandoNaoHaEnrollmentPendente() {
        UUID usuarioId = UUID.randomUUID();
        Usuario usuario = usuarioComId(usuarioId, Role.ADMIN, false);
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));
        MfaConfirmarRequest request = new MfaConfirmarRequest("123456");
        assertThatThrownBy(() -> mfaService.confirmar(usuarioId, request))
                .isInstanceOf(RecursoNaoEncontradoException.class)
                .hasMessage("Nenhum enrollment de MFA pendente");
    }

    @Test
    void confirmar_deveFalharQuandoCodigoTotpInvalido() {
        UUID usuarioId = UUID.randomUUID();
        Usuario usuario = usuarioComId(usuarioId, Role.ADMIN, false);
        usuario.iniciarEnrollmentMfa("secret-cifrado");
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));
        when(mfaSecretCipher.decifrar("secret-cifrado")).thenReturn("JBSWY3DPEHPK3PXP");
        when(totpService.validar("JBSWY3DPEHPK3PXP", "000000")).thenReturn(false);
        MfaConfirmarRequest request = new MfaConfirmarRequest("000000");
        assertThatThrownBy(() -> mfaService.confirmar(usuarioId, request))
                .isInstanceOf(UnauthenticatedException.class)
                .hasMessage("Código TOTP inválido");
        assertThat(usuario.isMfaAtivo()).isFalse();
    }

    @Test
    void confirmar_deveFalharComConflitoQuandoMfaJaAtivo() {
        UUID usuarioId = UUID.randomUUID();
        Usuario usuario = usuarioComId(usuarioId, Role.ADMIN, true);
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));
        MfaConfirmarRequest request = new MfaConfirmarRequest("123456");
        assertThatThrownBy(() -> mfaService.confirmar(usuarioId, request))
                .isInstanceOf(ConflitoDeEstadoException.class)
                .hasMessage("MFA já está ativo para este usuário");
    }

    @Test
    void confirmar_deveAtivarMfaQuandoCodigoValido() {
        UUID usuarioId = UUID.randomUUID();
        Usuario usuario = usuarioComId(usuarioId, Role.ADMIN, false);
        usuario.iniciarEnrollmentMfa("secret-cifrado");
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));
        when(mfaSecretCipher.decifrar("secret-cifrado")).thenReturn("JBSWY3DPEHPK3PXP");
        when(totpService.validar("JBSWY3DPEHPK3PXP", "123456")).thenReturn(true);
        MfaConfirmarRequest request = new MfaConfirmarRequest("123456");
        MensagemResponse response = mfaService.confirmar(usuarioId, request);
        assertThat(response.mensagem()).isEqualTo("MFA ativado com sucesso.");
        assertThat(usuario.isMfaAtivo()).isTrue();
    }

    @Test
    void desativar_deveFalharQuandoSenhaIncorreta() {
        UUID usuarioId = UUID.randomUUID();
        Usuario usuario = usuarioComId(usuarioId, Role.ADMIN, true);
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("senhaErrada", "hash-senha")).thenReturn(false);
        MfaDesativarRequest request = new MfaDesativarRequest("senhaErrada");
        assertThatThrownBy(() -> mfaService.desativar(usuarioId, request))
                .isInstanceOf(UnauthenticatedException.class)
                .hasMessage("Senha incorreta");
        assertThat(usuario.isMfaAtivo()).isTrue();
    }

    @Test
    void desativar_deveDesativarMfaQuandoSenhaCorreta() {
        UUID usuarioId = UUID.randomUUID();
        Usuario usuario = usuarioComId(usuarioId, Role.ADMIN, true);
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("senhaCorreta", "hash-senha")).thenReturn(true);
        MfaDesativarRequest request = new MfaDesativarRequest("senhaCorreta");
        MensagemResponse response = mfaService.desativar(usuarioId, request);
        assertThat(response.mensagem()).isEqualTo("MFA desativado.");
        assertThat(usuario.isMfaAtivo()).isFalse();
    }

    @Test
    void desativar_deveFalharComRateLimitAposExcederTentativas() {
        UUID usuarioId = UUID.randomUUID();
        Usuario usuario = usuarioComId(usuarioId, Role.ADMIN, true);
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("senhaCorreta", "hash-senha")).thenReturn(true);
        MfaDesativarRequest request = new MfaDesativarRequest("senhaCorreta");
        assertThatThrownBy(() -> {
            for (int i = 0; i < 10; i++) {
                usuario.iniciarEnrollmentMfa("secret-cifrado");
                usuario.confirmarMfa();
                mfaService.desativar(usuarioId, request);
            }
        }).isInstanceOf(RateLimitedException.class);
    }
}
