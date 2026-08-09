package br.com.jogadoresanonimos.auth.service;

import br.com.jogadoresanonimos.auth.dto.AdminRedefinirSenhaRequest;
import br.com.jogadoresanonimos.auth.dto.MensagemResponse;
import br.com.jogadoresanonimos.auth.entity.AdminResetCodigo;
import br.com.jogadoresanonimos.auth.repository.AdminResetCodigoRepository;
import br.com.jogadoresanonimos.common.exception.RateLimitedException;
import br.com.jogadoresanonimos.common.exception.UnauthenticatedException;
import br.com.jogadoresanonimos.common.exception.ValidationException;
import br.com.jogadoresanonimos.common.ratelimit.RateLimiterService;
import br.com.jogadoresanonimos.common.util.HmacHasher;
import br.com.jogadoresanonimos.usuario.entity.Usuario;
import br.com.jogadoresanonimos.usuario.entity.Role;
import br.com.jogadoresanonimos.usuario.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
class AdminAuthServiceTest {

    @Mock
    UsuarioRepository usuarioRepository;
    @Mock
    AdminResetCodigoRepository adminResetCodigoRepository;
    @Mock
    PasswordEncoder passwordEncoder;
    @Mock
    HmacHasher hmacHasher;

    RateLimiterService rateLimiterService;

    AdminAuthService adminAuthService;

    @BeforeEach
    void setUp() {
        rateLimiterService = new RateLimiterService();
        adminAuthService = new AdminAuthService(usuarioRepository, adminResetCodigoRepository, passwordEncoder,
                rateLimiterService, hmacHasher);
        lenient().when(hmacHasher.hash(anyString())).thenAnswer(inv -> "hash:" + inv.getArgument(0));
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
    void redefinirSenha_deveFalharComErroGenericoQuandoCodigoNaoExiste() {
        AdminRedefinirSenhaRequest request = new AdminRedefinirSenhaRequest("123456", "novaSenha1234", "novaSenha1234");
        when(adminResetCodigoRepository.findValidoPorCodigoHash(anyString(), any(Instant.class)))
                .thenReturn(Optional.empty());
        assertThatThrownBy(() -> adminAuthService.redefinirSenha(request))
                .isInstanceOf(UnauthenticatedException.class);
        verify(usuarioRepository, never()).findById(any());
    }

    @Test
    void redefinirSenha_deveFalharComMesmoErroGenericoQuandoCodigoExpiradoOuJaUsado() {
        AdminRedefinirSenhaRequest request = new AdminRedefinirSenhaRequest("654321", "novaSenha1234", "novaSenha1234");
        when(adminResetCodigoRepository.findValidoPorCodigoHash(anyString(), any(Instant.class)))
                .thenReturn(Optional.empty());
        assertThatThrownBy(() -> adminAuthService.redefinirSenha(request))
                .isInstanceOf(UnauthenticatedException.class)
                .hasMessage("Código inválido ou expirado");
    }

    @Test
    void redefinirSenha_deveFalharQuandoConfirmacaoNaoCoincide() {
        UUID usuarioId = UUID.randomUUID();
        AdminResetCodigo codigo = new AdminResetCodigo(usuarioId, "hash:123456", Instant.now().plusSeconds(600));
        AdminRedefinirSenhaRequest request = new AdminRedefinirSenhaRequest("123456", "novaSenha1234", "outraSenha1234");
        when(adminResetCodigoRepository.findValidoPorCodigoHash(anyString(), any(Instant.class)))
                .thenReturn(Optional.of(codigo));
        assertThatThrownBy(() -> adminAuthService.redefinirSenha(request))
                .isInstanceOf(ValidationException.class);
        assertThat(codigo.getUsadoEm()).isNull();
        verify(usuarioRepository, never()).findById(any());
    }

    @Test
    void redefinirSenha_deveFalharComErroGenericoQuandoCodigoValidoMasUsuarioNaoExisteMais() {
        UUID usuarioId = UUID.randomUUID();
        AdminResetCodigo codigo = new AdminResetCodigo(usuarioId, "hash:123456", Instant.now().plusSeconds(600));
        AdminRedefinirSenhaRequest request = new AdminRedefinirSenhaRequest("123456", "novaSenha1234", "novaSenha1234");
        when(adminResetCodigoRepository.findValidoPorCodigoHash(anyString(), any(Instant.class)))
                .thenReturn(Optional.of(codigo));
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> adminAuthService.redefinirSenha(request))
                .isInstanceOf(UnauthenticatedException.class)
                .hasMessage("Código inválido ou expirado");
        assertThat(codigo.getUsadoEm()).isNull();
    }

