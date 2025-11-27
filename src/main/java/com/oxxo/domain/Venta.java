package com.oxxo.domain;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name="ventas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Venta {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private LocalDateTime fecha = LocalDateTime.now();
  private BigDecimal total = BigDecimal.ZERO;
  private String medioPago;
  private String estado; // p.ej. PENDIENTE, PAGADO, ANULADO

  @OneToMany(mappedBy = "venta", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<VentaDetalle> items = new ArrayList<>();
}
