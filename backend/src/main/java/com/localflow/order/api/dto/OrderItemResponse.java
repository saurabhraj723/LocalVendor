package com.localflow.order.api.dto;

import com.localflow.order.domain.OrderItem;

import java.math.BigDecimal;

public record OrderItemResponse(
        Long id,
        Long productId,
        String productName,
        String unit,
        BigDecimal unitPrice,
        BigDecimal quantity,
        BigDecimal lineTotal,
        BigDecimal discountPercent,
        BigDecimal discountAmount,
        String taxType,
        BigDecimal taxPercent,
        BigDecimal taxAmount
) {
    public static OrderItemResponse from(OrderItem item) {
        return new OrderItemResponse(
                item.getId(), item.getProduct().getId(), item.getProductName(), item.getUnit(),
                item.getUnitPrice(), item.getQuantity(), item.getLineTotal(), item.getDiscountPercent(),
                item.getDiscountAmount(), item.getTaxType(), item.getTaxPercent(), item.getTaxAmount()
        );
    }
}
