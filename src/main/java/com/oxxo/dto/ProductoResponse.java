package com.oxxo.dto;

import java.math.BigDecimal;

public class ProductoResponse {
  private String sku;
  private String nombre;
  private String codigoBarras;
  private String categoria;
  private String proveedorId;
  private String proveedorNombre;
  private BigDecimal precio;
  private BigDecimal costo;
  private boolean activo;
  private Integer stockMinimo;

  public ProductoResponse() {}

  public ProductoResponse(String sku, String nombre, String codigoBarras, String categoria,
                          String proveedorId, String proveedorNombre,
                          BigDecimal precio, BigDecimal costo, boolean activo, Integer stockMinimo) {
    this.sku = sku;
    this.nombre = nombre;
    this.codigoBarras = codigoBarras;
    this.categoria = categoria;
    this.proveedorId = proveedorId;
    this.proveedorNombre = proveedorNombre;
    this.precio = precio;
    this.costo = costo;
    this.activo = activo;
    this.stockMinimo = stockMinimo;
  }

  // getters y setters
  public String getSku() { return sku; }
  public void setSku(String sku) { this.sku = sku; }
  public String getNombre() { return nombre; }
  public void setNombre(String nombre) { this.nombre = nombre; }
  public String getCodigoBarras() { return codigoBarras; }
  public void setCodigoBarras(String codigoBarras) { this.codigoBarras = codigoBarras; }
  public String getCategoria() { return categoria; }
  public void setCategoria(String categoria) { this.categoria = categoria; }
  public String getProveedorId() { return proveedorId; }
  public void setProveedorId(String proveedorId) { this.proveedorId = proveedorId; }
  public String getProveedorNombre() { return proveedorNombre; }
  public void setProveedorNombre(String proveedorNombre) { this.proveedorNombre = proveedorNombre; }
  public BigDecimal getPrecio() { return precio; }
  public void setPrecio(BigDecimal precio) { this.precio = precio; }
  public BigDecimal getCosto() { return costo; }
  public void setCosto(BigDecimal costo) { this.costo = costo; }
  public boolean isActivo() { return activo; }
  public void setActivo(boolean activo) { this.activo = activo; }
  public Integer getStockMinimo() { return stockMinimo; }
  public void setStockMinimo(Integer stockMinimo) { this.stockMinimo = stockMinimo; }
}
