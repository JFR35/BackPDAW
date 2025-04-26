package com.myobservation.auth.controller;

import com.myobservation.auth.security.AuthRequest;
import com.myobservation.auth.security.AuthResponse;
import com.myobservation.auth.security.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Controlador de autenticación que gestiona el inicio de sesión de usuarios.
 * Este controlador proporciona un endpoint para la autenticación mediante JWT.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody AuthRequest request){
        AuthResponse authResponse = authService.authenticate(request);
        Map<String, Object> response = new HashMap<>();
        response.put("token", authResponse.token());
        response.put("role", authResponse.role());
        response.put("message", "Login exitoso");
        return ResponseEntity.ok(response);
    }
}
