package br.com.jogadoresanonimos.usuario.service;

import br.com.jogadoresanonimos.auditoria.entity.AcaoAuditoria;
import br.com.jogadoresanonimos.auditoria.service.AdminAuditoriaService;
import br.com.jogadoresanonimos.auth.dto.MensagemResponse;
import br.com.jogadoresanonimos.common.exception.ConflitoDeEstadoException;
import br.com.jogadoresanonimos.common.exception.ErrorCode;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

@Service
public class UsuarioService {

    private static final String ENTIDADE_USUARIO = "usuario";

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final RateLimiterService rateLimiterService;
    private final AdminAuditoriaService adminAuditoriaService;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder,
                           RateLimiterService rateLimiterService, AdminAuditoriaService adminAuditoriaService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.rateLimiterService = rateLimiterService;
        this.adminAuditoriaService = adminAuditoriaService;
    }

    public UsuarioMeResponse buscarMe(UUID usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new UnauthenticatedException(ErrorCode.AUTH_TOKEN_AUSENTE_OU_INVALIDO, "Token ausente ou inválido"));
        return UsuarioMeResponse.from(usuario);
    }

    @Transactional(readOnly = true)
    public List<UsuarioAdminResponse> listarTodos() {
        return usuarioRepository.findAllByOrderByCriadoEmDesc().stream()
                .map(UsuarioAdminResponse::fromMascarado)
                .toList();
    }

    @Transactional
    public UsuarioAdminResponse editarRole(UUID id, Role novaRole, UUID solicitanteId) {
        rateLimiterService.check("admin-usuario-escrita:" + solicitanteId, 60, Duration.ofMinutes(1), ErrorCode.USUARIO_RATE_LIMITED);
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException(ErrorCode.USUARIO_NAO_ENCONTRADO, "Usuário não encontrado"));
        if (usuario.getRole() == novaRole) {
            return UsuarioAdminResponse.from(usuario);
        }
        boolean autoRebaixamento = usuario.getId().equals(solicitanteId)
                && usuario.getRole() == Role.ADMIN && novaRole != Role.ADMIN;
        if (autoRebaixamento) {
            throw new ConflitoDeEstadoException(ErrorCode.USUARIO_AUTO_REBAIXAMENTO_ADMIN, "Um admin não pode remover a própria role de admin");
        }
        boolean removendoUltimoAdmin = usuario.getRole() == Role.ADMIN && novaRole != Role.ADMIN
                && usuarioRepository.buscarPorRoleParaAtualizacao(Role.ADMIN).size() <= 1;
        if (removendoUltimoAdmin) {
            throw new ConflitoDeEstadoException(ErrorCode.USUARIO_ULTIMO_ADMIN, "Não é possível remover o último admin do sistema");
        }
        String roleAnterior = usuario.getRole().name();
        usuario.alterarRole(novaRole);
        adminAuditoriaService.registrar(solicitanteId, ENTIDADE_USUARIO, usuario.getId(), AcaoAuditoria.EDITAR,
                roleAnterior, novaRole.name());
        return UsuarioAdminResponse.from(usuario);
    }

    @Transactional
    public UsuarioMeResponse atualizarConta(UUID usuarioId, AtualizarContaRequest request) {
        rateLimiterService.check("atualizar-conta:" + usuarioId, 5, Duration.ofMinutes(1), ErrorCode.USUARIO_RATE_LIMITED);
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new UnauthenticatedException(ErrorCode.AUTH_TOKEN_AUSENTE_OU_INVALIDO, "Token ausente ou inválido"));
        boolean apelidoEmUsoPorOutro = !usuario.getApelido().equalsIgnoreCase(request.apelido())
                && usuarioRepository.existsByApelidoIgnoreCase(request.apelido());
        boolean emailEmUsoPorOutro = !usuario.getEmail().equalsIgnoreCase(request.email())
                && usuarioRepository.existsByEmailIgnoreCase(request.email());
        if (apelidoEmUsoPorOutro || emailEmUsoPorOutro) {
            throw new ConflitoDeEstadoException(ErrorCode.USUARIO_DADOS_EM_USO, "Não foi possível salvar os dados informados");
        }
        usuario.editarConta(request.apelido(), request.email());
        return UsuarioMeResponse.from(usuario);
    }

    @Transactional
    public MensagemResponse alterarSenha(UUID usuarioId, AlterarSenhaRequest request) {
        rateLimiterService.check("alterar-senha:" + usuarioId, 5, Duration.ofMinutes(1), ErrorCode.USUARIO_RATE_LIMITED);
        if (!request.novaSenha().equals(request.confirmarNovaSenha())) {
            throw new ValidationException(ErrorCode.USUARIO_SENHA_CONFIRMACAO_DIVERGENTE, "Nova senha e confirmação não coincidem",
                    List.of(new br.com.jogadoresanonimos.common.dto.ApiError.FieldError(
                            "confirmarNovaSenha", "Deve ser igual à nova senha")));
        }
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new UnauthenticatedException(ErrorCode.AUTH_TOKEN_AUSENTE_OU_INVALIDO, "Token ausente ou inválido"));
        if (!passwordEncoder.matches(request.senhaAtual(), usuario.getSenhaHash())) {
            throw new UnauthenticatedException(ErrorCode.USUARIO_SENHA_ATUAL_INCORRETA, "Senha atual incorreta");
        }
        usuario.setSenhaHash(passwordEncoder.encode(request.novaSenha()));
        return new MensagemResponse("Senha alterada com sucesso.");
    }
}
