package com.oxxo.dto;

import lombok.Getter;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonAlias;
@Getter
@Setter
public class LoginRequest {
    @JsonAlias({"username", "user", "login"})
    private String email;

    private String password;

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}

