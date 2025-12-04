package com.oxxo.web;

import com.oxxo.config.JwtTokenService;
import com.oxxo.domain.Usuario;
import com.oxxo.repo.UsuarioRepository;
import com.oxxo.dto.LoginRequest;
import com.oxxo.dto.LoginResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authManager;
    private final JwtTokenService       jwtTokenService;
    private final UsuarioRepository     usuarioRepository;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest req) {

        // 1) autenticar por EMAIL + password
        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        req.getEmail(),
                        req.getPassword()
                )
        );

        // 2) principal viene con el "username" interno (que es el email)
        UserDetails principal = (UserDetails) auth.getPrincipal();

        // 3) generar token JWT
        String token = jwtTokenService.generarToken(principal);

        Usuario u = usuarioRepository
        .findFirstByEmailIgnoreCaseOrderByIdAsc(principal.getUsername())
        .orElseThrow(() -> new IllegalStateException("Usuario no encontrado"));

        // 5) armar respuesta para el frontend
        LoginResponse res = new LoginResponse();
        res.setToken(token);
        res.setEmail(u.getEmail());
        res.setName(u.getNombreCompleto());
        res.setRole(u.getRol().name());

        return ResponseEntity.status(HttpStatus.OK).body(res);
    }
}
