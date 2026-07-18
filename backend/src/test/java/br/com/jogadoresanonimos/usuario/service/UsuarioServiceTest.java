package br.com.jogadoresanonimos.usuario.service;

import br.com.jogadoresanonimos.auditoria.entity.AcaoAuditoria;
import br.com.jogadoresanonimos.auditoria.service.AdminAuditoriaService;
import br.com.jogadoresanonimos.common.exception.ConflitoDeEstadoException;
import br.com.jogadoresanonimos.common.exception.RecursoNaoEncontradoException;
import br.com.jogadoresanonimos.common.exception.UnauthenticatedException;
import br.com.jogadoresanonimos.common.exception.ValidationException;
import br.com.jogadoresanonimos.common.ratelimit.RateLimiterService;
import br.com.jogadoresanonimos.usuario.dto.AlterarSenhaRequest;
import br.com.jogadoresanonimos.usuario.dto.AtualizarContaRequest;
import br.com.jogadoresanonimos.usuario.dto.UsuarioAdminResponse;
import br.com.jogadoresanonimos.usuario.dto.UsuarioMeResponse;
import br.com.jogadoresanonimos.usuario.entity.Role;
import br.com.jogadoresanonimos.usuario.entity.Usuario;
import br.com.jogadoresanonimos.usuario.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    UsuarioRepository usuarioRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @Mock
    RateLimiterService rateLimiterService;

    @Mock
    AdminAuditoriaService adminAuditoriaService;

    UsuarioService usuarioService;

    @BeforeEach
    void setUp() {
        usuarioService = new UsuarioService(usuarioRepository, passwordEncoder, rateLimiterService,
                adminAuditoriaService);
    }

    private Usuario usuarioComId(UUID id, String apelido, Role role) throws Exception {
        Usuario usuario = new Usuario(apelido, apelido + "@exemplo.com", "hash", role);
        Field idField = Usuario.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(usuario, id);
        return usuario;
    }

    @Test
    void listarTodos_deveMapearTodosOsUsuarios() throws Exception {
        Usuario usuario = usuarioComId(UUID.randomUUID(), "fulano", Role.PARTICIPANTE);
        when(usuarioRepository.findAllByOrderByCriadoEmDesc()).thenReturn(List.of(usuario));
        List<UsuarioAdminResponse> resultado = usuarioService.listarTodos();
        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).apelido()).isEqualTo("fulano");
        assertThat(resultado.get(0).email()).isEqualTo("f***@exemplo.com");
    }

    @Test
    void editarRole_deveAtualizarQuandoNaoHaConflito() throws Exception {
        UUID id = UUID.randomUUID();
        UUID solicitanteId = UUID.randomUUID();
        Usuario usuario = usuarioComId(id, "fulano", Role.PARTICIPANTE);
        when(usuarioRepository.findById(id)).thenReturn(Optional.of(usuario));
        UsuarioAdminResponse resultado = usuarioService.editarRole(id, Role.ADMIN, solicitanteId);
        assertThat(resultado.role()).isEqualTo(Role.ADMIN);
        verify(adminAuditoriaService).registrar(solicitanteId, "usuario", id, AcaoAuditoria.EDITAR,
                "PARTICIPANTE", "ADMIN");
    }

    @Test
    void editarRole_deveLancarExcecaoQuandoUsuarioNaoExiste() {
        UUID id = UUID.randomUUID();
        when(usuarioRepository.findById(id)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> usuarioService.editarRole(id, Role.ADMIN, UUID.randomUUID()))
                .isInstanceOf(RecursoNaoEncontradoException.class);
    }

    @Test
    void editarRole_deveImpedirAutoRebaixamento() throws Exception {
        UUID id = UUID.randomUUID();
        Usuario admin = usuarioComId(id, "admin", Role.ADMIN);
        when(usuarioRepository.findById(id)).thenReturn(Optional.of(admin));
        assertThatThrownBy(() -> usuarioService.editarRole(id, Role.PARTICIPANTE, id))
                .isInstanceOf(ConflitoDeEstadoException.class);
    }

    @Test
    void editarRole_devePermitirOutroAdminRebaixarQuandoHaMaisDeUmAdmin() throws Exception {
        UUID id = UUID.randomUUID();
        UUID solicitanteId = UUID.randomUUID();
        Usuario admin = usuarioComId(id, "admin", Role.ADMIN);
        when(usuarioRepository.findById(id)).thenReturn(Optional.of(admin));
        lenient().when(usuarioRepository.countByRole(Role.ADMIN)).thenReturn(2L);
        UsuarioAdminResponse resultado = usuarioService.editarRole(id, Role.PARTICIPANTE, solicitanteId);
        assertThat(resultado.role()).isEqualTo(Role.PARTICIPANTE);
    }

    @Test
    void editarRole_deveImpedirRemoverUltimoAdminDoSistema() throws Exception {
        UUID id = UUID.randomUUID();
        UUID solicitanteId = UUID.randomUUID();
        Usuario admin = usuarioComId(id, "admin", Role.ADMIN);
        when(usuarioRepository.findById(id)).thenReturn(Optional.of(admin));
        when(usuarioRepository.countByRole(Role.ADMIN)).thenReturn(1L);
        assertThatThrownBy(() -> usuarioService.editarRole(id, Role.PARTICIPANTE, solicitanteId))
                .isInstanceOf(ConflitoDeEstadoException.class);
    }

    @Test
    void editarRole_deveSerNoOpQuandoRoleJaEAMesma() throws Exception {
        UUID id = UUID.randomUUID();
        Usuario usuario = usuarioComId(id, "fulano", Role.PARTICIPANTE);
        when(usuarioRepository.findById(id)).thenReturn(Optional.of(usuario));
        UsuarioAdminResponse resultado = usuarioService.editarRole(id, Role.PARTICIPANTE, UUID.randomUUID());
        assertThat(resultado.role()).isEqualTo(Role.PARTICIPANTE);
        org.mockito.Mockito.verifyNoInteractions(adminAuditoriaService);
    }

    @Test
    void atualizarConta_deveAtualizarQuandoApelidoEEmailDisponiveis() throws Exception {
        UUID id = UUID.randomUUID();
        Usuario usuario = usuarioComId(id, "fulano", Role.PARTICIPANTE);
        when(usuarioRepository.findById(id)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.existsByApelidoIgnoreCase("novoApelido")).thenReturn(false);
        when(usuarioRepository.existsByEmailIgnoreCase("novo@exemplo.com")).thenReturn(false);
        UsuarioMeResponse resultado = usuarioService.atualizarConta(id,
                new AtualizarContaRequest("novoApelido", "novo@exemplo.com"));
        assertThat(resultado.apelido()).isEqualTo("novoApelido");
        assertThat(resultado.email()).isEqualTo("novo@exemplo.com");
    }

    @Test
    void atualizarConta_devePermitirReenviarOsMesmosDadosSemChecarUnicidadeContraSiMesmo() throws Exception {
        UUID id = UUID.randomUUID();
        Usuario usuario = usuarioComId(id, "fulano", Role.PARTICIPANTE);
        when(usuarioRepository.findById(id)).thenReturn(Optional.of(usuario));
        UsuarioMeResponse resultado = usuarioService.atualizarConta(id,
                new AtualizarContaRequest("fulano", "fulano@exemplo.com"));
        assertThat(resultado.apelido()).isEqualTo("fulano");
    }

    @Test
    void atualizarConta_deveLancarConflitoQuandoApelidoJaEstaEmUsoPorOutraConta() throws Exception {
        UUID id = UUID.randomUUID();
        Usuario usuario = usuarioComId(id, "fulano", Role.PARTICIPANTE);
        when(usuarioRepository.findById(id)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.existsByApelidoIgnoreCase("ocupado")).thenReturn(true);
        assertThatThrownBy(() -> usuarioService.atualizarConta(id,
                new AtualizarContaRequest("ocupado", "fulano@exemplo.com")))
                .isInstanceOf(ConflitoDeEstadoException.class);
    }

    @Test
    void atualizarConta_deveLancarConflitoQuandoEmailJaEstaEmUsoPorOutraConta() throws Exception {
        UUID id = UUID.randomUUID();
        Usuario usuario = usuarioComId(id, "fulano", Role.PARTICIPANTE);
        when(usuarioRepository.findById(id)).thenReturn(Optional.of(usuario));
        lenient().when(usuarioRepository.existsByApelidoIgnoreCase("fulano")).thenReturn(false);
        when(usuarioRepository.existsByEmailIgnoreCase("ocupado@exemplo.com")).thenReturn(true);
        assertThatThrownBy(() -> usuarioService.atualizarConta(id,
                new AtualizarContaRequest("fulano", "ocupado@exemplo.com")))
                .isInstanceOf(ConflitoDeEstadoException.class);
    }

    @Test
    void atualizarConta_deveLancarUnauthenticatedQuandoUsuarioNaoExiste() {
        UUID id = UUID.randomUUID();
        when(usuarioRepository.findById(id)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> usuarioService.atualizarConta(id,
                new AtualizarContaRequest("fulano", "fulano@exemplo.com")))
                .isInstanceOf(UnauthenticatedException.class);
    }

    @Test
    void alterarSenha_deveTrocarHashQuandoSenhaAtualConfere() throws Exception {
        UUID id = UUID.randomUUID();
        Usuario usuario = usuarioComId(id, "fulano", Role.PARTICIPANTE);
        when(usuarioRepository.findById(id)).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("senhaAtual123", "hash")).thenReturn(true);
        when(passwordEncoder.encode("senhaNova12345")).thenReturn("novoHash");
        usuarioService.alterarSenha(id, new AlterarSenhaRequest("senhaAtual123", "senhaNova12345", "senhaNova12345"));
        assertThat(usuario.getSenhaHash()).isEqualTo("novoHash");
    }

    @Test
    void alterarSenha_deveLancarValidationExceptionQuandoConfirmacaoNaoBate() {
        UUID id = UUID.randomUUID();
        assertThatThrownBy(() -> usuarioService.alterarSenha(id,
                new AlterarSenhaRequest("senhaAtual123", "senhaNova12345", "outraSenha123")))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    void alterarSenha_deveLancarUnauthenticatedQuandoSenhaAtualIncorreta() throws Exception {
        UUID id = UUID.randomUUID();
        Usuario usuario = usuarioComId(id, "fulano", Role.PARTICIPANTE);
        when(usuarioRepository.findById(id)).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("senhaErrada12", "hash")).thenReturn(false);
        assertThatThrownBy(() -> usuarioService.alterarSenha(id,
                new AlterarSenhaRequest("senhaErrada12", "senhaNova12345", "senhaNova12345")))
                .isInstanceOf(UnauthenticatedException.class);
    }

    @Test
    void alterarSenha_deveLancarUnauthenticatedQuandoUsuarioNaoExiste() {
        UUID id = UUID.randomUUID();
        when(usuarioRepository.findById(id)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> usuarioService.alterarSenha(id,
                new AlterarSenhaRequest("senhaAtual123", "senhaNova12345", "senhaNova12345")))
                .isInstanceOf(UnauthenticatedException.class);
    }
}
