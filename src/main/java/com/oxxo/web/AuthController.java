package com.oxxo.web;

import java.util.Map;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.oxxo.repo.UserRepository;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

  record LoginReq(String email, String password) {}
  record LoginRes(String email, String name, String role, String token) {}

  private final UserRepository users;
  private final PasswordEncoder enc;

  public AuthController(UserRepository users, PasswordEncoder enc) {
    this.users = users;
    this.enc = enc;
  }

  @PostMapping("/login")
  public ResponseEntity<?> login(@RequestBody LoginReq req) {
    var u = users.findByEmailIgnoreCase(req.email()).orElse(null);
    if (u == null) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body(Map.of("message","Usuario no existe"));
    }
    if (!u.isEnabled()) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body(Map.of("message","Usuario inactivo"));
    }
    if (!enc.matches(req.password(), u.getPasswordHash())) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body(Map.of("message","Contraseña incorrecta"));
    }

    // 🔸 Token “dummy”, solo para que el front guarde algo
    String token = "tok_" + UUID.randomUUID();

    var res = new LoginRes(
        u.getEmail(),
        u.getName(),
        u.getRole().name(),
        token
    );
    return ResponseEntity.ok(res);
  }

  @GetMapping("/whoami")
  public Map<String,Object> whoami() {
    return Map.of("ok", true);
  }
}
