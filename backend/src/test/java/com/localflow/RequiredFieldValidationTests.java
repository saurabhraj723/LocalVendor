package com.localflow;

import com.localflow.catalogue.api.dto.ProductRequest;
import com.localflow.customer.api.dto.CustomerAddressRequest;
import com.localflow.customer.api.dto.CustomerRequest;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertTrue;

class RequiredFieldValidationTests {

    private final Validator validator;

    RequiredFieldValidationTests() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void customerRequiresPhoneEmailAndAtLeastOneAddress() {
        CustomerRequest request = new CustomerRequest("Customer", " ", "invalid-email", null, List.of());

        Set<String> invalidFields = validator.validate(request).stream()
                .map(error -> error.getPropertyPath().toString())
                .collect(Collectors.toSet());

        assertTrue(invalidFields.containsAll(Set.of("phone", "email", "addresses")));
    }

    @Test
    void customerAddressRequiresStreetAndCity() {
        CustomerAddressRequest address = new CustomerAddressRequest(
                "Home", null, null, " ", null, null, " ", null, null, "IN", null, true
        );
        CustomerRequest request = new CustomerRequest("Customer", "9876543210", "customer@example.com", null,
                List.of(address));

        Set<String> invalidFields = validator.validate(request).stream()
                .map(error -> error.getPropertyPath().toString())
                .collect(Collectors.toSet());

        assertTrue(invalidFields.containsAll(Set.of("addresses[0].addressLine1", "addresses[0].city")));
    }

    @Test
    void productRequiresSkuCategoryAndQuantityRules() {
        ProductRequest request = new ProductRequest(
                "Product", null, " ", "kg", BigDecimal.ONE, null, null, null
        );

        Set<String> invalidFields = validator.validate(request).stream()
                .map(error -> error.getPropertyPath().toString())
                .collect(Collectors.toSet());

        assertTrue(invalidFields.containsAll(Set.of("sku", "categoryId", "minQuantity", "quantityStep")));
    }

    @Test
    void selectedTaxTypeRequiresPositiveTaxRate() {
        ProductRequest request = new ProductRequest(
                "Product", null, "SKU-1", "each", BigDecimal.TEN, 1L,
                BigDecimal.ONE, BigDecimal.ONE, BigDecimal.ZERO, "GST", BigDecimal.ZERO
        );

        Set<String> invalidFields = validator.validate(request).stream()
                .map(error -> error.getPropertyPath().toString())
                .collect(Collectors.toSet());

        assertTrue(invalidFields.contains("taxConfigurationValid"));
    }
}
