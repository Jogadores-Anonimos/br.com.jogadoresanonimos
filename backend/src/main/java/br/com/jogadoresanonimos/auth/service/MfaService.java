package br.com.jogadoresanonimos.auth.service;

import br.com.jogadoresanonimos.auth.dto.MfaConfirmarRequest;
import br.com.jogadoresanonimos.auth.dto.MfaDesativarRequest;
import br.com.jogadoresanonimos.auth.dto.MfaEnrollResponse;
import br.com.jogadoresanonimos.auth.dto.MensagemResponse;
import br.com.jogadoresanonimos.common.exception.ConflitoDeEstadoException;
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

    private final UsuarioRepository usuarioRepository;
    private final TotpService totpService;
    private final MfaSecretCipher mfaSecretCipher;
    private final PasswordEncoder passwordEncoder;
    private final RateLimiterService rateLimiterService;
    private final String issuer;

    public MfaService(UsuarioRepository usuarioRepository, TotpService totpService, MfaSecretCipher mfaSecretCipher,
                       PasswordEncoder passwordEncoder, RateLimiterService rateLimiterService,
                       @Value("${app.mfa.issuer:JogadoresAnonimos}") String issuer) {
        this.usuarioRepository = usuarioRepository;
        this.totpService = totpService;
        this.mfaSecretCipher = mfaSecretCipher;
        this.passwordEncoder = passwordEncoder;
        this.rateLimiterService = rateLimiterService;
        this.issuer = issuer;
    }

    @Transactional
    public MfaEnrollResponse enroll(UUID usuarioId) {
        rateLimiterService.check("mfa-enroll:" + usuarioId, 5, Duration.ofHours(1));
        Usuario usuario = buscarUsuario(usuarioId);
        if (usuario.isMfaAtivo()) {
            throw new ConflitoDeEstadoException("MFA já está ativo para este usuário");
        }
        String secret = totpService.gerarSecretBase32();
        usuario.iniciarEnrollmentMfa(mfaSecretCipher.cifrar(secret));
        String otpauthUri = totpService.construirOtpauthUri(issuer, usuario.getApelido(), secret);
        return new MfaEnrollResponse(otpauthUri);
    }

    @Transactional
    public MensagemResponse confirmar(UUID usuarioId, MfaConfirmarRequest request) {
        rateLimiterService.check("mfa-confirmar:" + usuarioId, 5, Duration.ofMinutes(5));
        Usuario usuario = buscarUsuario(usuarioId);
        if (usuario.isMfaAtivo()) {
            throw new ConflitoDeEstadoException("MFA já está ativo para este usuário");
        }
        if (usuario.getMfaSecretCifrado() == null) {
            throw new RecursoNaoEncontradoException("Nenhum enrollment de MFA pendente");
        }
        String secret = mfaSecretCipher.decifrar(usuario.getMfaSecretCifrado());
        if (!totpService.validar(secret, request.codigoTotp())) {
            throw new UnauthenticatedException("Código TOTP inválido");
        }
        usuario.confirmarMfa();
        return new MensagemResponse("MFA ativado com sucesso.");
    }

    @Transactional
    public MensagemResponse desativar(UUID usuarioId, MfaDesativarRequest request) {
        rateLimiterService.check("mfa-desativar:" + usuarioId, 5, Duration.ofMinutes(5));
        Usuario usuario = buscarUsuario(usuarioId);
        if (!passwordEncoder.matches(request.senha(), usuario.getSenhaHash())) {
            throw new UnauthenticatedException("Senha incorreta");
        }
        usuario.desativarMfa();
        return new MensagemResponse("MFA desativado.");
    }

    private Usuario buscarUsuario(UUID usuarioId) {
        return usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado"));
    }
}
