package com.myobservation.auth.service;

import com.myobservation.auth.entity.MyUser;
import com.myobservation.auth.entity.Role;
import com.myobservation.auth.repository.MyUserRepository;
import com.myobservation.auth.repository.RoleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final MyUserRepository myUserRepository;

    // Inyección de dependencias mediante constructor
    public RoleServiceImpl(RoleRepository roleRepository, MyUserRepository myUserRepository) {
        this.roleRepository = roleRepository;
        this.myUserRepository = myUserRepository;
    }

    @Override
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    @Override
    public Role createRole(Role role) {
        // Validar que el rol no exista ya (por nombre, que es único)
        if (roleRepository.findByName(role.getName()).isPresent()) {
            throw new IllegalArgumentException("El rol con nombre '" + role.getName() + "' ya existe");
        }
        return roleRepository.save(role);
    }

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