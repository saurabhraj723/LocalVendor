package com.localflow.common.security;

import com.localflow.common.domain.UserRole;
import com.localflow.common.exception.UnauthorizedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component
public class CurrentUserProvider {

    public CurrentUser getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof Jwt jwt)) {
            throw new UnauthorizedException("UNAUTHORIZED", "Authentication is required.");
        }

        return new CurrentUser(
                jwt.getClaim("userId"),
                jwt.getClaim("businessId"),
                UserRole.valueOf(jwt.getClaimAsString("role")),
                jwt.getClaimAsString("email")
        );
    }

    public Long currentBusinessId() {
        return getCurrentUser().businessId();
    }
}

