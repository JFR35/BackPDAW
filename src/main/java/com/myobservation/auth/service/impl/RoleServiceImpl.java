package com.myobservation.auth.service.impl;

import com.myobservation.auth.entity.MyUser;
import com.myobservation.auth.entity.Role;
import com.myobservation.auth.repository.MyUserRepository;
import com.myobservation.auth.repository.RoleRepository;
import com.myobservation.auth.service.RoleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

// Implementación para el servicio de gestión de roles.
@Service
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final MyUserRepository myUserRepository;

    /**
     * Constructor de {@code RoleServiceImpl}
     * @param roleRepository Repostitorio para la gestión de roles.
     * @param myUserRepository Respositorio para hestión de usuarios.
     */
    public RoleServiceImpl(RoleRepository roleRepository, MyUserRepository myUserRepository) {
        this.roleRepository = roleRepository;
        this.myUserRepository = myUserRepository;
    }

    // Obtener todos los roles en el sistema.
    @Override
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    // Crear un nuevo rol.
    @Override
    public Role createRole(Role role) {
        // Validar que el rol no exista ya (por nombre, que es único)
        if (roleRepository.findByName(role.getName()).isPresent()) {
            throw new IllegalArgumentException("El rol con nombre '" + role.getName() + "' ya existe");
        }
        return roleRepository.save(role);
    }

    // Asigna una lista de roles a un usuario.
    @Override
    @Transactional // Asegura que la operación sea atómica
    public void assignRolesToUser(Long userId, List<String> roleNames) {
        // Buscar al usuario por ID
        MyUser user = myUserRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario con ID " + userId + " no encontrado"));

        // Obtener los roles existentes por sus nombres
        Set<Role> rolesToAssign = roleNames.stream()
                .map(roleName -> roleRepository.findByName(roleName)
                        .orElseThrow(() -> new IllegalArgumentException("Rol con nombre '" + roleName + "' no encontrado")))
                .collect(Collectors.toSet());

        // Asignar los roles al usuario
        user.setRoles(rolesToAssign);
    }
}