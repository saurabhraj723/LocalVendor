package com.localflow.order.api;

import com.localflow.order.api.dto.ChangeOrderStatusRequest;
import com.localflow.order.api.dto.CreateOrderRequest;
import com.localflow.order.api.dto.CreateWhatsAppOrderRequest;
import com.localflow.order.api.dto.OrderResponse;
import com.localflow.order.api.dto.PagedResponse;
import com.localflow.order.service.OrderService;
import com.localflow.order.service.WhatsAppOrderIntakeResult;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public PagedResponse<OrderResponse> list(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size
    ) {
        return PagedResponse.from(orderService.list(page, size));
    }

    @GetMapping("/customer/{customerId}")
    public PagedResponse<OrderResponse> customerOrders(
            @PathVariable Long customerId,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size
    ) {
        return PagedResponse.from(orderService.customerOrders(customerId, page, size));
    }

    @GetMapping("/{id}")
    public OrderResponse get(@PathVariable Long id) {
        return orderService.get(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse create(@Valid @RequestBody CreateOrderRequest request) {
        return orderService.create(request);
    }

    @PostMapping("/whatsapp/intake-test")
    public ResponseEntity<OrderResponse> createWhatsAppIntakeTest(
            @Valid @RequestBody CreateWhatsAppOrderRequest request
    ) {
        WhatsAppOrderIntakeResult result = orderService.createFromWhatsAppTest(request);
        HttpStatus status = result.created() ? HttpStatus.CREATED : HttpStatus.OK;
        return ResponseEntity.status(status).body(result.order());
    }

    @PatchMapping("/{id}/status")
    public OrderResponse changeStatus(
            @PathVariable Long id,
            @Valid @RequestBody ChangeOrderStatusRequest request
    ) {
        return orderService.changeStatus(id, request);
    }
}
