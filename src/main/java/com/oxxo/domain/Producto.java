package com.oxxo.domain;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity @Table(name = "productos")
@Data @NoArgsConstructor @AllArgsConstructor
public class Producto {
  @Id
  private String sku;

  private String nombre;
  private String codigoBarras;
  private String categoria;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "proveedor_id")          // <— FK real
  private Proveedor proveedor;

  private BigDecimal precio = BigDecimal.ZERO;
  private BigDecimal costo  = BigDecimal.ZERO;

  private boolean activo = true;
  private Integer stockMinimo = 0;
}
