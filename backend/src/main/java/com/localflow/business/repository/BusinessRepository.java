package com.localflow.business.repository;

import com.localflow.business.domain.Business;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BusinessRepository extends JpaRepository<Business, Long> {
    Optional<Business> findByStorefrontSlug(String storefrontSlug);
    Optional<Business> findByCustomerRegistrationCodeIgnoreCase(String customerRegistrationCode);
    boolean existsByEmailIgnoreCase(String email);
}