    @Test
    void redefinirSenha_deveTrocarSenhaEMarcarCodigoComoUsadoQuandoValido() {
        UUID usuarioId = UUID.randomUUID();
        Usuario usuario = new Usuario("admin", "admin@example.com", "hash-antigo", Role.ADMIN);
        setId(usuario, usuarioId);
        AdminResetCodigo codigo = new AdminResetCodigo(usuarioId, "hash:123456", Instant.now().plusSeconds(600));
        AdminRedefinirSenhaRequest request = new AdminRedefinirSenhaRequest("123456", "novaSenha1234", "novaSenha1234");
        when(adminResetCodigoRepository.findValidoPorCodigoHash(anyString(), any(Instant.class)))
                .thenReturn(Optional.of(codigo));
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));
        when(passwordEncoder.encode("novaSenha1234")).thenReturn("hash-nova");
        MensagemResponse response = adminAuthService.redefinirSenha(request);
        assertThat(response.mensagem()).isEqualTo("Senha redefinida com sucesso.");
        assertThat(usuario.getSenhaHash()).isEqualTo("hash-nova");
        assertThat(codigo.getUsadoEm()).isNotNull();
    }

    @Test
    void redefinirSenha_sucessoNaoDeveTocarMfaAtivoNemMfaSecretCifrado() {
        UUID usuarioId = UUID.randomUUID();
        Usuario usuario = new Usuario("admin", "admin@example.com", "hash-antigo", Role.ADMIN);
        setId(usuario, usuarioId);
        usuario.iniciarEnrollmentMfa("secret-cifrado-original");
        usuario.confirmarMfa();
        AdminResetCodigo codigo = new AdminResetCodigo(usuarioId, "hash:123456", Instant.now().plusSeconds(600));
        AdminRedefinirSenhaRequest request = new AdminRedefinirSenhaRequest("123456", "novaSenha1234", "novaSenha1234");
        when(adminResetCodigoRepository.findValidoPorCodigoHash(anyString(), any(Instant.class)))
                .thenReturn(Optional.of(codigo));
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));
        when(passwordEncoder.encode("novaSenha1234")).thenReturn("hash-nova");
        adminAuthService.redefinirSenha(request);
        assertThat(usuario.isMfaAtivo()).isTrue();
        assertThat(usuario.getMfaSecretCifrado()).isEqualTo("secret-cifrado-original");
    }

    @Test
    void redefinirSenha_deveEstourarRateLimitPorCodigoNaSextaChamada() {
        AdminRedefinirSenhaRequest request = new AdminRedefinirSenhaRequest("111111", "novaSenha1234", "novaSenha1234");
        lenient().when(adminResetCodigoRepository.findValidoPorCodigoHash(anyString(), any(Instant.class)))
                .thenReturn(Optional.empty());
        for (int i = 0; i < 5; i++) {
            assertThatThrownBy(() -> adminAuthService.redefinirSenha(request))
                    .isInstanceOf(UnauthenticatedException.class);
        }
        assertThatThrownBy(() -> adminAuthService.redefinirSenha(request))
                .isInstanceOf(RateLimitedException.class);
    }
}
