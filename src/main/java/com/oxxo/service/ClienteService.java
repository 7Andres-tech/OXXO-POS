package com.oxxo.service;

import com.oxxo.domain.Cliente;
import com.oxxo.repo.ClienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service @RequiredArgsConstructor
public class ClienteService {

  private final ClienteRepository repo;

  public List<Cliente> listar() {
    return repo.findAll(Sort.by("nombre").ascending());
  }

  public Cliente crear(Cliente c) { return repo.save(c); }

  public Cliente actualizar(String doc, Cliente c) {
    c.setDoc(doc);
    return repo.save(c);
  }

  public void eliminar(String doc) { repo.deleteById(doc); }

  @Transactional
  public Cliente registrarCompra(String doc, int puntosDelta) {
    var c = repo.findById(doc).orElseThrow();
    c.setCompras((c.getCompras()==null?0:c.getCompras()) + 1);
    c.setPuntos((c.getPuntos()==null?0:c.getPuntos()) + Math.max(0,puntosDelta));
    c.setUltimaCompra(LocalDateTime.now());
    return c;
  }

  public record ClientesKpis(long clientes, long conCompras, long puntosTotales, BigDecimal ticketPromHist) {}

  public ClientesKpis kpis() {
    long tot  = repo.count();
    long conC = repo.countByComprasGreaterThan(0);
    long pts  = repo.sumPuntos();
    // Como tu entidad Venta aún no tiene relación con Cliente, dejamos 0 por ahora:
    return new ClientesKpis(tot, conC, pts, BigDecimal.ZERO);
  }
}
