package com.localflow.order.service;

import com.localflow.order.api.dto.OrderResponse;

public record StorefrontOrderResult(OrderResponse order, boolean created) { }
