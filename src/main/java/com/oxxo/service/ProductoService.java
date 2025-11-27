package com.oxxo.service;

import com.oxxo.domain.Producto;
import com.oxxo.dto.ProductoRequest;
import com.oxxo.dto.ProductoResponse;
import com.oxxo.repo.ProductoRepository;
import com.oxxo.repo.ProveedorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductoService {

  private final ProductoRepository productoRepo;
  private final ProveedorRepository proveedorRepo;

  /* ===================== MAPPER ===================== */

  private ProductoResponse toDto(Producto p){
    var prov = p.getProveedor();
    return new ProductoResponse(
        p.getSku(),
        p.getNombre(),
        p.getCodigoBarras(),
        p.getCategoria(),
        prov != null ? prov.getId()      : null,
        prov != null ? prov.getNombre()  : null,
        p.getPrecio(),
        p.getCosto(),
        p.isActivo(),
        p.getStockMinimo()
    );
  }

  private void copyFromRequest(Producto p, ProductoRequest r) {
    p.setSku(r.sku());
    p.setNombre(r.nombre());
    p.setCodigoBarras(r.codigoBarras());
    p.setCategoria(r.categoria());
    p.setPrecio(r.precio());
    p.setCosto(r.costo());
    p.setActivo(r.activo());
    p.setStockMinimo(r.stockMinimo() == null ? 0 : r.stockMinimo());

    if (r.proveedorId() != null) {
      p.setProveedor(proveedorRepo.findById(r.proveedorId()).orElse(null));
    } else {
      p.setProveedor(null);
    }
  }

  /* ===================== CRUD ===================== */

  @Transactional(readOnly = true)
  public List<ProductoResponse> listar(){
    return productoRepo.findAllWithProveedor()
        .stream()
        .map(this::toDto)
        .toList();
  }

  @Transactional
  public ProductoResponse crear(ProductoRequest r){
    // si quieres, puedes validar que no exista el SKU
    // if (productoRepo.existsById(r.sku())) { ... }

    var p = new Producto();
    copyFromRequest(p, r);
    return toDto(productoRepo.save(p));
  }

  @Transactional
  public ProductoResponse actualizar(String sku, ProductoRequest r){
    var p = productoRepo.findById(sku).orElseThrow();
    // normalmente NO cambiamos el SKU, sólo el resto de campos
    p.setNombre(r.nombre());
    p.setCodigoBarras(r.codigoBarras());
    p.setCategoria(r.categoria());
    p.setPrecio(r.precio());
    p.setCosto(r.costo());
    p.setActivo(r.activo());
    p.setStockMinimo(r.stockMinimo() == null ? 0 : r.stockMinimo());

    if (r.proveedorId() != null) {
      p.setProveedor(proveedorRepo.findById(r.proveedorId()).orElse(null));
    } else {
      p.setProveedor(null);
    }

    return toDto(p);
  }

  /* ===================== ESTADO (ACTIVO / INACTIVO) ===================== */

  // Tu método original
  @Transactional
  public ProductoResponse activar(String sku, boolean value){
    var p = productoRepo.findById(sku).orElseThrow();
    p.setActivo(value);
    return toDto(p);
  }

  // Método que usa el controlador: PUT /api/productos/{sku}/activo
  @Transactional
  public void actualizarEstado(String sku, boolean activo) {
    activar(sku, activo);
  }

  /* ===================== ELIMINAR ===================== */

  @Transactional
  public void eliminar(String sku){
    if (productoRepo.existsById(sku)) {
      productoRepo.deleteById(sku);
    }
  }
}
