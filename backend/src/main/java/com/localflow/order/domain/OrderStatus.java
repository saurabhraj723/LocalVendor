package com.localflow.order.domain;

public enum OrderStatus {
    NEW,
    CONFIRMED,
    REJECTED,
    PACKING,
    READY,
    OUT_FOR_DELIVERY,
    PICKUP_READY,
    DELIVERED,
    COMPLETED,
    CANCELLED
}
