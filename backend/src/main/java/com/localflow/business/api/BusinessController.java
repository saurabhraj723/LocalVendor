package com.localflow.business.api;

import com.localflow.business.repository.BusinessRepository;
import com.localflow.common.exception.NotFoundException;
import com.localflow.common.security.CurrentUserProvider;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/business")
public class BusinessController {
    private final BusinessRepository businessRepository;
    private final CurrentUserProvider currentUserProvider;

    public BusinessController(BusinessRepository businessRepository, CurrentUserProvider currentUserProvider) {
        this.businessRepository = businessRepository;
        this.currentUserProvider = currentUserProvider;
    }

    @GetMapping("/storefront-settings")
    public BusinessStorefrontSettingsResponse storefrontSettings() {
        var business = businessRepository.findById(currentUserProvider.currentBusinessId())
                .orElseThrow(() -> new NotFoundException("BUSINESS_NOT_FOUND", "Business was not found."));
        return new BusinessStorefrontSettingsResponse(business.getStorefrontSlug(), business.getCustomerRegistrationCode());
    }

    @GetMapping("/profile")
    public BusinessProfileResponse profile() {
        var business = businessRepository.findById(currentUserProvider.currentBusinessId())
                .orElseThrow(() -> new NotFoundException("BUSINESS_NOT_FOUND", "Business was not found."));
        return BusinessProfileResponse.from(business);
    }

    @PutMapping("/profile")
    public BusinessProfileResponse updateProfile(@Valid @RequestBody BusinessProfileRequest request) {
        var business = businessRepository.findById(currentUserProvider.currentBusinessId())
                .orElseThrow(() -> new NotFoundException("BUSINESS_NOT_FOUND", "Business was not found."));
        business.setGstin(blankToNull(request.gstin()) == null ? null : request.gstin().trim().toUpperCase());
        business.setPhone(blankToNull(request.phone()));
        business.setAddress(blankToNull(request.address()));
        return BusinessProfileResponse.from(businessRepository.save(business));
    }

    private static String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
