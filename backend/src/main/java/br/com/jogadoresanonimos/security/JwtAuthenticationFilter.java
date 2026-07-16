package br.com.jogadoresanonimos.security;

import br.com.jogadoresanonimos.security.repository.JwtDenylistRepository;
import br.com.jogadoresanonimos.usuario.entity.Usuario;
import br.com.jogadoresanonimos.usuario.repository.UsuarioRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UsuarioRepository usuarioRepository;
    private final JwtDenylistRepository jwtDenylistRepository;

    public JwtAuthenticationFilter(JwtService jwtService, UsuarioRepository usuarioRepository,
                                    JwtDenylistRepository jwtDenylistRepository) {
        this.jwtService = jwtService;
        this.usuarioRepository = usuarioRepository;
        this.jwtDenylistRepository = jwtDenylistRepository;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
                                     @NonNull FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            try {
                Claims claims = jwtService.parseClaims(token);
                String jti = claims.getId();
                if (jti == null || !jwtDenylistRepository.existsByJti(jti)) {
                    UUID usuarioId = UUID.fromString(claims.getSubject());
                    long tokenSenhaAlteradaEm = claims.get("senhaAlteradaEm", Long.class);
                    Optional<Usuario> usuarioOpt = usuarioRepository.findById(usuarioId);
                    if (usuarioOpt.isPresent()) {
                        Usuario usuario = usuarioOpt.get();
                        boolean tokenAindaValido = usuario.getSenhaAlteradaEm().toEpochMilli() == tokenSenhaAlteradaEm;
                        if (tokenAindaValido) {
                            Instant expiraEm = claims.getExpiration().toInstant();
                            AuthenticatedUser principal = new AuthenticatedUser(usuario.getId(), usuario.getApelido(),
                                    usuario.getRole().name(), jti, expiraEm);
                            var authorities = List.of(new SimpleGrantedAuthority("ROLE_" + usuario.getRole().name()));
                            var authentication = new UsernamePasswordAuthenticationToken(principal, null, authorities);
                            SecurityContextHolder.getContext().setAuthentication(authentication);
                        }
                    }
                }
            } catch (JwtException | IllegalArgumentException ex) {
                SecurityContextHolder.clearContext();
            }
        }
        filterChain.doFilter(request, response);
    }
}
