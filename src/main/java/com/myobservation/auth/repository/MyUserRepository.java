package com.myobservation.auth.repository;

import com.myobservation.auth.entity.MyUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MyUserRepository extends JpaRepository<MyUser,Long> {
    @Query("SELECT u FROM MyUser u JOIN FETCH u.roles WHERE u.email = :email")
    Optional<MyUser> findByEmailWithRoles(@Param("email") String email);

    Optional<MyUser> findByEmail(String email); // Mantén esta si la usas en otros lugares
}
