package com.oxxo.config;

import com.oxxo.domain.Role;
import com.oxxo.domain.User;
import com.oxxo.repo.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@Configuration
public class DataSeeder {

  // Si pones seed.reset-passwords=true en application.properties/yml,
  // volverá a escribir el hash aunque el usuario ya exista.
  @Value("${seed.reset-passwords:false}")
  private boolean resetPasswords;

  static record SeedUser(String email, String name, String pass, Role role) {}

  @Bean
  CommandLineRunner seedUsers(UserRepository repo, PasswordEncoder enc) {
    return args -> {
      List<SeedUser> seeds = List.of(
          new SeedUser("admin@oxxo.pe",  "Administrador", "Admin#2025", Role.ADMIN),
          new SeedUser("cajero1@oxxo.pe","Cajero 01",     "Caja#2025",  Role.CAJERO),
          new SeedUser("cajero2@oxxo.pe","Cajero 02",     "Caja2#2025", Role.CAJERO)
      );

      for (SeedUser s : seeds) {
        User u = repo.findByEmailIgnoreCase(s.email()).orElseGet(User::new);
        u.setEmail(s.email());
        u.setName(s.name());
        u.setRole(s.role());
        u.setEnabled(true);

        if (u.getPasswordHash() == null || u.getPasswordHash().isBlank() || resetPasswords) {
          u.setPasswordHash(enc.encode(s.pass()));
        }
        repo.save(u);
      }
    };
  }
}
