package com.localflow.storefront.service;

import com.localflow.business.domain.Business;
import com.localflow.business.repository.BusinessRepository;
import com.localflow.catalogue.api.dto.ProductResponse;
import com.localflow.catalogue.repository.ProductRepository;
import com.localflow.common.domain.BusinessStatus;
import com.localflow.common.exception.NotFoundException;
import com.localflow.common.exception.UnauthorizedException;
import com.localflow.common.security.CurrentCustomerProvider;
import com.localflow.order.service.OrderService;
import com.localflow.order.service.StorefrontOrderResult;
import com.localflow.storefront.api.dto.CreateStorefrontOrderRequest;
import com.localflow.storefront.api.dto.StorefrontResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StorefrontService {

    private final BusinessRepository businessRepository;
    private final ProductRepository productRepository;
    private final OrderService orderService;
    private final CurrentCustomerProvider currentCustomerProvider;

    public StorefrontService(
            BusinessRepository businessRepository,
            ProductRepository productRepository,
            OrderService orderService,
            CurrentCustomerProvider currentCustomerProvider
    ) {
        this.businessRepository = businessRepository;
        this.productRepository = productRepository;
        this.orderService = orderService;
        this.currentCustomerProvider = currentCustomerProvider;
    }

    @Transactional(readOnly = true)
    public StorefrontResponse getStorefront(String storefrontSlug) {
        Business business = activeBusiness(storefrontSlug);
        var products = productRepository.findAllByBusiness_IdAndActiveTrueOrderByNameAsc(business.getId()).stream()
                .filter(product -> product.isAvailable())
                .map(ProductResponse::from)
                .toList();
        return new StorefrontResponse(business.getName(), business.getBusinessType(),
                business.getPhone(), business.getAddress(), business.getCurrency(), products);
    }

    @Transactional
    public StorefrontOrderResult createOrder(String storefrontSlug, CreateStorefrontOrderRequest request) {
        Business business = activeBusiness(storefrontSlug);
        var customer = currentCustomerProvider.get();
        if (!business.getId().equals(customer.businessId())) {
            throw new UnauthorizedException("STORE_ACCOUNT_MISMATCH", "Sign in to the customer account for this store.");
        }
        return orderService.createFromStorefront(business.getId(), customer.customerId(), request);
    }

    private Business activeBusiness(String storefrontSlug) {
        Business business = businessRepository.findByStorefrontSlug(storefrontSlug.trim().toLowerCase())
                .orElseThrow(() -> new NotFoundException("STOREFRONT_NOT_FOUND", "This store was not found."));
        if (business.getStatus() != BusinessStatus.ACTIVE) {
            throw new NotFoundException("STOREFRONT_NOT_FOUND", "This store is currently unavailable.");
        }
        return business;
    }
}
