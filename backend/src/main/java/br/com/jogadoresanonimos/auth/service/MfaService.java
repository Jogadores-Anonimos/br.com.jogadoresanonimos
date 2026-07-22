package br.com.jogadoresanonimos.auth.service;

import br.com.jogadoresanonimos.auditoria.entity.AcaoAuditoria;
import br.com.jogadoresanonimos.auditoria.service.AdminAuditoriaService;
import br.com.jogadoresanonimos.auth.dto.MfaConfirmarRequest;
import br.com.jogadoresanonimos.auth.dto.MfaDesativarRequest;
import br.com.jogadoresanonimos.auth.dto.MfaEnrollResponse;
import br.com.jogadoresanonimos.auth.dto.MensagemResponse;
import br.com.jogadoresanonimos.common.exception.ConflitoDeEstadoException;
import br.com.jogadoresanonimos.common.exception.ErrorCode;
import br.com.jogadoresanonimos.common.exception.RecursoNaoEncontradoException;
import br.com.jogadoresanonimos.common.exception.UnauthenticatedException;
import br.com.jogadoresanonimos.common.ratelimit.RateLimiterService;
import br.com.jogadoresanonimos.security.MfaSecretCipher;
import br.com.jogadoresanonimos.security.TotpService;
import br.com.jogadoresanonimos.usuario.entity.Usuario;
import br.com.jogadoresanonimos.usuario.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.UUID;

@Service
public class MfaService {

    private static final String ENTIDADE_MFA = "mfa";
    private static final String STATUS_MFA_INATIVO = "MFA_INATIVO";
    private static final String STATUS_MFA_PENDENTE = "MFA_PENDENTE";
    private static final String STATUS_MFA_ATIVO = "MFA_ATIVO";

    private final UsuarioRepository usuarioRepository;
    private final TotpService totpService;
    private final MfaSecretCipher mfaSecretCipher;
    private final PasswordEncoder passwordEncoder;
    private final RateLimiterService rateLimiterService;
    private final AdminAuditoriaService adminAuditoriaService;
    private final String issuer;

    public MfaService(UsuarioRepository usuarioRepository, TotpService totpService, MfaSecretCipher mfaSecretCipher,
                       PasswordEncoder passwordEncoder, RateLimiterService rateLimiterService,
                       AdminAuditoriaService adminAuditoriaService,
                       @Value("${app.mfa.issuer:JogadoresAnonimos}") String issuer) {
        this.usuarioRepository = usuarioRepository;
        this.totpService = totpService;
        this.mfaSecretCipher = mfaSecretCipher;
        this.passwordEncoder = passwordEncoder;
        this.rateLimiterService = rateLimiterService;
        this.adminAuditoriaService = adminAuditoriaService;
        this.issuer = issuer;
    }

    @Transactional
    public MfaEnrollResponse enroll(UUID usuarioId) {
        rateLimiterService.check("mfa-enroll:" + usuarioId, 5, Duration.ofHours(1), ErrorCode.MFA_RATE_LIMITED);
        Usuario usuario = buscarUsuario(usuarioId);
        if (usuario.isMfaAtivo()) {
            throw new ConflitoDeEstadoException(ErrorCode.MFA_JA_ATIVO, "MFA já está ativo para este usuário");
        }
        String secret = totpService.gerarSecretBase32();
        usuario.iniciarEnrollmentMfa(mfaSecretCipher.cifrar(secret));
        String otpauthUri = totpService.construirOtpauthUri(issuer, usuario.getApelido(), secret);
        adminAuditoriaService.registrar(usuarioId, ENTIDADE_MFA, usuarioId, AcaoAuditoria.EDITAR,
                STATUS_MFA_INATIVO, STATUS_MFA_PENDENTE);
        return new MfaEnrollResponse(otpauthUri);
    }

    @Transactional
    public MensagemResponse confirmar(UUID usuarioId, MfaConfirmarRequest request) {
        rateLimiterService.check("mfa-confirmar:" + usuarioId, 5, Duration.ofMinutes(5), ErrorCode.MFA_RATE_LIMITED);
        Usuario usuario = buscarUsuario(usuarioId);
        if (usuario.isMfaAtivo()) {
            throw new ConflitoDeEstadoException(ErrorCode.MFA_JA_ATIVO, "MFA já está ativo para este usuário");
        }
        if (usuario.getMfaSecretCifrado() == null) {
            throw new RecursoNaoEncontradoException(ErrorCode.MFA_ENROLLMENT_INEXISTENTE, "Nenhum enrollment de MFA pendente");
        }
        String secret = mfaSecretCipher.decifrar(usuario.getMfaSecretCifrado());
        if (!totpService.validar(secret, request.codigoTotp())) {
            throw new UnauthenticatedException(ErrorCode.MFA_CODIGO_INVALIDO, "Código TOTP inválido");
        }
        usuario.confirmarMfa();
        adminAuditoriaService.registrar(usuarioId, ENTIDADE_MFA, usuarioId, AcaoAuditoria.EDITAR,
                STATUS_MFA_PENDENTE, STATUS_MFA_ATIVO);
        return new MensagemResponse("MFA ativado com sucesso.");
    }

    @Transactional
    public MensagemResponse desativar(UUID usuarioId, MfaDesativarRequest request) {
        rateLimiterService.check("mfa-desativar:" + usuarioId, 5, Duration.ofMinutes(5), ErrorCode.MFA_RATE_LIMITED);
        Usuario usuario = buscarUsuario(usuarioId);
        if (!passwordEncoder.matches(request.senha(), usuario.getSenhaHash())) {
            throw new UnauthenticatedException(ErrorCode.MFA_SENHA_INCORRETA, "Senha incorreta");
        }
        String secret = mfaSecretCipher.decifrar(usuario.getMfaSecretCifrado());
        if (!totpService.validar(secret, request.codigoTotp())) {
            throw new UnauthenticatedException(ErrorCode.MFA_CODIGO_INVALIDO, "Código TOTP inválido");
        }
        usuario.desativarMfa();
        adminAuditoriaService.registrar(usuarioId, ENTIDADE_MFA, usuarioId, AcaoAuditoria.EDITAR,
                STATUS_MFA_ATIVO, STATUS_MFA_INATIVO);
        return new MensagemResponse("MFA desativado.");
    }

    private Usuario buscarUsuario(UUID usuarioId) {
        return usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RecursoNaoEncontradoException(ErrorCode.MFA_USUARIO_NAO_ENCONTRADO, "Usuário não encontrado"));
    }
}
