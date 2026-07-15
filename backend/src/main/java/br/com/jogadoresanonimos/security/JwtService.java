package br.com.jogadoresanonimos.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

@Service
public class JwtService {

    private final SecretKey key;
    private final long ttlMinutes;

    public JwtService(@Value("${app.jwt.secret}") String secret,
                       @Value("${app.jwt.ttl-minutes:60}") long ttlMinutes) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
        this.ttlMinutes = ttlMinutes;
    }

    public String gerarToken(UUID usuarioId, String role, Instant senhaAlteradaEm) {
        Instant now = Instant.now();
        Instant expiration = now.plus(ttlMinutes, ChronoUnit.MINUTES);
        return Jwts.builder()
                .setSubject(usuarioId.toString())
                .claim("role", role)
                .claim("senhaAlteradaEm", senhaAlteradaEm.toEpochMilli())
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(expiration))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public Instant expiracaoDe(String token) {
        return parseClaims(token).getExpiration().toInstant();
    }

    public Claims parseClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }
}
