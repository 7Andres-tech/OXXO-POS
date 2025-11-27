package com.oxxo.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity @Table(name="pedido_items")
@Data @NoArgsConstructor @AllArgsConstructor
public class PedidoItem {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(optional = false)
  @JoinColumn(name="pedido_id")
  private PedidoCompra pedido;

  @ManyToOne(optional = false)
  @JoinColumn(name="sku")
  private Producto producto;

  private Integer cantidad;
}
