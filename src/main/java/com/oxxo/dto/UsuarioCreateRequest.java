package com.oxxo.dto;

import lombok.Data;

/**
 * DTO para crear usuarios desde el formulario del ADMIN.
 */
@Data
public class UsuarioCreateRequest {

    private String username;
    private String nombreCompleto;
    private String dni;
    private String email;

    /**
     * Rol como texto: "ADMIN" o "CAJERO"
     */
    private String rol;

    /**
     * Si el usuario estará activo al crearse.
     */
    private boolean activo = true;

    /**
     * Password plano enviado desde el formulario.
     */
    private String password;
}
