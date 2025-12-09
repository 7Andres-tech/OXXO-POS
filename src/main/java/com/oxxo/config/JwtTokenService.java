package com.oxxo.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

@Service
public class JwtTokenService {

    private final Key key;

    private final long adminExpMillis;
    private final long cajeroExpMillis;
    private final long defaultExpMillis;

    public JwtTokenService(
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.adminExpMinutes:1440}") long adminExpMinutes,
            @Value("${app.jwt.cajeroExpMinutes:600}") long cajeroExpMinutes,
            @Value("${app.jwt.defaultExpMinutes:480}") long defaultExpMinutes
    ) {
        if (secret == null || secret.isBlank()) {
            throw new IllegalArgumentException("Falta app.jwt.secret en application.properties");
        }

        // app.jwt.secret está en base64
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret.trim()));

        this.adminExpMillis = adminExpMinutes * 60_000L;
        this.cajeroExpMillis = cajeroExpMinutes * 60_000L;
        this.defaultExpMillis = defaultExpMinutes * 60_000L;
    }

    // ✅ ESTE es el método que tu login está intentando usar
    public String generarToken(UserDetails userDetails) {
        String subject = userDetails.getUsername(); // en tu proyecto suele ser el email
        String role = extractRole(userDetails);     // ADMIN / CAJERO / etc.

        long ttl = switch (role) {
            case "ADMIN" -> adminExpMillis;
            case "CAJERO" -> cajeroExpMillis;
            default -> defaultExpMillis;
        };

        Date now = new Date();
        Date exp = new Date(now.getTime() + ttl);

        return Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(exp)
                .claim("role", role)     // lo lee tu filtro
                .claim("rol", role)      // por si tu frontend usa "rol"
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String getSubject(String token) {
        return getClaims(token).getSubject();
    }

    public String getRole(String token) {
        Claims c = getClaims(token);
        String r = c.get("role", String.class);
        if (r == null || r.isBlank()) r = c.get("rol", String.class);
        return r;
    }

    public boolean isValid(String token) {
        try {
            Claims c = getClaims(token);
            return c.getExpiration() != null && c.getExpiration().after(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private String extractRole(UserDetails userDetails) {
        // En Spring Security normalmente viene ROLE_ADMIN, ROLE_CAJERO, etc.
        for (GrantedAuthority a : userDetails.getAuthorities()) {
            if (a == null) continue;
            String s = a.getAuthority();
            if (s == null) continue;
            s = s.trim().toUpperCase();
            if (s.startsWith("ROLE_")) s = s.substring(5);
            if (!s.isBlank()) return s;
        }
        return "DEFAULT";
    }
}
