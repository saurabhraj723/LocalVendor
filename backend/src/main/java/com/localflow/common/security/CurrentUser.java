package com.localflow.common.security;

import com.localflow.common.domain.UserRole;

public record CurrentUser(
        Long userId,
        Long businessId,
        UserRole role,
        String email
) {
}

