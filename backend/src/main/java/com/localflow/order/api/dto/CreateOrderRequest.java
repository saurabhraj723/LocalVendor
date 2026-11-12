package com.localflow.order.api.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.util.List;

public record CreateOrderRequest(
        @NotNull @Positive Long customerId,
        @NotEmpty @Size(max = 100) List<@Valid OrderItemRequest> items,
        @Size(max = 1000) String notes
) {
}
