package com.oxxo.web;

import com.oxxo.domain.Cliente;
import com.oxxo.repo.ClienteRepository;
import com.oxxo.reniec.ReniecDto;
import com.oxxo.reniec.ReniecService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clientes")
@RequiredArgsConstructor
public class ClienteController {

    private final ClienteRepository repo;
    private final ReniecService reniecService;

    // ===== LISTAR =====
    @GetMapping
    public List<Cliente> list() {
        return repo.findAll();
    }

    // ===== CREAR NORMAL (Clientes frecuentes) =====
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Cliente create(@RequestBody Cliente cli) {

        if (cli.getDoc() == null || cli.getDoc().isBlank()) {
            throw new IllegalArgumentException("El doc/DNI es obligatorio");
        }

        // Si no mandas nombre y el doc es un DNI de 8 dígitos → intenta RENIEC
        if ((cli.getNombre() == null || cli.getNombre().isBlank())
                && cli.getDoc().matches("\\d{8}")) {

            ReniecDto dto = reniecService.consultarDni(cli.getDoc());
            if (dto != null) {
                cli.setNombre(dto.getNombreCompleto());
            }
        }

        if (cli.getCompras() == null) cli.setCompras(0);
        if (cli.getPuntos() == null) cli.setPuntos(0);
        if (cli.getActivo() == null) cli.setActivo(true);

        return repo.save(cli);
    }

    // ===== ACTUALIZAR =====
    @PutMapping("/{doc}")
    public Cliente update(@PathVariable String doc, @RequestBody Cliente cli) {
        Cliente db = repo.findById(doc).orElseThrow();

        db.setNombre(cli.getNombre());
        db.setTelefono(cli.getTelefono());
        db.setEmail(cli.getEmail());
        db.setPuntos(cli.getPuntos());
        db.setActivo(cli.getActivo());

        return repo.save(db);
    }

    // ===== ELIMINAR =====
    @DeleteMapping("/{doc}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String doc) {
        repo.deleteById(doc);
    }

    // ===== CLIENTE RÁPIDO PARA VENTAS =====
    @PostMapping("/rapido/{dni}")
    public ResponseEntity<Cliente> crearRapido(@PathVariable String dni) {

        // busca si ya existe
        Cliente existente = repo.findById(dni).orElse(null);
        if (existente != null) {
            return ResponseEntity.ok(existente);
        }

        String nombre = "Cliente sin nombre";

        // intenta obtener nombre de RENIEC
        if (dni.matches("\\d{8}")) {
            ReniecDto dto = reniecService.consultarDni(dni);
            if (dto != null && dto.getNombreCompleto() != null && !dto.getNombreCompleto().isBlank()) {
                nombre = dto.getNombreCompleto();
            }
        }

        Cliente c = new Cliente();
        c.setDoc(dni);
        c.setNombre(nombre);
        c.setCompras(0);
        c.setPuntos(0);
        c.setActivo(true);

        Cliente guardado = repo.save(c);
        return ResponseEntity.status(HttpStatus.CREATED).body(guardado);
    }
}
