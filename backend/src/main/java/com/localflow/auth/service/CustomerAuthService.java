package com.localflow.auth.service;

import com.localflow.auth.api.dto.CustomerAuthResponse;
import com.localflow.auth.api.dto.CustomerLoginRequest;
import com.localflow.auth.api.dto.CustomerRegisterRequest;
import com.localflow.business.domain.Business;
import com.localflow.business.repository.BusinessRepository;
import com.localflow.common.domain.BusinessStatus;
import com.localflow.common.exception.ConflictException;
import com.localflow.common.exception.NotFoundException;
import com.localflow.common.exception.UnauthorizedException;
import com.localflow.customer.domain.Customer;
import com.localflow.customer.repository.CustomerRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomerAuthService {
    private final BusinessRepository businessRepository;
    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenService jwtTokenService;

    public CustomerAuthService(BusinessRepository businessRepository, CustomerRepository customerRepository,
                               PasswordEncoder passwordEncoder, JwtTokenService jwtTokenService) {
        this.businessRepository = businessRepository;
        this.customerRepository = customerRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenService = jwtTokenService;
    }

    @Transactional
    public CustomerAuthResponse register(CustomerRegisterRequest request) {
        Business business = activeBusiness(request.storefrontSlug());
        if (!business.getCustomerRegistrationCode().equalsIgnoreCase(request.registrationCode().trim())) {
            throw new UnauthorizedException("INVALID_STORE_CODE", "The store signup code is not valid.");
        }
        String email = normalizeEmail(request.email());
        boolean alreadyRegistered = !customerRepository
                .findAllByBusiness_IdAndEmailIgnoreCaseAndPasswordHashIsNotNull(business.getId(), email).isEmpty();
        if (alreadyRegistered) {
            throw new ConflictException("CUSTOMER_ACCOUNT_EXISTS", "An account with this email already exists for this store.");
        }

        Customer customer = new Customer();
        customer.setBusiness(business);
        customer.setName(request.name().trim());
        customer.setPhone(request.phone().trim());
        customer.setEmail(email);
        customer.setPasswordHash(passwordEncoder.encode(request.password()));
        return response(customerRepository.save(customer), business);
    }

    @Transactional(readOnly = true)
    public CustomerAuthResponse login(CustomerLoginRequest request) {
        Business business = activeBusiness(request.storefrontSlug());
        Customer customer = customerRepository
                .findAllByBusiness_IdAndEmailIgnoreCaseAndPasswordHashIsNotNull(business.getId(), normalizeEmail(request.email()))
                .stream().findFirst()
                .orElseThrow(() -> new UnauthorizedException("INVALID_CREDENTIALS", "Invalid email or password."));
        if (!passwordEncoder.matches(request.password(), customer.getPasswordHash())) {
            throw new UnauthorizedException("INVALID_CREDENTIALS", "Invalid email or password.");
        }
        return response(customer, business);
    }

    private Business activeBusiness(String slug) {
        Business business = businessRepository.findByStorefrontSlug(slug.trim().toLowerCase())
                .orElseThrow(() -> new NotFoundException("STOREFRONT_NOT_FOUND", "This store was not found."));
        if (business.getStatus() != BusinessStatus.ACTIVE) {
            throw new NotFoundException("STOREFRONT_NOT_FOUND", "This store is currently unavailable.");
        }
        return business;
    }

    private CustomerAuthResponse response(Customer customer, Business business) {
        JwtTokenService.Token token = jwtTokenService.createCustomerAccessToken(customer);
        return new CustomerAuthResponse(token.value(), "Bearer", token.expiresInSeconds(),
                new CustomerAuthResponse.CustomerSummary(customer.getId(), customer.getName(), customer.getEmail(), customer.getPhone()),
                new CustomerAuthResponse.StoreSummary(business.getStorefrontSlug(), business.getName(), business.getCurrency()));
    }

    private static String normalizeEmail(String email) { return email.trim().toLowerCase(); }
}
