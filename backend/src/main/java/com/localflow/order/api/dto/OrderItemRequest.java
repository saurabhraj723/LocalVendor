package com.localflow.order.api.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record OrderItemRequest(
        @NotNull @Positive Long productId,
        @NotNull @DecimalMin(value = "0.001", inclusive = true) @Digits(integer = 9, fraction = 3) BigDecimal quantity
) {
}
