package com.oxxo.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="inventario")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Inventario {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @OneToOne
  @JoinColumn(name = "sku", referencedColumnName = "sku", unique = true)
  private Producto producto;

  private Integer stock = 0;
  private Integer minimo = 0;
  private String vence; // para simplificar; puede cambiar a LocalDate
}
