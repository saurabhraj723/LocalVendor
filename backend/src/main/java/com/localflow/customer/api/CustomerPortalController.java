package com.localflow.customer.api;

import com.localflow.auth.api.dto.CustomerAuthResponse;
import com.localflow.common.security.CurrentCustomerProvider;
import com.localflow.order.api.dto.OrderResponse;
import com.localflow.order.api.dto.PagedResponse;
import com.localflow.order.service.OrderService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/v1/customer-portal")
public class CustomerPortalController {
    private final CurrentCustomerProvider currentCustomerProvider;
    private final OrderService orderService;

    public CustomerPortalController(CurrentCustomerProvider currentCustomerProvider, OrderService orderService) {
        this.currentCustomerProvider = currentCustomerProvider;
        this.orderService = orderService;
    }

    @GetMapping("/me")
    public CustomerAuthResponse.CustomerSummary me() {
        var customer = currentCustomerProvider.get();
        return new CustomerAuthResponse.CustomerSummary(customer.customerId(), customer.name(), customer.email(), customer.phone());
    }

    @GetMapping("/orders")
    public PagedResponse<OrderResponse> orders(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size
    ) {
        var customer = currentCustomerProvider.get();
        return PagedResponse.from(orderService.portalOrders(customer.customerId(), customer.businessId(), page, size));
    }

    @GetMapping("/orders/{orderId}")
    public OrderResponse order(@PathVariable Long orderId) {
        var customer = currentCustomerProvider.get();
        return orderService.portalOrder(orderId, customer.customerId(), customer.businessId());
    }
}
