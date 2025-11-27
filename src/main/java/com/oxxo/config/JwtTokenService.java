package com.oxxo.config;

import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.oxxo.domain.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Service
public class JwtTokenService {

    @Value("${jwt.secret:mi_super_secreto_oxxo_2025}")
    private String secret;

    @Value("${jwt.expiration:86400000}") // 1 día en milisegundos
    private long expiration;

    // Crear token firmado (JWT real HS256)
    public String generateToken(User user) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .setSubject(user.getEmail())
                .claim("role", user.getRole().name())
                .claim("name", user.getName())
                .setIssuedAt(now)
                .setExpiration(exp)
                // API antigua de JJWT 0.9.x
                .signWith(SignatureAlgorithm.HS256, secret.getBytes())
                .compact();
    }

    // Validar token y devolver Claims (API antigua)
    public Claims validateToken(String token) {
        return Jwts.parser()
                .setSigningKey(secret.getBytes())
                .parseClaimsJws(token.replace("Bearer ", ""))
                .getBody();
    }

    public String extractEmail(String token) {
        return validateToken(token).getSubject();
    }

    public String extractRole(String token) {
        return (String) validateToken(token).get("role");
    }
}
