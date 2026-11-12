package com.localflow.customer.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CustomerAddressRequest(
        @NotBlank @Size(max = 60) String label,
        @Size(max = 160) String recipientName,
        @Size(max = 32) String phone,
        @NotBlank @Size(max = 240) String addressLine1,
        @Size(max = 240) String addressLine2,
        @Size(max = 160) String locality,
        @NotBlank @Size(max = 120) String city,
        @Size(max = 120) String state,
        @Size(max = 24) String postalCode,
        @Pattern(regexp = "[A-Za-z]{2}") String countryCode,
        @Size(max = 500) String deliveryInstructions,
        boolean defaultAddress
) {
}
