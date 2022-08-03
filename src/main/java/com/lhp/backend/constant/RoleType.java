package com.lhp.backend.constant;

public enum RoleType {
    ADMIN,
    USER;

    public String getRoleId() {
        return name();
    }

}
