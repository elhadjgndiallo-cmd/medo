package com.medo.api.tenant;

import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class TenantIdentifierResolver implements CurrentTenantIdentifierResolver<String> {

    @Value("${medo.tenant.default-schema:public}")
    private String defaultSchema;

    @Override
    public String resolveCurrentTenantIdentifier() {
        String t = TenantContext.getCurrentTenant();
        return t != null ? t : defaultSchema;
    }

    @Override
    public boolean validateExistingCurrentSessions() { return false; }
}
