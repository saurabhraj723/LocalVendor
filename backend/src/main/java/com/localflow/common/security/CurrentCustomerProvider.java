package com.localflow.common.security;

import com.localflow.common.exception.UnauthorizedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component
public class CurrentCustomerProvider {
    public CustomerIdentity get() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof Jwt jwt)
                || !"CUSTOMER".equals(jwt.getClaimAsString("role"))) {
            throw new UnauthorizedException("CUSTOMER_AUTH_REQUIRED", "Please sign in to continue.");
        }
        return new CustomerIdentity(jwt.getClaim("customerId"), jwt.getClaim("businessId"),
                jwt.getClaimAsString("email"), jwt.getClaimAsString("name"), jwt.getClaimAsString("phone"));
    }

    public record CustomerIdentity(Long customerId, Long businessId, String email, String name, String phone) { }
}
