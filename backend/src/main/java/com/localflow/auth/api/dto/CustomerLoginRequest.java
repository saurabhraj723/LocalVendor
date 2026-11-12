package com.localflow.auth.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CustomerLoginRequest(
        @NotBlank @Size(max = 48) String storefrontSlug,
        @NotBlank @Email @Size(max = 254) String email,
        @NotBlank String password
) { }
