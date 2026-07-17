package com.medo.api.tenant;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

@Component
@Order(1)
public class TenantFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(TenantFilter.class);

    @Value("${medo.tenant.header-name:X-Tenant}")
    private String headerName;

    @Value("${medo.tenant.default-schema:public}")
    private String defaultSchema;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain chain)
            throws ServletException, IOException {
        try {
            TenantContext.setCurrentTenant(resolve(request));
            chain.doFilter(request, response);
        } finally {
            TenantContext.clear();
        }
    }

    private String resolve(HttpServletRequest request) {
        String h = request.getHeader(headerName);
        if (StringUtils.hasText(h)) {
            String s = h.trim().toLowerCase().replaceAll("[^a-z0-9-]", "");
            return s + "_schema";
        }
        return defaultSchema;
    }
}
