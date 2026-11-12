package com.localflow.storefront.api.dto;

import com.localflow.catalogue.api.dto.ProductResponse;

import java.util.List;

public record StorefrontResponse(
        String name,
        String businessType,
        String phone,
        String address,
        String currency,
        List<ProductResponse> products
) { }
