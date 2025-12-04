package com.oxxo.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
// package com.oxxo.dto;

public class LoginResponse {
    private String token;
    private String email;
    private String name;
    private String role;

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}