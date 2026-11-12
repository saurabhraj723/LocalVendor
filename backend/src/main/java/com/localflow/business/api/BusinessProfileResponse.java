package com.localflow.business.api;

import com.localflow.business.domain.Business;

public record BusinessProfileResponse(
        Long id,
        String name,
        String businessType,
        String gstin,
        String phone,
        String email,
        String address,
        String currency,
        String timezone
) {
    public static BusinessProfileResponse from(Business business) {
        return new BusinessProfileResponse(
                business.getId(), business.getName(), business.getBusinessType(), business.getGstin(),
                business.getPhone(), business.getEmail(), business.getAddress(),
                business.getCurrency(), business.getTimezone()
        );
    }
}
