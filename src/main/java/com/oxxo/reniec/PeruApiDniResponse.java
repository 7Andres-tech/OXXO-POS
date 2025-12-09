package com.oxxo.reniec;

import com.fasterxml.jackson.annotation.JsonAlias;

public class PeruApiDniResponse {

    @JsonAlias({ "nombres", "name" })
    public String nombres;

    @JsonAlias({ "apellidoPaterno", "apellido_paterno" })
    public String apellidoPaterno;

    @JsonAlias({ "apellidoMaterno", "apellido_materno" })
    public String apellidoMaterno;

    public ReniecDto toDto(String dni) {
        ReniecDto dto = new ReniecDto();
        dto.setNumeroDocumento(dni);
        dto.setNombres(nombres);
        dto.setApellidoPaterno(apellidoPaterno);
        dto.setApellidoMaterno(apellidoMaterno);
        return dto;
    }
}
