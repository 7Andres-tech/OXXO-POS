package com.oxxo.config;

import com.oxxo.domain.Usuario;
import com.oxxo.repo.UsuarioRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

@Configuration
public class UserDetailsConfig {

  @Bean
  public UserDetailsService userDetailsService(UsuarioRepository usuarioRepository) {

    return login -> {
      if (login == null || login.trim().isEmpty()) {
        throw new UsernameNotFoundException("Usuario no encontrado: " + login);
      }

      String key = login.trim().toLowerCase();

      // ✅ 1) Buscar por email (tu caso normal: login con email)
      Optional<Usuario> opt = usuarioRepository.findFirstByEmailIgnoreCaseOrderByIdAsc(key);

      // ✅ 2) Fallback: si alguien intenta loguear con username
      if (opt.isEmpty()) {
        opt = usuarioRepository.findFirstByUsernameIgnoreCaseOrderByIdAsc(key);
      }

      Usuario u = opt
          .filter(Usuario::isActivo)
          .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + login));

      // ✅ IMPORTANTE:
      // - User.roles("ADMIN") => crea autoridad ROLE_ADMIN
      // - Aquí ponemos authority "ADMIN"/"CAJERO" para que te calce con tu SecurityBeans (que acepta ambos)
      return User.withUsername(u.getEmail())                 // principal = email
          .password(u.getPasswordHash())                    // tu campo real
          .authorities(u.getRol().name())                   // "ADMIN" o "CAJERO"
          .build();
    };
  }
}
