package com.myobservation.auth.security;

import com.myobservation.auth.entity.MyUser;
import com.myobservation.auth.repository.MyUserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Servicio de gestión de usuarios para la autenticación en Spring Security.
 * Implementa {@link UserDetailsService} para cargar los detalles del usuario en
 * el proceso de autenticación.
 */
@Service
public class MyUserDetailsService implements UserDetailsService {
    private final MyUserRepository myUserRepository;

    /**
     * Constructo de {@code MyUserDetailService}.
     *
     * @param myUserRepository Respositorio de usuarios utilizados en la consulta de
     *                         cedenciales.
     */
    public MyUserDetailsService(MyUserRepository myUserRepository) {
        this.myUserRepository = myUserRepository;
    }

    /*
     * Carga los detalles de un usuario por su email.
     * Este método se utiliza en la autenticación para recuperar los datos del usuario.
     * @param email Email del usuario.
     * @return Detalles del usuario autenticado.
     * @throws UsernameNotFoundException Si el user no es encontrado.
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        MyUser user = myUserRepository.findByEmailWithRoles(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
        return user;
    }
}