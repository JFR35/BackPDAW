package com.myobservation.auth.entity;

import jakarta.persistence.*;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * Representa los roles en el sistema de autenticación.
 */
@Table(name = "roles")
@Entity
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roleId;

    @Column(nullable = false, unique = true)
    private String name;

    public Role() {
    }


    public Role(String name) {
        this.name = name;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
