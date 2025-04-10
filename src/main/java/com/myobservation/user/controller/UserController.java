package com.myobservation.user.controller;


import com.myobservation.user.entity.MyUser;
import com.myobservation.user.service.MyUserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/users") // Ojo en la ruta del front
public class UserController {

    private final MyUserService myUserService;

    public UserController(MyUserService myUserService) {
        this.myUserService = myUserService;

    }

    @GetMapping
    public ResponseEntity <List<MyUser>> retrieveAllUsers() {
        List<MyUser> users = myUserService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    // Obtener un usuario por ID
    @GetMapping("/{id}")
    public ResponseEntity<MyUser> getUserById(@PathVariable Long id) {
        Optional user = myUserService.getUserById(id);
        return (ResponseEntity<MyUser>) user.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<MyUser> createUser(@RequestBody MyUser user) {
        MyUser savedUser = myUserService.createUser(user);
        return ResponseEntity.ok(savedUser);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUserById(@PathVariable Long id) {
        boolean deleted = myUserService.deleteUserById(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }


}
