package com.myobservation.auth.repository;

import com.myobservation.auth.entity.MyUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio de usuarios para la gestión de autenticación y roles.
 * Extiene de {@link JpaRepository} para proporcionar los métodos de
 * acceso a datos.
 */
@Repository
public interface MyUserRepository extends JpaRepository<MyUser,Long> {
    /**
     * Busca un usuario por su email e incluye los roles
     * @param email Email a buscar.
     * @return {@link Optional} que contiene el user si existe.
     */
    @Query("SELECT u FROM MyUser u JOIN FETCH u.roles WHERE u.email = :email")
    Optional<MyUser> findByEmailWithRoles(@Param("email") String email);

    /**
     * Buscar usuario por su email
     * @param email El email del user.
     * @return Retorna al user si lo encuentra.
     */
    Optional<MyUser> findByEmail(String email);
}
