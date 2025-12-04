package com.oxxo.web;

import com.oxxo.domain.Role;
import com.oxxo.domain.Usuario;
import com.oxxo.repo.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioRepository repo;
    private final PasswordEncoder passwordEncoder;

    public UsuarioController(UsuarioRepository repo, PasswordEncoder passwordEncoder) {
        this.repo = repo;
        this.passwordEncoder = passwordEncoder;
    }

    // ---------------- DTOs ----------------

    public static class UsuarioDTO {
        public Long id;
        public String email;
        public String username;
        public String nombreCompleto;
        public String dni;
        public String rol;
        public boolean activo;
        public String creadoPor;
        public Object fechaCreacion;

        public UsuarioDTO(Usuario u) {
            this.id = u.getId();
            this.email = u.getEmail();
            this.username = u.getUsername();
            this.nombreCompleto = u.getNombreCompleto();
            this.dni = u.getDni();
            this.rol = (u.getRol() == null) ? null : u.getRol().name();
            this.activo = u.isActivo();
            this.creadoPor = u.getCreadoPor();
            this.fechaCreacion = u.getFechaCreacion();
        }
    }

    public static class UsuarioCreateRequest {
        public String email;
        public String username;
        public String nombreCompleto;
        public String dni;
        public String rol;
        public Boolean activo;
        public String password;
        // si tu front manda "notas", no rompe:
        public String notas;
    }

    public static class UsuarioUpdateRequest {
        public String email;
        public String username;
        public String nombreCompleto;
        public String dni;
        public String rol;
        public Boolean activo;
        public String password;
        public String notas;
    }

    // ---------------- Helpers ----------------

    private static String n(String s) {
        return s == null ? null : s.trim();
    }

    private static String blankToNull(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isBlank() ? null : t;
    }

    private static Role parseRole(String raw) {
        if (raw == null) return null;
        String r = raw.trim().toUpperCase();
        if (r.startsWith("ROLE_")) r = r.substring(5);
        return Role.valueOf(r); // ADMIN, CAJERO
    }

    // ---------------- CRUD ----------------

    // ✅ LISTAR (aquí deben verse los del DataSeeder)
    @GetMapping
    public List<UsuarioDTO> listar() {
        return repo.findAll()
                .stream()
                .map(UsuarioDTO::new)
                .toList();
    }

    @GetMapping("{id}")
    public ResponseEntity<UsuarioDTO> obtener(@PathVariable Long id) {
        return repo.findById(id)
                .map(u -> ResponseEntity.ok(new UsuarioDTO(u)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody UsuarioCreateRequest req, Authentication auth) {

        String email = blankToNull(req.email);
        String username = blankToNull(req.username);
        String nombre = blankToNull(req.nombreCompleto);
        String dni = blankToNull(req.dni);
        Role rol = null;

        try { rol = parseRole(req.rol); }
        catch (Exception e) { return ResponseEntity.badRequest().body("Rol inválido"); }

        if (email == null || username == null || nombre == null || rol == null)
            return ResponseEntity.badRequest().body("Faltan campos requeridos");

        if (req.password == null || req.password.trim().length() < 4)
            return ResponseEntity.badRequest().body("Password mínimo 4 caracteres");

        // únicos
        if (repo.existsByEmailIgnoreCase(email))
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Email ya existe");

        if (repo.existsByUsernameIgnoreCase(username))
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Username ya existe");

        Usuario u = new Usuario();
        u.setEmail(email);
        u.setUsername(username);
        u.setNombreCompleto(nombre);
        u.setDni(dni);
        u.setRol(rol);
        u.setActivo(req.activo == null || req.activo); // default true

        // ✅ tu entidad usa passwordHash
        u.setPasswordHash(passwordEncoder.encode(req.password.trim()));

        // creadoPor = el email del que crea (si existe auth)
        if (auth != null && auth.getName() != null) u.setCreadoPor(auth.getName());

        Usuario saved = repo.save(u);
        return ResponseEntity.status(HttpStatus.CREATED).body(new UsuarioDTO(saved));
    }

    @PutMapping("{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @RequestBody UsuarioUpdateRequest req) {

        Optional<Usuario> opt = repo.findById(id);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();

        Usuario u = opt.get();

        // email
        if (req.email != null) {
            String email = blankToNull(req.email);
            if (email == null) return ResponseEntity.badRequest().body("Email inválido");
            if (!email.equalsIgnoreCase(u.getEmail()) && repo.existsByEmailIgnoreCase(email))
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Email ya existe");
            u.setEmail(email);
        }

        // username
        if (req.username != null) {
            String username = blankToNull(req.username);
            if (username == null) return ResponseEntity.badRequest().body("Username inválido");
            if (!username.equalsIgnoreCase(u.getUsername()) && repo.existsByUsernameIgnoreCase(username))
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Username ya existe");
            u.setUsername(username);
        }

        // nombre/dni
        if (req.nombreCompleto != null) u.setNombreCompleto(blankToNull(req.nombreCompleto));
        if (req.dni != null) u.setDni(blankToNull(req.dni));

        // rol
        if (req.rol != null && !n(req.rol).isBlank()) {
            try { u.setRol(parseRole(req.rol)); }
            catch (Exception e) { return ResponseEntity.badRequest().body("Rol inválido"); }
        }

        // activo
        if (req.activo != null) u.setActivo(req.activo);

        // password (opcional)
        if (req.password != null && !req.password.trim().isBlank()) {
            String pass = req.password.trim();
            if (pass.length() < 4) return ResponseEntity.badRequest().body("Password mínimo 4 caracteres");
            u.setPasswordHash(passwordEncoder.encode(pass));
        }

        Usuario saved = repo.save(u);
        return ResponseEntity.ok(new UsuarioDTO(saved));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        if (!repo.existsById(id)) return ResponseEntity.notFound().build();
        repo.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
