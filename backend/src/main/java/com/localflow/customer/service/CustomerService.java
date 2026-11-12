package com.localflow.customer.service;

import com.localflow.business.repository.BusinessRepository;
import com.localflow.common.exception.ConflictException;
import com.localflow.common.exception.NotFoundException;
import com.localflow.common.security.CurrentUserProvider;
import com.localflow.customer.api.dto.CustomerAddressRequest;
import com.localflow.customer.api.dto.CustomerRequest;
import com.localflow.customer.api.dto.CustomerResponse;
import com.localflow.customer.domain.Customer;
import com.localflow.customer.domain.CustomerAddress;
import com.localflow.customer.repository.CustomerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final BusinessRepository businessRepository;
    private final CurrentUserProvider currentUserProvider;

    public CustomerService(
            CustomerRepository customerRepository,
            BusinessRepository businessRepository,
            CurrentUserProvider currentUserProvider
    ) {
        this.customerRepository = customerRepository;
        this.businessRepository = businessRepository;
        this.currentUserProvider = currentUserProvider;
    }

    @Transactional(readOnly = true)
    public List<CustomerResponse> list() {
        Long businessId = currentUserProvider.currentBusinessId();
        return customerRepository.findAllByBusiness_IdOrderByNameAsc(businessId)
                .stream().map(CustomerResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public CustomerResponse get(Long id) {
        return CustomerResponse.from(findCustomer(currentUserProvider.currentBusinessId(), id));
    }

    @Transactional
    public CustomerResponse create(CustomerRequest request) {
        validateDefaultAddress(request.addresses());
        Long businessId = currentUserProvider.currentBusinessId();
        Customer customer = new Customer();
        customer.setBusiness(businessRepository.getReferenceById(businessId));
        apply(customer, request);
        return CustomerResponse.from(customerRepository.save(customer));
    }

    @Transactional
    public CustomerResponse update(Long id, CustomerRequest request) {
        validateDefaultAddress(request.addresses());
        Customer customer = findCustomer(currentUserProvider.currentBusinessId(), id);
        if (request.addresses() != null) {
            customer.replaceAddresses(List.of());
            customerRepository.flush();
        }
        apply(customer, request);
        customerRepository.flush();
        return CustomerResponse.from(customer);
    }

    private Customer findCustomer(Long businessId, Long id) {
        return customerRepository.findByIdAndBusiness_Id(id, businessId)
                .orElseThrow(() -> new NotFoundException("CUSTOMER_NOT_FOUND", "Customer was not found."));
    }

    private static void apply(Customer customer, CustomerRequest request) {
        customer.setName(request.name().trim());
        customer.setPhone(blankToNull(request.phone()));
        customer.setEmail(blankToNull(request.email()));
        customer.setNotes(blankToNull(request.notes()));

        if (request.addresses() == null) {
            return;
        }
        List<CustomerAddressRequest> requestedAddresses = request.addresses();
        List<CustomerAddress> addresses = new ArrayList<>();
        for (int index = 0; index < requestedAddresses.size(); index++) {
            CustomerAddressRequest addressRequest = requestedAddresses.get(index);
            CustomerAddress address = new CustomerAddress();
            address.setLabel(addressRequest.label().trim());
            address.setRecipientName(blankToNull(addressRequest.recipientName()));
            address.setPhone(blankToNull(addressRequest.phone()));
            address.setAddressLine1(addressRequest.addressLine1().trim());
            address.setAddressLine2(blankToNull(addressRequest.addressLine2()));
            address.setLocality(blankToNull(addressRequest.locality()));
            address.setCity(addressRequest.city().trim());
            address.setState(blankToNull(addressRequest.state()));
            address.setPostalCode(blankToNull(addressRequest.postalCode()));
            address.setCountryCode(addressRequest.countryCode() == null || addressRequest.countryCode().isBlank()
                    ? "IN" : addressRequest.countryCode().trim().toUpperCase());
            address.setDeliveryInstructions(blankToNull(addressRequest.deliveryInstructions()));
            address.setDefaultAddress(addressRequest.defaultAddress());
            address.setSortOrder(index);
            addresses.add(address);
        }
        customer.replaceAddresses(addresses);
    }

    private static void validateDefaultAddress(List<CustomerAddressRequest> addresses) {
        if (addresses != null && addresses.stream().filter(CustomerAddressRequest::defaultAddress).count() > 1) {
            throw new ConflictException("MULTIPLE_DEFAULT_ADDRESSES", "Only one address can be marked as default.");
        }
    }

    private static String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
