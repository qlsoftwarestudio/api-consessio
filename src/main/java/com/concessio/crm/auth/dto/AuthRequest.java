package com.concessio.crm.auth.dto;

public class AuthRequest {
    private String tenantCode;
    private String email;
    private String password;

    // getters y setters

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public String getTenantCode() {
        return tenantCode;
    }
}
