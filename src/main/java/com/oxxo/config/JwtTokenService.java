package com.oxxo.config;

import com.oxxo.domain.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

@Service
public class JwtTokenService {

    @Value("${app.jwt.secret}")
    private String secretB64;

    @Value("${app.jwt.adminExpMinutes:1440}")
    private long adminExpMinutes;

    @Value("${app.jwt.cajeroExpMinutes:600}")
    private long cajeroExpMinutes;

    @Value("${app.jwt.defaultExpMinutes:480}")
    private long defaultExpMinutes;

    private SecretKey key;

    @PostConstruct
    void init() {
        byte[] bytes = Decoders.BASE64.decode(secretB64);
        this.key = Keys.hmacShaKeyFor(bytes);
    }

    public String generateToken(String email, Role rol) {
        long expMinutes = expMinutesFor(rol);

        Instant now = Instant.now();
        Date iat = Date.from(now);
        Date exp = Date.from(now.plusSeconds(expMinutes * 60));

        Map<String, Object> claims = Map.of(
                "role", rol != null ? rol.name() : null,
                "rol",  rol != null ? rol.name() : null // por compat si en algún lado lees "rol"
        );

        return Jwts.builder()
                .setSubject(email == null ? null : email.trim().toLowerCase())
                .setIssuedAt(iat)
                .setExpiration(exp)
                .addClaims(claims)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean isValid(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public String getSubject(String token) {
        return parseClaims(token).getSubject();
    }

    public String getRole(String token) {
        Claims c = parseClaims(token);
        Object r = c.get("role");
        if (r == null) r = c.get("rol");
        return r == null ? null : String.valueOf(r);
    }

    private Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private long expMinutesFor(Role role) {
        if (role == null) return defaultExpMinutes;
        return switch (role) {
            case ADMIN -> adminExpMinutes;
            case CAJERO -> cajeroExpMinutes;
            default -> defaultExpMinutes;
        };
    }
}
