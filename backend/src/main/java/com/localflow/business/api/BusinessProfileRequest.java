package com.localflow.business.api;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record BusinessProfileRequest(
        @Size(max = 15) @Pattern(regexp = "(?i)^[A-Z0-9]{15}$") String gstin,
        @Size(max = 32) String phone,
        @Size(max = 1000) String address
) { }
