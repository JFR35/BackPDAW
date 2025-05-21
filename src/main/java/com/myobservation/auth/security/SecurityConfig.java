package com.myobservation.auth.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Config de seguridad de Spring Security.
 * Define las políticas de seguridad de la app, incluye autenticación +
 * autorización, manejo de JWT y config CORS.
 *
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class); // Logger

    private final JwtAuthFilter jwtAuthFilter;
    private final UserDetailsService userDetailsService;

    /**
     *
     * Constructor de {@code SecurityConfig}.
     *
     * @param jwtAuthFilter Filtro de auth JWT.
     * @param userDetailsService servicio para recuperar los detalles de usuario.
     */
    public SecurityConfig(JwtAuthFilter jwtAuthFilter, UserDetailsService userDetailsService) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.userDetailsService = userDetailsService;
    }

    /**
     * Configura la seguridad HTTP de la aplicación:
     * - Define las reglas de autorización.
     * - Desactiva CSFR.
     * - Habilita CORS
     * - Agrega filtro JWT.
     * @param http Configuración de seguridad HTTP.
     * @return Cadena de filtros de seguridad configurada.
     * @throws Exception Si ocurre algún error en la config.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        logger.info("Configuring HTTP security...");

        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // Habilitar CORS
                .authorizeHttpRequests(auth -> auth
                        // Para pruebas desproteger estos paths
                        .requestMatchers("/api/auth/**","/api/patients/**", "/h2-console/**", "/fhir/**","/api/fhir/validate", "/error", "/Patient","/api/v1/blood-pressure/**").permitAll()
                        .requestMatchers("/api/users/**").hasAnyRole("ADMIN", "PRACTITIONER")
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/practitioner/**", "/api/fhir/patients/**", "/api/fhirbase/**").hasRole("PRACTITIONER")
                        .anyRequest().authenticated()
                )
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.disable()));
        logger.info("Security configuration completed.");

        return http.build();
    }

    /**
     * Configura las reglas de CORS para la aplicación.
     *
     * <p>Define los orígenes permitidos, métodos HTTP y encabezados expuestos.</p>
     *
     * @return Fuente de configuración CORS.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:5173")); // Origen de tu frontend
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        configuration.setExposedHeaders(List.of("Authorization")); // Para exponer el header de autenticación

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    /**
     * Configura el codificador de contraseñas usando BCrypt.
     *
     * @return Instancia de {@link BCryptPasswordEncoder}.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configura el proveedor de auth basado en {@code DaoAuthenticationProvider}.
     * Define cómo se recuperan los detalles del usuario y cómo se codifica el password.
     * @return Proveedor de autenticación configurado.
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    /**
     * Obtiene el administrador de auth de la app.
     *
     * @param config Configuración de autenticación de Spring Security.
     * @return Instancia de {@link AuthenticationManager}.
     * @throws Exception Si ocurre error durante la config.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}