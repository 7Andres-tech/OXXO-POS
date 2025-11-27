package com.oxxo.domain;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name="venta_detalle")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VentaDetalle {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch=FetchType.LAZY)
  @JoinColumn(name="venta_id")
  private Venta venta;

  @ManyToOne(fetch=FetchType.EAGER)
  @JoinColumn(name="sku")
  private Producto producto;

  private Integer cantidad;
  private BigDecimal precioUnit = BigDecimal.ZERO;
  private BigDecimal subtotal = BigDecimal.ZERO;
}
