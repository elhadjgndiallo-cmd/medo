package com.medo.api.security;

import java.security.Principal;

public class MedoUserPrincipal implements Principal {

    private String userId;
    private String tenantId;
    private String typeUser;

    public MedoUserPrincipal() {}

    public MedoUserPrincipal(String userId, String tenantId, String typeUser) {
        this.userId = userId;
        this.tenantId = tenantId;
        this.typeUser = typeUser;
    }

    @Override public String getName() { return userId; }
    public String getUserId()         { return userId; }
    public String getTenantId()       { return tenantId; }
    public String getTypeUser()       { return typeUser; }
    public void setUserId(String v)   { this.userId = v; }
    public void setTenantId(String v) { this.tenantId = v; }
    public void setTypeUser(String v) { this.typeUser = v; }

    public boolean isSuperAdmin()     { return "SUPER_ADMIN".equals(typeUser); }
    public boolean isAdminPharmacie() { return "ADMIN_PHARMACIE".equals(typeUser); }
    public boolean isEmploye()        { return "EMPLOYE".equals(typeUser); }
    public boolean isClientMobile()   { return "CLIENT_MOBILE".equals(typeUser); }
}
