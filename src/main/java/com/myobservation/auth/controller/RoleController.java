package com.myobservation.auth.controller;

import com.myobservation.auth.dto.RoleAssignment;
import com.myobservation.auth.entity.Role;
import com.myobservation.auth.service.RoleService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/admin/roles") // Ejemplo de ruta protegida para administradores
@PreAuthorize("hasRole('ADMIN')") // Solo los administradores pueden acceder
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping
    public ResponseEntity<List<Role>> getAllRoles() {
        return ResponseEntity.ok(roleService.getAllRoles());
    }

    @PostMapping
    public ResponseEntity<Role> createRole(@RequestBody Role role) {
        return ResponseEntity.ok(roleService.createRole(role));
    }

    @PostMapping("/assign")
    public ResponseEntity<String> assignRoles(@RequestBody RoleAssignment roleAssignment) {
        roleService.assignRolesToUser(roleAssignment.getUserId(), roleAssignment.getRoleNames());
        return ResponseEntity.ok("Roles assigned successfully");
    }
}