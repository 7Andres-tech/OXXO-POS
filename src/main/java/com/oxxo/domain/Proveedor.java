package com.oxxo.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity @Table(name="proveedores")
@Data @NoArgsConstructor @AllArgsConstructor
public class Proveedor {
  @Id
  private String id;          // ej: PV001

  private String nombre;
  private String contacto;
  private String email;
  private String telefono;

  private Integer leadTimeDias = 3;  // promedio de entrega en días
  private Boolean activo = true;
}
