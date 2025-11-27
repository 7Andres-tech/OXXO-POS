package com.oxxo.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PedidoRequest {
  @NotBlank private String proveedorId;
  @NotNull  private LocalDate estimada;

  @NotEmpty private List<Item> items;

  @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
  public static class Item {
    @NotBlank private String sku;
    @NotNull @Positive private Integer cantidad;
  }
}
