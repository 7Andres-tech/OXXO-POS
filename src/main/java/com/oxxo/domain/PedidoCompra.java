package com.oxxo.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity @Table(name="pedidos_compra")
@Data @NoArgsConstructor @AllArgsConstructor
public class PedidoCompra {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;                       // puedes mostrar como "PO-0001" en front

  @ManyToOne(optional = false)
  @JoinColumn(name="proveedor_id")
  private Proveedor proveedor;

  private LocalDate fecha = LocalDate.now();
  private LocalDate estimada;           // fecha estimada de entrega

  public enum Estado { PENDIENTE, RECIBIDO, CANCELADO }
  @Enumerated(EnumType.STRING)
  private Estado estado = Estado.PENDIENTE;

  @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<PedidoItem> items = new ArrayList<>();
}
