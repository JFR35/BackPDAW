package com.myobservation.user.repository;

import com.myobservation.user.entity.MyUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.expression.spel.ast.OpAnd;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MyUserRepository extends JpaRepository<MyUser,Long> {
    Optional<MyUser> findByEmail(String email);
}
