package com.oxxo.service;

import com.oxxo.domain.Proveedor;
import com.oxxo.dto.ProveedoresKpis;
import com.oxxo.repo.PedidoCompraRepository;
import com.oxxo.repo.ProveedorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service @RequiredArgsConstructor
public class ProveedorService {

  private final ProveedorRepository repo;
  private final PedidoCompraRepository pedidos;

  public List<Proveedor> listar() {
    return repo.findAll(Sort.by("nombre").ascending());
  }

  public Proveedor crear(Proveedor p){ return repo.save(p); }

  public Proveedor actualizar(String id, Proveedor p){
    p.setId(id);
    return repo.save(p);
  }

  @Transactional
  public Proveedor activar(String id, boolean value) {
    var pr = repo.findById(id).orElseThrow();
    pr.setActivo(value);
    return pr;
  }

  public void eliminar(String id){ repo.deleteById(id); }

  public ProveedoresKpis kpis() {
    long activos = repo.countByActivoTrue();
    Double lead  = repo.leadTimePromedioDias();
    long pend    = pedidos.countByEstado(com.oxxo.domain.PedidoCompra.Estado.PENDIENTE);
    long total   = pedidos.totalPedidos();
    double cumpl = total == 0 ? 0.0 :
        (pedidos.countByEstado(com.oxxo.domain.PedidoCompra.Estado.RECIBIDO) * 100.0) / total;

    return new ProveedoresKpis(activos, lead == null ? 0.0 : lead, pend, cumpl);
  }
}