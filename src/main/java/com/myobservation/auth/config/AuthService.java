package com.myobservation.auth.config;

import com.myobservation.auth.entity.MyUser;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthService(AuthenticationManager authenticationManager, JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    public AuthResponse authenticate(AuthRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                )
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        MyUser user = (MyUser) userDetails;  // Casteo a MyUser
        String token = jwtService.generateToken(user);

        // Obtener el primer rol del usuario (suponiendo que solo tiene uno)
        String role = user.getAuthorities().stream()
                .findFirst()  // Solo obtenemos el primer rol
                .map(grantedAuthority -> grantedAuthority.getAuthority())
                .orElse("ROLE_USER");  // Default si no tiene rol

        return new AuthResponse(token, role);
    }
}
