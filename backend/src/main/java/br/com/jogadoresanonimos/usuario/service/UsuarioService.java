package br.com.jogadoresanonimos.usuario.service;

import br.com.jogadoresanonimos.auth.dto.MensagemResponse;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final RateLimiterService rateLimiterService;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder,
                           RateLimiterService rateLimiterService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.rateLimiterService = rateLimiterService;
    }

    public UsuarioMeResponse buscarMe(UUID usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new UnauthenticatedException("Token ausente ou inválido"));
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
        rateLimiterService.check("admin-usuario-escrita:" + solicitanteId, 60, Duration.ofMinutes(1));
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado"));
        if (usuario.getRole() == novaRole) {
            return UsuarioAdminResponse.from(usuario);
        }
        boolean autoRebaixamento = usuario.getId().equals(solicitanteId)
                && usuario.getRole() == Role.ADMIN && novaRole != Role.ADMIN;
        if (autoRebaixamento) {
            throw new ConflitoDeEstadoException("Um admin não pode remover a própria role de admin");
        }
        boolean removendoUltimoAdmin = usuario.getRole() == Role.ADMIN && novaRole != Role.ADMIN
                && usuarioRepository.countByRole(Role.ADMIN) <= 1;
        if (removendoUltimoAdmin) {
            throw new ConflitoDeEstadoException("Não é possível remover o último admin do sistema");
        }
        usuario.alterarRole(novaRole);
        return UsuarioAdminResponse.from(usuario);
    }

    @Transactional
    public UsuarioMeResponse atualizarConta(UUID usuarioId, AtualizarContaRequest request) {
        rateLimiterService.check("atualizar-conta:" + usuarioId, 5, Duration.ofMinutes(1));
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new UnauthenticatedException("Token ausente ou inválido"));
        boolean apelidoEmUsoPorOutro = !usuario.getApelido().equalsIgnoreCase(request.apelido())
                && usuarioRepository.existsByApelidoIgnoreCase(request.apelido());
        boolean emailEmUsoPorOutro = !usuario.getEmail().equalsIgnoreCase(request.email())
                && usuarioRepository.existsByEmailIgnoreCase(request.email());
        if (apelidoEmUsoPorOutro || emailEmUsoPorOutro) {
            throw new ConflitoDeEstadoException("Não foi possível salvar os dados informados");
        }
        usuario.editarConta(request.apelido(), request.email());
        return UsuarioMeResponse.from(usuario);
    }

    @Transactional
    public MensagemResponse alterarSenha(UUID usuarioId, AlterarSenhaRequest request) {
        rateLimiterService.check("alterar-senha:" + usuarioId, 5, Duration.ofMinutes(1));
        if (!request.novaSenha().equals(request.confirmarNovaSenha())) {
            throw new ValidationException("Nova senha e confirmação não coincidem",
                    List.of(new br.com.jogadoresanonimos.common.dto.ApiError.FieldError(
                            "confirmarNovaSenha", "Deve ser igual à nova senha")));
        }
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new UnauthenticatedException("Token ausente ou inválido"));
        if (!passwordEncoder.matches(request.senhaAtual(), usuario.getSenhaHash())) {
            throw new UnauthenticatedException("Senha atual incorreta");
        }
        usuario.setSenhaHash(passwordEncoder.encode(request.novaSenha()));
        return new MensagemResponse("Senha alterada com sucesso.");
    }
}
