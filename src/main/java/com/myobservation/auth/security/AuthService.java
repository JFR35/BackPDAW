package com.myobservation.auth.security;

import com.myobservation.auth.entity.MyUser;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

/**
 * Sevicio de autenticación que maneja el inicio de sesión de usuarios.
 * Se utiliza {@link AuthenticationManager} para autenticar credenciales.
 * Se utiliza {@link JwtService} para generar tokens JWT.
 */
@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    /**
     * Constructor
     *
     * @param authenticationManager Maneja la autenticación de usuarios.
     * @param jwtService            Servicio para la generación de tokens JWT.
     */
    public AuthService(AuthenticationManager authenticationManager, JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    /**
     * Autentica un usuario con su correo y contraseña.
     * Genera un token JWT para el user autenticado.
     *
     * @param request Este objeto contiene el correo el correo+contraseña del usuario.
     * @return {@link AuthResponse} con el token generado y el rol de usuario.
     */
    public AuthResponse authenticate(AuthRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                )
        );

        // Obtener los detalles del usuario autenticado
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        MyUser user = (MyUser) userDetails;  // Casteo a MyUser
        String token = jwtService.generateToken(user);

        // Obtener el primer rol del usuario
        String role = user.getAuthorities().stream()
                .findFirst()  // Solo se obtiene el primer rol
                .map(grantedAuthority -> grantedAuthority.getAuthority())
                .orElse("ROLE_USER");  // Default si no tiene rol

        return new AuthResponse(token, role);
    }
}
