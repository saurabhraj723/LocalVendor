package com.localflow.customer.repository;

import com.localflow.customer.domain.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.time.OffsetDateTime;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Optional<Customer> findByIdAndBusiness_Id(Long id, Long businessId);

    List<Customer> findAllByBusiness_IdOrderByNameAsc(Long businessId);

    Optional<Customer> findByBusiness_IdAndEmailIgnoreCase(Long businessId, String email);

    List<Customer> findAllByBusiness_IdAndEmailIgnoreCaseAndPasswordHashIsNotNull(Long businessId, String email);

    long countByBusiness_IdAndCreatedAtGreaterThanEqualAndCreatedAtLessThan(
            Long businessId,
            OffsetDateTime start,
            OffsetDateTime end
    );
}
