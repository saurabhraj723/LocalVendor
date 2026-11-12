package com.localflow.dashboard.api.dto;

import com.localflow.order.domain.CustomerOrder;
import com.localflow.order.domain.OrderStatus;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record RecentOrderResponse(
        Long id,
        String orderNumber,
        String customerName,
        String source,
        OrderStatus status,
        BigDecimal totalAmount,
        String currency,
        OffsetDateTime createdAt
) {
    public static RecentOrderResponse from(CustomerOrder order) {
        return new RecentOrderResponse(
                order.getId(), order.getOrderNumber(), order.getCustomer().getName(), order.getSource(), order.getStatus(),
                order.getTotalAmount(), order.getCurrency(), order.getCreatedAt()
        );
    }
}
