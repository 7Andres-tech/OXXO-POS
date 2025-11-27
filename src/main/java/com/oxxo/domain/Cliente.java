package com.oxxo.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity @Table(name="clientes")
@Data @NoArgsConstructor @AllArgsConstructor
public class Cliente {
  @Id
  private String doc;              // DNI/Doc
  private String nombre;
  private String telefono;
  private String email;

  private Integer compras = 0;
  private Integer puntos  = 0;
  private LocalDateTime ultimaCompra;
  private Boolean activo = true;
}
