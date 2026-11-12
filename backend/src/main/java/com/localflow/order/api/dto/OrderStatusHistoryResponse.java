package com.localflow.order.api.dto;

import com.localflow.order.domain.OrderStatus;
import com.localflow.order.domain.OrderStatusHistory;

import java.time.OffsetDateTime;

public record OrderStatusHistoryResponse(
        OrderStatus oldStatus,
        OrderStatus newStatus,
        Long changedByUserId,
        OffsetDateTime changedAt,
        String notes
) {
    public static OrderStatusHistoryResponse from(OrderStatusHistory history) {
        return new OrderStatusHistoryResponse(
                history.getOldStatus(), history.getNewStatus(), history.getChangedBy() == null ? null : history.getChangedBy().getId(),
                history.getChangedAt(), history.getNotes()
        );
    }
}
