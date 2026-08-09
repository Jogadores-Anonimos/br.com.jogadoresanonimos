package br.com.jogadoresanonimos.auth.service;

import br.com.jogadoresanonimos.auth.dto.AdminRedefinirSenhaRequest;
import br.com.jogadoresanonimos.auth.dto.MensagemResponse;
import br.com.jogadoresanonimos.auth.entity.AdminResetCodigo;
import br.com.jogadoresanonimos.auth.repository.AdminResetCodigoRepository;
import br.com.jogadoresanonimos.common.dto.ApiError;
import br.com.jogadoresanonimos.common.exception.ErrorCode;
import br.com.jogadoresanonimos.common.exception.UnauthenticatedException;
import br.com.jogadoresanonimos.common.exception.ValidationException;
import br.com.jogadoresanonimos.common.ratelimit.RateLimiterService;
import br.com.jogadoresanonimos.common.util.HmacHasher;
import br.com.jogadoresanonimos.usuario.entity.Usuario;
import br.com.jogadoresanonimos.usuario.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

@Service
public class AdminAuthService {

    private final UsuarioRepository usuarioRepository;
    private final AdminResetCodigoRepository adminResetCodigoRepository;
    private final PasswordEncoder passwordEncoder;
    private final RateLimiterService rateLimiterService;
    private final HmacHasher hmacHasher;

    public AdminAuthService(UsuarioRepository usuarioRepository, AdminResetCodigoRepository adminResetCodigoRepository,
                             PasswordEncoder passwordEncoder, RateLimiterService rateLimiterService, HmacHasher hmacHasher) {
        this.usuarioRepository = usuarioRepository;
        this.adminResetCodigoRepository = adminResetCodigoRepository;
        this.passwordEncoder = passwordEncoder;
        this.rateLimiterService = rateLimiterService;
        this.hmacHasher = hmacHasher;
    }

    @Transactional
    public MensagemResponse redefinirSenha(AdminRedefinirSenhaRequest request) {
        rateLimiterService.check("admin-redefinir-senha:" + hmacHasher.hash(request.codigo()), 5, Duration.ofMinutes(1),
                ErrorCode.AUTH_RATE_LIMITED);
        String codigoHash = hmacHasher.hash(request.codigo());
        AdminResetCodigo resetCodigo = adminResetCodigoRepository.findValidoPorCodigoHash(codigoHash, Instant.now())
                .orElseThrow(() -> new UnauthenticatedException(ErrorCode.ADMIN_RESET_CODIGO_INVALIDO, "Código inválido ou expirado"));
        if (!request.novaSenha().equals(request.confirmarNovaSenha())) {
            throw new ValidationException(ErrorCode.AUTH_SENHA_CONFIRMACAO_DIVERGENTE, "Nova senha e confirmação não coincidem",
                    List.of(new ApiError.FieldError("confirmarNovaSenha", "Deve ser igual à nova senha")));
        }
        Usuario usuario = usuarioRepository.findById(resetCodigo.getUsuarioId())
                .orElseThrow(() -> new UnauthenticatedException(ErrorCode.ADMIN_RESET_CODIGO_INVALIDO, "Código inválido ou expirado"));
        usuario.setSenhaHash(passwordEncoder.encode(request.novaSenha()));
        resetCodigo.marcarUsado();
        return new MensagemResponse("Senha redefinida com sucesso.");
    }
}
