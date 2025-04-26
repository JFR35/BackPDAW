package com.myobservation.auth.security;

/**
 * Representa la respuesta de autenticación del usuario.
 * Al ser un record, proporciona por defecto:
 * - Un constructor público para inicializar params.
 * - Getters implícitos, sin setter ya que un record es inmutable.
 * - Implementa equals(), hashCode(), toString()
 * @param token Token de auth generado.
 * @param role Rol del usuario autenticado.
 */
public record AuthResponse(String token, String role) {}
