package br.com.jogadoresanonimos.usuario.service;

import br.com.jogadoresanonimos.auditoria.entity.AcaoAuditoria;
import br.com.jogadoresanonimos.auditoria.service.AdminAuditoriaService;
import br.com.jogadoresanonimos.auth.dto.MensagemResponse;
import br.com.jogadoresanonimos.auth.service.AuthService;
import br.com.jogadoresanonimos.common.exception.ApiException;
import br.com.jogadoresanonimos.common.exception.ConflitoDeEstadoException;
import br.com.jogadoresanonimos.common.exception.ErrorCode;
import br.com.jogadoresanonimos.common.exception.RecursoNaoEncontradoException;
import br.com.jogadoresanonimos.common.exception.UnauthenticatedException;
import br.com.jogadoresanonimos.common.exception.ValidationException;
import br.com.jogadoresanonimos.common.ratelimit.RateLimiterService;
import br.com.jogadoresanonimos.security.MfaSecretCipher;
import br.com.jogadoresanonimos.security.TotpService;
import br.com.jogadoresanonimos.usuario.dto.AlterarSenhaRequest;
import br.com.jogadoresanonimos.usuario.dto.AtualizarContaRequest;
import br.com.jogadoresanonimos.usuario.dto.SolicitarTrocaEmailRequest;
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

    @Mock
    TotpService totpService;

    @Mock
    MfaSecretCipher mfaSecretCipher;

    @Mock
    AuthService authService;

    UsuarioService usuarioService;

    @BeforeEach
    void setUp() {
        usuarioService = new UsuarioService(usuarioRepository, passwordEncoder, rateLimiterService,
                adminAuditoriaService, totpService, mfaSecretCipher, authService, "5");
    }

    private List<Usuario> adminsAtivos(int quantidade) throws Exception {
        return java.util.stream.IntStream.range(0, quantidade)
                .mapToObj(i -> {
                    try {
                        return usuarioComId(UUID.randomUUID(), "admin" + i, Role.ADMIN);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                })
                .toList();
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

    private Usuario usuarioComMfaAtivo(UUID id, String apelido, Role role) throws Exception {
        Usuario usuario = usuarioComId(id, apelido, role);
        usuario.iniciarEnrollmentMfa("secret-cifrado-fake");
        usuario.confirmarMfa();
        return usuario;
    }

    @Test
    void editarRole_deveAtualizarQuandoNaoHaConflito() throws Exception {
        UUID id = UUID.randomUUID();
        UUID solicitanteId = UUID.randomUUID();
        Usuario usuario = usuarioComId(id, "fulano", Role.PARTICIPANTE);
        Usuario solicitante = usuarioComMfaAtivo(solicitanteId, "promotor", Role.ADMIN);
        when(usuarioRepository.findById(id)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.findById(solicitanteId)).thenReturn(Optional.of(solicitante));
        when(usuarioRepository.buscarPorRoleParaAtualizacao(Role.ADMIN)).thenReturn(adminsAtivos(1));
        when(mfaSecretCipher.decifrar("secret-cifrado-fake")).thenReturn("secret-plano");
        when(totpService.validar("secret-plano", "123456")).thenReturn(true);
        UsuarioAdminResponse resultado = usuarioService.editarRole(id, Role.ADMIN, solicitanteId, "123456");
        assertThat(resultado.role()).isEqualTo(Role.ADMIN);
        verify(adminAuditoriaService).registrar(solicitanteId, "usuario", id, AcaoAuditoria.EDITAR,
                "PARTICIPANTE", "ADMIN");
    }

    @Test
    void editarRole_deveLancarExcecaoQuandoUsuarioNaoExiste() {
        UUID id = UUID.randomUUID();
        when(usuarioRepository.findById(id)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> usuarioService.editarRole(id, Role.ADMIN, UUID.randomUUID(), "123456"))
                .isInstanceOf(RecursoNaoEncontradoException.class);
    }

    @Test
    void editarRole_deveImpedirAutoRebaixamento() throws Exception {
        UUID id = UUID.randomUUID();
        Usuario admin = usuarioComId(id, "admin", Role.ADMIN);
        when(usuarioRepository.findById(id)).thenReturn(Optional.of(admin));
        assertThatThrownBy(() -> usuarioService.editarRole(id, Role.PARTICIPANTE, id, null))
                .isInstanceOf(ConflitoDeEstadoException.class);
    }

    @Test
    void editarRole_devePermitirOutroAdminRebaixarQuandoHaMaisDeUmAdmin() throws Exception {
        UUID id = UUID.randomUUID();
        UUID solicitanteId = UUID.randomUUID();
        Usuario admin = usuarioComId(id, "admin", Role.ADMIN);
        when(usuarioRepository.findById(id)).thenReturn(Optional.of(admin));
        lenient().when(usuarioRepository.buscarPorRoleParaAtualizacao(Role.ADMIN)).thenReturn(adminsAtivos(2));
        UsuarioAdminResponse resultado = usuarioService.editarRole(id, Role.PARTICIPANTE, solicitanteId, null);
        assertThat(resultado.role()).isEqualTo(Role.PARTICIPANTE);
    }

    @Test
    void editarRole_rebaixamentoNaoExigeTotpMesmoQuandoInformado() throws Exception {
        UUID id = UUID.randomUUID();
        UUID solicitanteId = UUID.randomUUID();
        Usuario admin = usuarioComId(id, "admin", Role.ADMIN);
        when(usuarioRepository.findById(id)).thenReturn(Optional.of(admin));
        lenient().when(usuarioRepository.buscarPorRoleParaAtualizacao(Role.ADMIN)).thenReturn(adminsAtivos(2));
        UsuarioAdminResponse resultado = usuarioService.editarRole(id, Role.PARTICIPANTE, solicitanteId, "123456");
        assertThat(resultado.role()).isEqualTo(Role.PARTICIPANTE);
        org.mockito.Mockito.verifyNoInteractions(totpService, mfaSecretCipher);
    }

    @Test
    void editarRole_deveImpedirRemoverUltimoAdminDoSistema() throws Exception {
        UUID id = UUID.randomUUID();
        UUID solicitanteId = UUID.randomUUID();
        Usuario admin = usuarioComId(id, "admin", Role.ADMIN);
        when(usuarioRepository.findById(id)).thenReturn(Optional.of(admin));
        when(usuarioRepository.buscarPorRoleParaAtualizacao(Role.ADMIN)).thenReturn(adminsAtivos(1));
        assertThatThrownBy(() -> usuarioService.editarRole(id, Role.PARTICIPANTE, solicitanteId, null))
                .isInstanceOf(ConflitoDeEstadoException.class);
    }

    @Test
    void editarRole_devePermitirPromoverQuandoAbaixoDoLimite() throws Exception {
        UUID id = UUID.randomUUID();
        UUID solicitanteId = UUID.randomUUID();
        Usuario usuario = usuarioComId(id, "fulano", Role.PARTICIPANTE);
        Usuario solicitante = usuarioComMfaAtivo(solicitanteId, "promotor", Role.ADMIN);
        when(usuarioRepository.findById(id)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.findById(solicitanteId)).thenReturn(Optional.of(solicitante));
        when(usuarioRepository.buscarPorRoleParaAtualizacao(Role.ADMIN)).thenReturn(adminsAtivos(4));
        when(mfaSecretCipher.decifrar("secret-cifrado-fake")).thenReturn("secret-plano");
        when(totpService.validar("secret-plano", "123456")).thenReturn(true);
        UsuarioAdminResponse resultado = usuarioService.editarRole(id, Role.ADMIN, solicitanteId, "123456");
        assertThat(resultado.role()).isEqualTo(Role.ADMIN);
    }

    @Test
    void editarRole_deveImpedirPromoverQuandoLimiteMaximoAtingido() throws Exception {
        UUID id = UUID.randomUUID();
        UUID solicitanteId = UUID.randomUUID();
        Usuario usuario = usuarioComId(id, "fulano", Role.PARTICIPANTE);
        when(usuarioRepository.findById(id)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.buscarPorRoleParaAtualizacao(Role.ADMIN)).thenReturn(adminsAtivos(5));
        assertThatThrownBy(() -> usuarioService.editarRole(id, Role.ADMIN, solicitanteId, "123456"))
                .isInstanceOf(ConflitoDeEstadoException.class);
    }

    @Test
    void editarRole_devePriorizarLimiteMaximoAntesDoStepUpMfa() throws Exception {
        UUID id = UUID.randomUUID();
        UUID solicitanteId = UUID.randomUUID();
        Usuario usuario = usuarioComId(id, "fulano", Role.PARTICIPANTE);
        when(usuarioRepository.findById(id)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.buscarPorRoleParaAtualizacao(Role.ADMIN)).thenReturn(adminsAtivos(5));
        assertThatThrownBy(() -> usuarioService.editarRole(id, Role.ADMIN, solicitanteId, "123456"))
                .isInstanceOfSatisfying(ApiException.class,
                        e -> assertThat(e.getCode()).isEqualTo(ErrorCode.USUARIO_LIMITE_ADMIN_ATINGIDO));
        org.mockito.Mockito.verifyNoInteractions(totpService, mfaSecretCipher);
    }

    @Test
    void editarRole_devePromoverBloqueandoComMfaNaoConfiguradoNoPromotor() throws Exception {
        UUID id = UUID.randomUUID();
        UUID solicitanteId = UUID.randomUUID();
        Usuario usuario = usuarioComId(id, "fulano", Role.PARTICIPANTE);
        Usuario solicitante = usuarioComId(solicitanteId, "promotor", Role.ADMIN);
        when(usuarioRepository.findById(id)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.findById(solicitanteId)).thenReturn(Optional.of(solicitante));
        when(usuarioRepository.buscarPorRoleParaAtualizacao(Role.ADMIN)).thenReturn(adminsAtivos(1));
        assertThatThrownBy(() -> usuarioService.editarRole(id, Role.ADMIN, solicitanteId, "123456"))
                .isInstanceOfSatisfying(ApiException.class,
                        e -> assertThat(e.getCode()).isEqualTo(ErrorCode.USUARIO_PROMOCAO_MFA_NAO_CONFIGURADO));
        org.mockito.Mockito.verifyNoInteractions(totpService, mfaSecretCipher, adminAuditoriaService);
        assertThat(usuario.getRole()).isEqualTo(Role.PARTICIPANTE);
    }

    @Test
    void editarRole_devePromoverBloqueandoComTotpAusente() throws Exception {
        UUID id = UUID.randomUUID();
        UUID solicitanteId = UUID.randomUUID();
        Usuario usuario = usuarioComId(id, "fulano", Role.PARTICIPANTE);
        Usuario solicitante = usuarioComMfaAtivo(solicitanteId, "promotor", Role.ADMIN);
        when(usuarioRepository.findById(id)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.findById(solicitanteId)).thenReturn(Optional.of(solicitante));
        when(usuarioRepository.buscarPorRoleParaAtualizacao(Role.ADMIN)).thenReturn(adminsAtivos(1));
        when(mfaSecretCipher.decifrar("secret-cifrado-fake")).thenReturn("secret-plano");
        when(totpService.validar("secret-plano", null)).thenReturn(false);
        assertThatThrownBy(() -> usuarioService.editarRole(id, Role.ADMIN, solicitanteId, null))
                .isInstanceOfSatisfying(ApiException.class,
                        e -> assertThat(e.getCode()).isEqualTo(ErrorCode.USUARIO_PROMOCAO_TOTP_INVALIDO));
        org.mockito.Mockito.verifyNoInteractions(adminAuditoriaService);
        assertThat(usuario.getRole()).isEqualTo(Role.PARTICIPANTE);
    }

    @Test
    void editarRole_devePromoverBloqueandoComTotpIncorreto() throws Exception {
        UUID id = UUID.randomUUID();
        UUID solicitanteId = UUID.randomUUID();
        Usuario usuario = usuarioComId(id, "fulano", Role.PARTICIPANTE);
        Usuario solicitante = usuarioComMfaAtivo(solicitanteId, "promotor", Role.ADMIN);
        when(usuarioRepository.findById(id)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.findById(solicitanteId)).thenReturn(Optional.of(solicitante));
        when(usuarioRepository.buscarPorRoleParaAtualizacao(Role.ADMIN)).thenReturn(adminsAtivos(1));
        when(mfaSecretCipher.decifrar("secret-cifrado-fake")).thenReturn("secret-plano");
        when(totpService.validar("secret-plano", "999999")).thenReturn(false);
        assertThatThrownBy(() -> usuarioService.editarRole(id, Role.ADMIN, solicitanteId, "999999"))
                .isInstanceOfSatisfying(ApiException.class,
                        e -> assertThat(e.getCode()).isEqualTo(ErrorCode.USUARIO_PROMOCAO_TOTP_INVALIDO));
        assertThat(usuario.getRole()).isEqualTo(Role.PARTICIPANTE);
    }

    @Test
    void editarRole_devePromoverBloqueandoComTotpFormatoInvalido() throws Exception {
        UUID id = UUID.randomUUID();
        UUID solicitanteId = UUID.randomUUID();
        Usuario usuario = usuarioComId(id, "fulano", Role.PARTICIPANTE);
        Usuario solicitante = usuarioComMfaAtivo(solicitanteId, "promotor", Role.ADMIN);
        when(usuarioRepository.findById(id)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.findById(solicitanteId)).thenReturn(Optional.of(solicitante));
        when(usuarioRepository.buscarPorRoleParaAtualizacao(Role.ADMIN)).thenReturn(adminsAtivos(1));
        assertThatThrownBy(() -> usuarioService.editarRole(id, Role.ADMIN, solicitanteId, "12a456"))
                .isInstanceOfSatisfying(ApiException.class,
                        e -> assertThat(e.getCode()).isEqualTo(ErrorCode.VALIDATION_ERROR));
        org.mockito.Mockito.verifyNoInteractions(totpService, mfaSecretCipher, adminAuditoriaService);
        assertThat(usuario.getRole()).isEqualTo(Role.PARTICIPANTE);
    }

    @Test
    void construtor_deveFalharQuandoLimiteMaximoAusente() {
        assertThatThrownBy(() -> new UsuarioService(usuarioRepository, passwordEncoder, rateLimiterService,
                adminAuditoriaService, totpService, mfaSecretCipher, authService, ""))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void construtor_deveFalharQuandoLimiteMaximoNaoNumerico() {
        assertThatThrownBy(() -> new UsuarioService(usuarioRepository, passwordEncoder, rateLimiterService,
                adminAuditoriaService, totpService, mfaSecretCipher, authService, "abc"))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void construtor_deveFalharQuandoLimiteMaximoZeroOuNegativo() {
        assertThatThrownBy(() -> new UsuarioService(usuarioRepository, passwordEncoder, rateLimiterService,
                adminAuditoriaService, totpService, mfaSecretCipher, authService, "0"))
                .isInstanceOf(IllegalStateException.class);
        assertThatThrownBy(() -> new UsuarioService(usuarioRepository, passwordEncoder, rateLimiterService,
                adminAuditoriaService, totpService, mfaSecretCipher, authService, "-1"))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void editarRole_deveSerNoOpQuandoRoleJaEAMesma() throws Exception {
        UUID id = UUID.randomUUID();
        Usuario usuario = usuarioComId(id, "fulano", Role.PARTICIPANTE);
        when(usuarioRepository.findById(id)).thenReturn(Optional.of(usuario));
        UsuarioAdminResponse resultado = usuarioService.editarRole(id, Role.PARTICIPANTE, UUID.randomUUID(), null);
        assertThat(resultado.role()).isEqualTo(Role.PARTICIPANTE);
        org.mockito.Mockito.verifyNoInteractions(adminAuditoriaService, totpService, mfaSecretCipher);
    }

    @Test
    void editarRole_noOpNaoExigeTotpMesmoQuandoInformado() throws Exception {
        UUID id = UUID.randomUUID();
        Usuario usuario = usuarioComId(id, "fulano", Role.ADMIN);
        when(usuarioRepository.findById(id)).thenReturn(Optional.of(usuario));
        UsuarioAdminResponse resultado = usuarioService.editarRole(id, Role.ADMIN, UUID.randomUUID(), "123456");
        assertThat(resultado.role()).isEqualTo(Role.ADMIN);
        org.mockito.Mockito.verifyNoInteractions(adminAuditoriaService, totpService, mfaSecretCipher);
    }

    @Test
    void atualizarConta_deveAtualizarApelidoQuandoDisponivel() throws Exception {
        UUID id = UUID.randomUUID();
        Usuario usuario = usuarioComId(id, "fulano", Role.PARTICIPANTE);
        when(usuarioRepository.findById(id)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.existsByApelidoIgnoreCase("novoApelido")).thenReturn(false);
        UsuarioMeResponse resultado = usuarioService.atualizarConta(id, new AtualizarContaRequest("novoApelido"));
        assertThat(resultado.apelido()).isEqualTo("novoApelido");
        assertThat(resultado.email()).isEqualTo("fulano@exemplo.com");
    }

    @Test
    void atualizarConta_devePermitirReenviarOMesmoApelidoSemChecarUnicidadeContraSiMesmo() throws Exception {
        UUID id = UUID.randomUUID();
        Usuario usuario = usuarioComId(id, "fulano", Role.PARTICIPANTE);
        when(usuarioRepository.findById(id)).thenReturn(Optional.of(usuario));
        UsuarioMeResponse resultado = usuarioService.atualizarConta(id, new AtualizarContaRequest("fulano"));
        assertThat(resultado.apelido()).isEqualTo("fulano");
    }

    @Test
    void atualizarConta_deveLancarConflitoQuandoApelidoJaEstaEmUsoPorOutraConta() throws Exception {
        UUID id = UUID.randomUUID();
        Usuario usuario = usuarioComId(id, "fulano", Role.PARTICIPANTE);
        when(usuarioRepository.findById(id)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.existsByApelidoIgnoreCase("ocupado")).thenReturn(true);
        assertThatThrownBy(() -> usuarioService.atualizarConta(id, new AtualizarContaRequest("ocupado")))
                .isInstanceOf(ConflitoDeEstadoException.class);
    }

    @Test
    void atualizarConta_deveLancarUnauthenticatedQuandoUsuarioNaoExiste() {
        UUID id = UUID.randomUUID();
        when(usuarioRepository.findById(id)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> usuarioService.atualizarConta(id, new AtualizarContaRequest("fulano")))
                .isInstanceOf(UnauthenticatedException.class);
    }

    @Test
    void solicitarTrocaEmail_deveGerarTokenQuandoSenhaEEmailValidos() throws Exception {
        UUID id = UUID.randomUUID();
        Usuario usuario = usuarioComId(id, "fulano", Role.PARTICIPANTE);
        when(usuarioRepository.findById(id)).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("senhaAtual123", "hash")).thenReturn(true);
        when(usuarioRepository.existsByEmailIgnoreCase("novo@exemplo.com")).thenReturn(false);
        MensagemResponse resultado = usuarioService.solicitarTrocaEmail(id,
                new SolicitarTrocaEmailRequest("senhaAtual123", "novo@exemplo.com"));
        assertThat(resultado.mensagem()).isNotBlank();
        verify(authService).gerarEEnviarTokenTrocaEmail(usuario, "novo@exemplo.com");
    }

    @Test
    void solicitarTrocaEmail_deveChecarRateLimitComChaveEParametrosCorretos() throws Exception {
        UUID id = UUID.randomUUID();
        Usuario usuario = usuarioComId(id, "fulano", Role.PARTICIPANTE);
        when(usuarioRepository.findById(id)).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("senhaAtual123", "hash")).thenReturn(true);
        when(usuarioRepository.existsByEmailIgnoreCase("novo@exemplo.com")).thenReturn(false);
        usuarioService.solicitarTrocaEmail(id, new SolicitarTrocaEmailRequest("senhaAtual123", "novo@exemplo.com"));
        verify(rateLimiterService).check("troca-email:" + id, 3, java.time.Duration.ofMinutes(10),
                ErrorCode.USUARIO_RATE_LIMITED);
    }

    @Test
    void solicitarTrocaEmail_deveLancarRateLimitedQuandoLimiteExcedido() {
        UUID id = UUID.randomUUID();
        org.mockito.Mockito.doThrow(new br.com.jogadoresanonimos.common.exception.RateLimitedException(
                        ErrorCode.USUARIO_RATE_LIMITED, "Muitas tentativas", 60L))
                .when(rateLimiterService).check("troca-email:" + id, 3, java.time.Duration.ofMinutes(10),
                        ErrorCode.USUARIO_RATE_LIMITED);
        assertThatThrownBy(() -> usuarioService.solicitarTrocaEmail(id,
                new SolicitarTrocaEmailRequest("senhaAtual123", "novo@exemplo.com")))
                .isInstanceOf(br.com.jogadoresanonimos.common.exception.RateLimitedException.class);
        org.mockito.Mockito.verifyNoInteractions(usuarioRepository, authService);
    }

    @Test
    void solicitarTrocaEmail_deveLancarUnauthenticatedQuandoUsuarioNaoExiste() {
        UUID id = UUID.randomUUID();
        when(usuarioRepository.findById(id)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> usuarioService.solicitarTrocaEmail(id,
                new SolicitarTrocaEmailRequest("senhaAtual123", "novo@exemplo.com")))
                .isInstanceOf(UnauthenticatedException.class);
        org.mockito.Mockito.verifyNoInteractions(authService);
    }

    @Test
    void solicitarTrocaEmail_deveLancarUnauthenticatedQuandoSenhaAtualIncorreta() throws Exception {
        UUID id = UUID.randomUUID();
        Usuario usuario = usuarioComId(id, "fulano", Role.PARTICIPANTE);
        when(usuarioRepository.findById(id)).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("senhaErrada12", "hash")).thenReturn(false);
        assertThatThrownBy(() -> usuarioService.solicitarTrocaEmail(id,
                new SolicitarTrocaEmailRequest("senhaErrada12", "novo@exemplo.com")))
                .isInstanceOf(UnauthenticatedException.class);
    }

    @Test
    void solicitarTrocaEmail_deveLancarValidationExceptionQuandoNovoEmailIgualAtual() throws Exception {
        UUID id = UUID.randomUUID();
        Usuario usuario = usuarioComId(id, "fulano", Role.PARTICIPANTE);
        when(usuarioRepository.findById(id)).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("senhaAtual123", "hash")).thenReturn(true);
        assertThatThrownBy(() -> usuarioService.solicitarTrocaEmail(id,
                new SolicitarTrocaEmailRequest("senhaAtual123", "fulano@exemplo.com")))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    void solicitarTrocaEmail_deveLancarConflitoQuandoNovoEmailJaEmUso() throws Exception {
        UUID id = UUID.randomUUID();
        Usuario usuario = usuarioComId(id, "fulano", Role.PARTICIPANTE);
        when(usuarioRepository.findById(id)).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("senhaAtual123", "hash")).thenReturn(true);
        when(usuarioRepository.existsByEmailIgnoreCase("ocupado@exemplo.com")).thenReturn(true);
        assertThatThrownBy(() -> usuarioService.solicitarTrocaEmail(id,
                new SolicitarTrocaEmailRequest("senhaAtual123", "ocupado@exemplo.com")))
                .isInstanceOf(ConflitoDeEstadoException.class);
    }

    @Test
    void alterarSenha_deveTrocarHashQuandoSenhaAtualConfere() throws Exception {
        UUID id = UUID.randomUUID();
        Usuario usuario = usuarioComId(id, "fulano", Role.PARTICIPANTE);
        when(usuarioRepository.findById(id)).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("senhaAtual123", "hash")).thenReturn(true);
        when(passwordEncoder.matches("senhaNova12345", "hash")).thenReturn(false);
        when(passwordEncoder.encode("senhaNova12345")).thenReturn("novoHash");
        usuarioService.alterarSenha(id, new AlterarSenhaRequest("senhaAtual123", "senhaNova12345", "senhaNova12345"));
        assertThat(usuario.getSenhaHash()).isEqualTo("novoHash");
    }

    @Test
    void alterarSenha_deveLancarValidationExceptionQuandoNovaSenhaIgualAtual() throws Exception {
        UUID id = UUID.randomUUID();
        Usuario usuario = usuarioComId(id, "fulano", Role.PARTICIPANTE);
        when(usuarioRepository.findById(id)).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("senhaAtual123", "hash")).thenReturn(true);
        assertThatThrownBy(() -> usuarioService.alterarSenha(id,
                new AlterarSenhaRequest("senhaAtual123", "senhaAtual123", "senhaAtual123")))
                .isInstanceOf(ValidationException.class);
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
