package com.localflow.order.api.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.util.List;

public record CreateWhatsAppOrderRequest(
        @NotBlank @Size(max = 160) String messageId,
        @NotNull @Positive Long customerId,
        @NotEmpty @Size(max = 100) List<@Valid OrderItemRequest> items,
        @Size(max = 1000) String notes,
        @Positive Long addressId
) {
    public CreateWhatsAppOrderRequest(String messageId, Long customerId, List<OrderItemRequest> items, String notes) {
        this(messageId, customerId, items, notes, null);
    }
}
