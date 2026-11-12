package com.localflow.order.api.dto;

import com.localflow.order.domain.CustomerOrder;
import com.localflow.order.domain.OrderStatus;
import com.localflow.order.domain.PaymentStatus;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

public record OrderResponse(
        Long id,
        String orderNumber,
        Long customerId,
        String customerName,
        String customerPhone,
        String customerEmail,
        OrderStatus status,
        BigDecimal subtotal,
        BigDecimal deliveryCharge,
        BigDecimal discount,
        BigDecimal tax,
        BigDecimal totalAmount,
        String currency,
        PaymentStatus paymentStatus,
        String source,
        String notes,
        OrderDeliveryAddressResponse deliveryAddress,
        List<OrderItemResponse> items,
        List<OrderStatusHistoryResponse> statusHistory,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
    public static OrderResponse from(CustomerOrder order) {
        return new OrderResponse(
                order.getId(), order.getOrderNumber(), order.getCustomer().getId(), order.getCustomer().getName(),
                order.getCustomer().getPhone(), order.getCustomer().getEmail(),
                order.getStatus(), order.getSubtotal(), order.getDeliveryCharge(), order.getDiscount(), order.getTax(),
                order.getTotalAmount(), order.getCurrency(), order.getPaymentStatus(), order.getSource(), order.getNotes(),
                OrderDeliveryAddressResponse.from(order),
                order.getItems().stream().map(OrderItemResponse::from).toList(),
                order.getStatusHistory().stream().map(OrderStatusHistoryResponse::from).toList(),
                order.getCreatedAt(), order.getUpdatedAt()
        );
    }

    public OrderResponse(
            Long id, String orderNumber, Long customerId, String customerName, OrderStatus status,
            BigDecimal subtotal, BigDecimal deliveryCharge, BigDecimal discount, BigDecimal tax,
            BigDecimal totalAmount, String currency, PaymentStatus paymentStatus, String source, String notes,
            List<OrderItemResponse> items, List<OrderStatusHistoryResponse> statusHistory,
            OffsetDateTime createdAt, OffsetDateTime updatedAt
    ) {
        this(id, orderNumber, customerId, customerName, null, null, status, subtotal, deliveryCharge, discount, tax,
                totalAmount, currency, paymentStatus, source, notes, null, items, statusHistory, createdAt, updatedAt);
    }
}
