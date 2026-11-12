package com.localflow.order.api.dto;

import com.localflow.order.domain.OrderStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ChangeOrderStatusRequest(
        @NotNull OrderStatus status,
        @Size(max = 500) String notes
) {
}
