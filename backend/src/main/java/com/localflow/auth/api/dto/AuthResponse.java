package com.localflow.auth.api.dto;

import com.localflow.common.domain.UserRole;

public record AuthResponse(
        String accessToken,
        String tokenType,
        long expiresInSeconds,
        UserSummary user,
        BusinessSummary business
) {

    public record UserSummary(
            Long id,
            String name,
            String email,
            String phone,
            UserRole role
    ) {
    }

    public record BusinessSummary(
            Long id,
            String name,
            String businessType,
            String currency,
            String timezone,
            String storefrontSlug,
            String customerRegistrationCode,
            String gstin,
            String phone,
            String email,
            String address
    ) {
    }
}

