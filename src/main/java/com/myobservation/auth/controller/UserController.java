package com.myobservation.auth.controller;

import com.myobservation.auth.dto.UserRequest;
import com.myobservation.auth.dto.UserResponse;
import com.myobservation.auth.repository.RoleRepository;
import com.myobservation.auth.service.UserService;
import com.myobservation.auth.service.exception.ResourceNotFoundException;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService,
                          RoleRepository roleRepository) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<UserResponse>> retrieveAllUsers() {
        List<UserResponse> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        Optional<UserResponse> user = userService.getUserById(id);
        return user.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    @Transactional
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserRequest userRequest) {
        UserResponse savedUser = userService.createUser(userRequest);
        return ResponseEntity.ok(savedUser);
    }

    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<Optional<UserResponse>> updateUserById(@PathVariable Long id, @Valid @RequestBody UserRequest userRequest) {
        Optional<UserResponse> updatedUser = userService.updateUserById(id, userRequest)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUserById(@PathVariable Long id) {
        boolean deleted = userService.deleteUserById(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }


    @PostMapping("/practitioners")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> createPractitioner(@Valid @RequestBody UserRequest userRequest) {
        UserResponse createdUser = userService.createPractitioner(userRequest);
        return ResponseEntity.ok(createdUser);
    }
}