package com.oxxo.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.Instant;

@Entity @Table(name = "users")
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false, unique=true)
    private String email;

    @Column(nullable=false)
    private String name;

    @Column(nullable=false, name="password_hash")
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false)
    private Role role;

    @Column(nullable=false)
    private boolean enabled = true;

    @CreationTimestamp
    private Instant createdAt;

    // getters/setters
    public Long getId(){return id;}
    public void setId(Long id){this.id = id;}
    public String getEmail(){return email;}
    public void setEmail(String email){this.email = email;}
    public String getName(){return name;}
    public void setName(String name){this.name = name;}
    public String getPasswordHash(){return passwordHash;}
    public void setPasswordHash(String passwordHash){this.passwordHash = passwordHash;}
    public Role getRole(){return role;}
    public void setRole(Role role){this.role = role;}
    public boolean isEnabled(){return enabled;}
    public void setEnabled(boolean enabled){this.enabled = enabled;}
    public Instant getCreatedAt(){return createdAt;}
}
