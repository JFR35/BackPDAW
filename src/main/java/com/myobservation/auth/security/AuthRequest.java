package com.myobservation.auth.security;

/**
 * Clase inmutable para transportar datos.
 * Representa una solicitud de autenticación del usuario.
 * @param email email del usuario
 * @param password contraseña del usuario
 * Además de estos parámetros crea un constructor public
 * Métodos getters
 * Implementa equals() hashCode() y toString()
 */
public record AuthRequest(String email, String password) {}

