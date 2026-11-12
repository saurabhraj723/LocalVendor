package com.localflow.customer.api.dto;

import com.localflow.customer.domain.CustomerAddress;

public record CustomerAddressResponse(
        Long id,
        String label,
        String recipientName,
        String phone,
        String addressLine1,
        String addressLine2,
        String locality,
        String city,
        String state,
        String postalCode,
        String countryCode,
        String deliveryInstructions,
        boolean defaultAddress
) {
    public static CustomerAddressResponse from(CustomerAddress address) {
        return new CustomerAddressResponse(
                address.getId(), address.getLabel(), address.getRecipientName(), address.getPhone(),
                address.getAddressLine1(), address.getAddressLine2(), address.getLocality(), address.getCity(),
                address.getState(), address.getPostalCode(), address.getCountryCode(),
                address.getDeliveryInstructions(), address.isDefaultAddress()
        );
    }
}
