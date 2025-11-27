package com.oxxo.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name="movimientos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovimientoInventario {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private LocalDateTime fecha = LocalDateTime.now();

  public enum Tipo { INGRESO, SALIDA, AJUSTE, VENTA, DEVOLUCION }
  @Enumerated(EnumType.STRING)
  private Tipo tipo;

  private Integer cantidad;
  private String motivo;

  @ManyToOne
  @JoinColumn(name="sku")
  private Producto producto;

  private String refTipo;
  private String refId;
}
