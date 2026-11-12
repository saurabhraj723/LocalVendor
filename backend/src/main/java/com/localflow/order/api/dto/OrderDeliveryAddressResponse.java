package com.localflow.order.api.dto;

import com.localflow.order.domain.CustomerOrder;

public record OrderDeliveryAddressResponse(
        String recipientName,
        String phone,
        String addressLine1,
        String addressLine2,
        String locality,
        String city,
        String state,
        String postalCode,
        String countryCode,
        String deliveryInstructions
) {
    public static OrderDeliveryAddressResponse from(CustomerOrder order) {
        if (order.getDeliveryAddressLine1() == null) return null;
        return new OrderDeliveryAddressResponse(
                order.getDeliveryRecipientName(), order.getDeliveryPhone(), order.getDeliveryAddressLine1(),
                order.getDeliveryAddressLine2(), order.getDeliveryLocality(), order.getDeliveryCity(),
                order.getDeliveryState(), order.getDeliveryPostalCode(), order.getDeliveryCountryCode(),
                order.getDeliveryInstructions()
        );
    }
}
