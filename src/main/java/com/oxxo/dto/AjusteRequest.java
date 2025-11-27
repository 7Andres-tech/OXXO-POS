package com.oxxo.dto;

import com.oxxo.domain.MovimientoInventario;
import lombok.Builder;

@Builder
public record AjusteRequest(
        String sku,
        MovimientoInventario.Tipo tipo,
        int cantidad,
        String motivo
) {}
