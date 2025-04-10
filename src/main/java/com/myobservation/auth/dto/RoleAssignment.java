package com.myobservation.auth.dto;

import java.util.List;

public class RoleAssignment {
    private Long userId;
    private List<String> roleNames;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public List<String> getRoleNames() {
        return roleNames;
    }

    public void setRoleNames(List<String> roleNames) {
        this.roleNames = roleNames;
    }
}
