package com.localflow.customer.api.dto;

import com.localflow.customer.domain.Customer;

import java.time.OffsetDateTime;
import java.util.List;

public record CustomerResponse(
        Long id,
        String name,
        String phone,
        String email,
        String notes,
        List<CustomerAddressResponse> addresses,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
    public static CustomerResponse from(Customer customer) {
        return new CustomerResponse(
                customer.getId(), customer.getName(), customer.getPhone(), customer.getEmail(), customer.getNotes(),
                customer.getAddresses().stream().map(CustomerAddressResponse::from).toList(),
                customer.getCreatedAt(), customer.getUpdatedAt()
        );
    }
}
