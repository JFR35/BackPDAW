package com.myobservation.auth.dto;

import java.util.Set;

/**
 * Representa la respuesta de datos de un usuario.
 * Esta clase se usa para enviar información de usuario desde la API sin exponer datos sensibles como la contraseña.
 */
public class UserResponse {
    private Long userId;
    private String firstName;
    private String lastName;
    private String email;
    private Set<String> roles;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }
}