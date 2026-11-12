package com.localflow.catalogue.api.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.AssertTrue;

import java.math.BigDecimal;

public record ProductRequest(
        @NotBlank @Size(max = 160) String name,
        @Size(max = 1000) String description,
        @NotBlank @Size(max = 80) String sku,
        @NotBlank @Size(max = 32) String unit,
        @NotNull @DecimalMin("0.00") @Digits(integer = 10, fraction = 2) BigDecimal price,
        @NotNull @Positive Long categoryId,
        @NotNull @DecimalMin("0.001") @Digits(integer = 9, fraction = 3) BigDecimal minQuantity,
        @NotNull @DecimalMin("0.001") @Digits(integer = 9, fraction = 3) BigDecimal quantityStep,
        @DecimalMin("0.00") @DecimalMax("100.00") @Digits(integer = 3, fraction = 2) BigDecimal discountPercent,
        @Size(max = 24) String taxType,
        @DecimalMin("0.01") @DecimalMax("100.00") @Digits(integer = 3, fraction = 2) BigDecimal taxPercent
) {
    public ProductRequest(String name, String description, String sku, String unit, BigDecimal price,
                          Long categoryId, BigDecimal minQuantity, BigDecimal quantityStep) {
        this(name, description, sku, unit, price, categoryId, minQuantity, quantityStep, null, null, null);
    }

    @AssertTrue(message = "Choose a supported tax type and enter a tax percentage greater than 0, or leave both empty.")
    public boolean isTaxConfigurationValid() {
        boolean noTax = (taxType == null || taxType.isBlank()) && taxPercent == null;
        boolean configuredTax = taxType != null && !taxType.isBlank()
                && taxPercent != null && taxPercent.compareTo(BigDecimal.ZERO) > 0
                && (taxType.equals("GST") || taxType.equals("VAT") || taxType.equals("OTHER"));
        return noTax || configuredTax;
    }
}
