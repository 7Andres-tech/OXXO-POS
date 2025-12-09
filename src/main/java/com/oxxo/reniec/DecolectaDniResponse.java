package com.oxxo.reniec;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DecolectaDniResponse {

    @JsonProperty("first_name")
    private String firstName;

    @JsonProperty("first_last_name")
    private String firstLastName;

    @JsonProperty("second_last_name")
    private String secondLastName;

    @JsonProperty("full_name")
    private String fullName;

    @JsonProperty("document_number")
    private String documentNumber;

    public ReniecDto toDto() {
        ReniecDto dto = new ReniecDto();
        dto.setNumeroDocumento(documentNumber);
        dto.setNombres(firstName);
        dto.setApellidoPaterno(firstLastName);
        dto.setApellidoMaterno(secondLastName);
        return dto;
    }
}
