package com.oxxo.service;

import com.oxxo.domain.Role;
import com.oxxo.domain.Usuario;
import com.oxxo.dto.UsuarioCreateRequest;
import com.oxxo.repo.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository repo;
    private final PasswordEncoder   passwordEncoder;

    public Usuario crear(UsuarioCreateRequest req, String creadoPor) {

        String email = req.getEmail().trim().toLowerCase();
        String username = req.getUsername().trim();

if (repo.existsByEmailIgnoreCase(email)) {
    throw new IllegalArgumentException("Ya existe un usuario con ese email");
}

if (repo.existsByUsernameIgnoreCase(username)) {
    throw new IllegalArgumentException("Ya existe un usuario con ese username");
}


        Usuario u = new Usuario();
        u.setUsername(req.getUsername());
        u.setEmail(req.getEmail());
        u.setNombreCompleto(req.getNombreCompleto());
        u.setDni(req.getDni());
        u.setRol(Role.valueOf(req.getRol().toUpperCase()));
        u.setActivo(req.isActivo());
        u.setPasswordHash(passwordEncoder.encode(req.getPassword()));

        u.setCreadoPor(creadoPor);
        u.setFechaCreacion(LocalDateTime.now());

        return repo.save(u);
    }
}
