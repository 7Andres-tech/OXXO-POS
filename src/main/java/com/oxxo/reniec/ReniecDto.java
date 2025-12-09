package com.oxxo.reniec;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ReniecDto {

    // tiene que llamarse EXACTAMENTE así para que existan
    // los setters: setNumeroDocumento, setNombres, etc.
    private String numeroDocumento;
    private String nombres;
    private String apellidoPaterno;
    private String apellidoMaterno;

    public String getNombreCompleto() {
        StringBuilder sb = new StringBuilder();

        if (apellidoPaterno != null && !apellidoPaterno.isBlank()) {
            sb.append(apellidoPaterno.trim());
        }
        if (apellidoMaterno != null && !apellidoMaterno.isBlank()) {
            if (sb.length() > 0) sb.append(" ");
            sb.append(apellidoMaterno.trim());
        }
        if (nombres != null && !nombres.isBlank()) {
            if (sb.length() > 0) sb.append(" ");
            sb.append(nombres.trim());
        }
        return sb.toString();
    }
}
