package com.localflow.order.service;

import com.localflow.order.api.dto.OrderResponse;

public record WhatsAppOrderIntakeResult(OrderResponse order, boolean created) {
}
