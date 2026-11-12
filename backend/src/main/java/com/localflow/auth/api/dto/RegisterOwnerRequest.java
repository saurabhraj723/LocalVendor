package com.localflow.auth.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterOwnerRequest(
        @NotBlank @Size(max = 160) String businessName,
        @NotBlank @Size(max = 80) String businessType,
        @Size(max = 15) @Pattern(regexp = "(?i)^[A-Z0-9]{15}$") String gstin,
        @Size(max = 32) String businessPhone,
        @Email @Size(max = 254) String businessEmail,
        String businessAddress,
        @Size(min = 3, max = 3) String currency,
        @Size(max = 80) String timezone,
        @NotBlank @Size(max = 160) String ownerName,
        @Email @NotBlank @Size(max = 254) String ownerEmail,
        @Size(max = 32) String ownerPhone,
        @NotBlank @Size(min = 8, max = 100) String password
) {
}

