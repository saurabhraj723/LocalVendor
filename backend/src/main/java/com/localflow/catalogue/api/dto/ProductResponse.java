package com.localflow.catalogue.api.dto;

import com.localflow.catalogue.domain.Product;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record ProductResponse(
        Long id,
        String name,
        String description,
        String sku,
        String unit,
        BigDecimal price,
        BigDecimal discountPercent,
        BigDecimal discountedPrice,
        String taxType,
        BigDecimal taxPercent,
        BigDecimal minQuantity,
        BigDecimal quantityStep,
        Long categoryId,
        String categoryName,
        boolean available,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
    public static ProductResponse from(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getSku(),
                product.getUnit(),
                product.getPrice(),
                product.getDiscountPercent(),
                product.getPrice().multiply(new BigDecimal("100").subtract(product.getDiscountPercent()))
                        .divide(new BigDecimal("100"), 2, java.math.RoundingMode.HALF_UP),
                product.getTaxType(),
                product.getTaxPercent(),
                product.getMinQuantity(),
                product.getQuantityStep(),
                product.getCategory() == null ? null : product.getCategory().getId(),
                product.getCategory() == null ? null : product.getCategory().getName(),
                product.isAvailable(),
                product.getCreatedAt(),
                product.getUpdatedAt()
        );
    }
}
