package com.medo.api.security;

import com.medo.api.tenant.TenantContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain chain)
            throws ServletException, IOException {
        String token = extractToken(request);
        if (StringUtils.hasText(token) && jwtTokenProvider.validateToken(token)) {
            try {
                authenticate(token, response);
            } catch (TenantMismatchException e) {
                log.warn("Cross-tenant : {}", e.getMessage());
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Accès refusé");
                return;
            }
        }
        chain.doFilter(request, response);
    }

    private void authenticate(String token, HttpServletResponse response) {
        String userId    = jwtTokenProvider.getUserIdFromToken(token);
        String tenantId  = jwtTokenProvider.getTenantIdFromToken(token);
        String typeUser  = jwtTokenProvider.getTypeUserFromToken(token);
        List<String> roles = jwtTokenProvider.getRolesFromToken(token);

        String current = TenantContext.getCurrentTenant();
        if (!isValidAccess(tenantId, current, typeUser)) {
            throw new TenantMismatchException("Token tenant '" + tenantId + "' != contexte '" + current + "'");
        }

        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        if (roles != null) {
            authorities = roles.stream()
                .map(r -> new SimpleGrantedAuthority("ROLE_" + r))
                .collect(Collectors.toList());
        }
        authorities.add(new SimpleGrantedAuthority("TYPE_" + typeUser));

        MedoUserPrincipal principal = new MedoUserPrincipal(userId, tenantId, typeUser);
        UsernamePasswordAuthenticationToken auth =
            new UsernamePasswordAuthenticationToken(principal, null, authorities);
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    private boolean isValidAccess(String tokenTenant, String currentTenant, String typeUser) {
        if ("SUPER_ADMIN".equals(typeUser)) return true;
        if (currentTenant == null || "public".equals(currentTenant)) return true;
        return tokenTenant != null && tokenTenant.equals(currentTenant);
    }

    private String extractToken(HttpServletRequest request) {
        String h = request.getHeader("Authorization");
        if (StringUtils.hasText(h) && h.startsWith("Bearer ")) return h.substring(7);
        return null;
    }

    public static class TenantMismatchException extends RuntimeException {
        public TenantMismatchException(String m) { super(m); }
    }
}
