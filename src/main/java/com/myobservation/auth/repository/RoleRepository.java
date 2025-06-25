package com.myobservation.auth.repository;

import com.myobservation.auth.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio para Roles
 * Extiende de {@link JpaRepository} para proporcionar
 * los métodos del acceso a datos
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    // Buscar por roleName
    Optional<Role> findByName(String roleName);
}
