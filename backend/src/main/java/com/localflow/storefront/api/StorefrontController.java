package com.localflow.storefront.api;

import com.localflow.order.api.dto.OrderResponse;
import com.localflow.order.service.StorefrontOrderResult;
import com.localflow.storefront.api.dto.CreateStorefrontOrderRequest;
import com.localflow.storefront.api.dto.StorefrontResponse;
import com.localflow.storefront.service.StorefrontService;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.validation.annotation.Validated;

@RestController
@Validated
@RequestMapping("/api/v1/storefront")
public class StorefrontController {

    private final StorefrontService storefrontService;

    public StorefrontController(StorefrontService storefrontService) {
        this.storefrontService = storefrontService;
    }

    @GetMapping("/{storefrontSlug}")
    public StorefrontResponse getStorefront(@PathVariable @Pattern(regexp = "[a-f0-9]{32}") String storefrontSlug) {
        return storefrontService.getStorefront(storefrontSlug);
    }

    @PostMapping("/{storefrontSlug}/orders")
    public ResponseEntity<OrderResponse> createOrder(
            @PathVariable @Pattern(regexp = "[a-f0-9]{32}") String storefrontSlug,
            @Valid @RequestBody CreateStorefrontOrderRequest request
    ) {
        StorefrontOrderResult result = storefrontService.createOrder(storefrontSlug, request);
        return ResponseEntity.status(result.created() ? HttpStatus.CREATED : HttpStatus.OK).body(result.order());
    }
}
