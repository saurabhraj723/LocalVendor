package com.localflow.customer.api.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

public record CustomerRequest(
        @NotBlank @Size(max = 160) String name,
        @NotBlank @Size(max = 32) String phone,
        @NotBlank @Email @Size(max = 254) String email,
        @Size(max = 1000) String notes,
        @NotEmpty List<@Valid CustomerAddressRequest> addresses
) {
}
