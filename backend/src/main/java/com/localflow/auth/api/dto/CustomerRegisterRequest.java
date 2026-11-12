package com.localflow.auth.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CustomerRegisterRequest(
        @NotBlank @Size(max = 48) String storefrontSlug,
        @NotBlank @Size(max = 32) String registrationCode,
        @NotBlank @Size(max = 160) String name,
        @NotBlank @Pattern(regexp = "[+0-9() .-]{8,32}") String phone,
        @NotBlank @Email @Size(max = 254) String email,
        @NotBlank @Size(min = 8, max = 72) String password
) { }
