package com.localflow.storefront.api.dto;

import com.localflow.order.api.dto.OrderItemRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.List;

public record CreateStorefrontOrderRequest(
        @NotBlank @Size(max = 80) String requestId,
        @NotBlank @Size(max = 240) String addressLine1,
        @Size(max = 240) String addressLine2,
        @Size(max = 160) String locality,
        @NotBlank @Size(max = 120) String city,
        @Size(max = 120) String state,
        @Size(max = 24) String postalCode,
        @Pattern(regexp = "[A-Za-z]{2}") String countryCode,
        @Size(max = 500) String deliveryInstructions,
        @Size(max = 1000) String notes,
        @NotEmpty @Size(max = 100) List<@Valid OrderItemRequest> items
) { }
