package com.localflow.auth.api.dto;

public record CustomerAuthResponse(
        String accessToken,
        String tokenType,
        long expiresInSeconds,
        CustomerSummary customer,
        StoreSummary store
) {
    public record CustomerSummary(Long id, String name, String email, String phone) { }
    public record StoreSummary(String storefrontSlug, String name, String currency) { }
}
