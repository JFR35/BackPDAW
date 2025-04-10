package com.myobservation.auth.service;

import com.myobservation.auth.entity.Role;

import java.util.List;

public interface RoleService {
    List<Role> getAllRoles();
    Role createRole(Role role);
    void assignRolesToUser(Long userId, List<String> roleNames);
}
