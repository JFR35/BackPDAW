package com.myobservation.auth.controller;

import com.myobservation.auth.config.AuthRequest;
import com.myobservation.auth.config.AuthResponse;
import com.myobservation.auth.config.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

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
        response.put("message", "Login exitoso");
        return ResponseEntity.ok(response);
    }
}
