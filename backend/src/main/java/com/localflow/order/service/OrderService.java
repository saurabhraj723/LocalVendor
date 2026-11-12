package com.localflow.order.service;

import com.localflow.business.domain.Business;
import com.localflow.business.repository.BusinessRepository;
import com.localflow.catalogue.domain.Product;
import com.localflow.catalogue.repository.ProductRepository;
import com.localflow.common.exception.ConflictException;
import com.localflow.common.exception.NotFoundException;
import com.localflow.common.security.CurrentUser;
import com.localflow.common.security.CurrentUserProvider;
import com.localflow.customer.domain.Customer;
import com.localflow.customer.domain.CustomerAddress;
import com.localflow.customer.repository.CustomerRepository;
import com.localflow.order.api.dto.ChangeOrderStatusRequest;
import com.localflow.order.api.dto.CreateOrderRequest;
import com.localflow.order.api.dto.CreateWhatsAppOrderRequest;
import com.localflow.order.api.dto.OrderItemRequest;
import com.localflow.order.api.dto.OrderResponse;
import com.localflow.order.domain.CustomerOrder;
import com.localflow.order.domain.OrderItem;
import com.localflow.order.domain.OrderStatus;
import com.localflow.order.domain.OrderStatusHistory;
import com.localflow.order.repository.OrderRepository;
import com.localflow.storefront.api.dto.CreateStorefrontOrderRequest;
import com.localflow.user.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
public class OrderService {

    private static final Map<OrderStatus, Set<OrderStatus>> TRANSITIONS = transitions();

    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final BusinessRepository businessRepository;
    private final UserRepository userRepository;
    private final CurrentUserProvider currentUserProvider;

    public OrderService(
            OrderRepository orderRepository,
            CustomerRepository customerRepository,
            ProductRepository productRepository,
            BusinessRepository businessRepository,
            UserRepository userRepository,
            CurrentUserProvider currentUserProvider
    ) {
        this.orderRepository = orderRepository;
        this.customerRepository = customerRepository;
        this.productRepository = productRepository;
        this.businessRepository = businessRepository;
        this.userRepository = userRepository;
        this.currentUserProvider = currentUserProvider;
    }

    @Transactional
    public OrderResponse create(CreateOrderRequest request) {
        CurrentUser currentUser = currentUserProvider.getCurrentUser();
        return create(request, currentUser.businessId(), currentUser.userId(), "VENDOR_APP", null, null, null);
    }

    @Transactional
    public WhatsAppOrderIntakeResult createFromWhatsAppTest(CreateWhatsAppOrderRequest request) {
        CurrentUser currentUser = currentUserProvider.getCurrentUser();
        return orderRepository.findByBusiness_IdAndWhatsappMessageId(currentUser.businessId(), request.messageId())
                .map(existing -> new WhatsAppOrderIntakeResult(OrderResponse.from(existing), false))
                .orElseGet(() -> new WhatsAppOrderIntakeResult(create(
                                new CreateOrderRequest(request.customerId(), request.items(), request.notes()),
                                currentUser.businessId(),
                                currentUser.userId(),
                                "WHATSAPP",
                                request.messageId(),
                                null,
                                request.addressId()
                        ), true));
    }

    @Transactional
    public StorefrontOrderResult createFromStorefront(Long businessId, Long customerId, CreateStorefrontOrderRequest request) {
        var existing = orderRepository.findByBusiness_IdAndCustomer_IdAndStorefrontRequestId(
                businessId, customerId, request.requestId());
        if (existing.isPresent()) return new StorefrontOrderResult(OrderResponse.from(existing.get()), false);

        Customer customer = customerRepository.findByIdAndBusiness_Id(customerId, businessId)
                .orElseThrow(() -> new NotFoundException("CUSTOMER_NOT_FOUND", "Customer account was not found for this store."));

        CustomerAddress address = customer.getAddresses().stream()
                .filter(candidate -> sameAddress(candidate, request))
                .findFirst().orElse(null);
        if (address == null) {
            address = new CustomerAddress();
            address.setCustomer(customer);
            address.setLabel("Customer website");
            address.setRecipientName(customer.getName());
            address.setPhone(customer.getPhone());
            address.setAddressLine1(request.addressLine1().trim());
            address.setAddressLine2(blankToNull(request.addressLine2()));
            address.setLocality(blankToNull(request.locality()));
            address.setCity(request.city().trim());
            address.setState(blankToNull(request.state()));
            address.setPostalCode(blankToNull(request.postalCode()));
            address.setCountryCode(defaultValue(request.countryCode(), "IN").toUpperCase());
            address.setDeliveryInstructions(blankToNull(request.deliveryInstructions()));
            address.setDefaultAddress(customer.getAddresses().isEmpty());
            address.setSortOrder(customer.getAddresses().size());
            customer.getAddresses().add(address);
        }
        customerRepository.saveAndFlush(customer);

        OrderResponse order = create(
                new CreateOrderRequest(customer.getId(), request.items(), request.notes()),
                businessId,
                null,
                "CUSTOMER_WEB",
                null,
                request.requestId(),
                address.getId()
        );
        return new StorefrontOrderResult(order, true);
    }

    private OrderResponse create(
            CreateOrderRequest request,
            Long businessId,
            Long actorUserId,
            String source,
            String whatsappMessageId,
            String storefrontRequestId,
            Long addressId
    ) {
        Customer customer = customerRepository.findByIdAndBusiness_Id(request.customerId(), businessId)
                .orElseThrow(() -> new NotFoundException("CUSTOMER_NOT_FOUND", "Customer was not found."));
        Business business = businessRepository.getReferenceById(businessId);

        CustomerOrder order = new CustomerOrder();
        order.setBusiness(business);
        order.setCustomer(customer);
        order.setOrderNumber(newOrderNumber());
        order.setCurrency(business.getCurrency());
        order.setNotes(blankToNull(request.notes()));
        order.setSource(source);
        order.setWhatsappMessageId(whatsappMessageId);
        order.setStorefrontRequestId(storefrontRequestId);
        if (addressId != null) {
            CustomerAddress address = customer.getAddresses().stream()
                    .filter(candidate -> candidate.getId().equals(addressId))
                    .findFirst()
                    .orElseThrow(() -> new NotFoundException("CUSTOMER_ADDRESS_NOT_FOUND", "Customer address was not found."));
            order.setDeliveryRecipientName(address.getRecipientName());
            order.setDeliveryPhone(address.getPhone());
            order.setDeliveryAddressLine1(address.getAddressLine1());
            order.setDeliveryAddressLine2(address.getAddressLine2());
            order.setDeliveryLocality(address.getLocality());
            order.setDeliveryCity(address.getCity());
            order.setDeliveryState(address.getState());
            order.setDeliveryPostalCode(address.getPostalCode());
            order.setDeliveryCountryCode(address.getCountryCode());
            order.setDeliveryInstructions(address.getDeliveryInstructions());
        }

        BigDecimal subtotal = BigDecimal.ZERO.setScale(2);
        BigDecimal discountTotal = BigDecimal.ZERO.setScale(2);
        BigDecimal taxTotal = BigDecimal.ZERO.setScale(2);
        Set<Long> productIds = new HashSet<>();
        for (OrderItemRequest requestedItem : request.items()) {
            if (!productIds.add(requestedItem.productId())) {
                throw new ConflictException("DUPLICATE_ORDER_PRODUCT", "Each product can appear only once in an order.");
            }
            Product product = productRepository.findByIdAndBusiness_IdAndActiveTrue(requestedItem.productId(), businessId)
                    .orElseThrow(() -> new NotFoundException("PRODUCT_NOT_FOUND", "Product was not found."));
            if (!product.isAvailable()) {
                throw new ConflictException("PRODUCT_UNAVAILABLE", "Product " + product.getName() + " is not available.");
            }
            validateQuantity(product, requestedItem);

            BigDecimal grossLineTotal = product.getPrice().multiply(requestedItem.quantity())
                    .setScale(2, RoundingMode.HALF_UP);
            BigDecimal lineDiscount = grossLineTotal.multiply(product.getDiscountPercent())
                    .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
            BigDecimal lineTotal = grossLineTotal.subtract(lineDiscount).setScale(2, RoundingMode.HALF_UP);
            BigDecimal lineTax = product.getTaxPercent() == null ? BigDecimal.ZERO.setScale(2)
                    : lineTotal.multiply(product.getTaxPercent()).divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
            subtotal = subtotal.add(grossLineTotal);
            discountTotal = discountTotal.add(lineDiscount);
            taxTotal = taxTotal.add(lineTax);

            OrderItem item = new OrderItem();
            item.setProduct(product);
            item.setProductName(product.getName());
            item.setUnit(product.getUnit());
            item.setUnitPrice(product.getPrice());
            item.setQuantity(requestedItem.quantity());
            item.setLineTotal(lineTotal);
            item.setDiscountPercent(product.getDiscountPercent());
            item.setDiscountAmount(lineDiscount);
            item.setTaxType(product.getTaxType());
            item.setTaxPercent(product.getTaxPercent());
            item.setTaxAmount(lineTax);
            order.addItem(item);
        }

        order.setSubtotal(subtotal);
        order.setDiscount(discountTotal);
        order.setTax(taxTotal);
        order.setTotalAmount(subtotal.subtract(discountTotal).add(taxTotal).add(order.getDeliveryCharge()));
        addHistory(order, null, OrderStatus.NEW, actorUserId, source.equals("CUSTOMER_WEB")
                ? "Order placed from customer storefront."
                : "Order created.");
        return OrderResponse.from(orderRepository.save(order));
    }

    @Transactional(readOnly = true)
    public Page<OrderResponse> list(int page, int size) {
        return orderRepository.findAllByBusiness_IdOrderByCreatedAtDesc(
                        currentUserProvider.currentBusinessId(),
                        pageable(page, size))
                .map(OrderResponse::from);
    }

    @Transactional(readOnly = true)
    public Page<OrderResponse> customerOrders(Long customerId, int page, int size) {
        Long businessId = currentUserProvider.currentBusinessId();
        if (!customerRepository.findByIdAndBusiness_Id(customerId, businessId).isPresent()) {
            throw new NotFoundException("CUSTOMER_NOT_FOUND", "Customer was not found.");
        }
        return orderRepository.findAllByCustomer_IdAndBusiness_IdOrderByCreatedAtDesc(
                        customerId, businessId, pageable(page, size))
                .map(OrderResponse::from);
    }

    @Transactional(readOnly = true)
    public Page<OrderResponse> portalOrders(Long customerId, Long businessId, int page, int size) {
        return orderRepository.findAllByCustomer_IdAndBusiness_IdOrderByCreatedAtDesc(
                        customerId, businessId, pageable(page, size))
                .map(OrderResponse::from);
    }

    @Transactional(readOnly = true)
    public OrderResponse portalOrder(Long orderId, Long customerId, Long businessId) {
        return orderRepository.findByIdAndCustomer_IdAndBusiness_Id(orderId, customerId, businessId)
                .map(OrderResponse::from)
                .orElseThrow(() -> new NotFoundException("ORDER_NOT_FOUND", "Order was not found."));
    }

    @Transactional(readOnly = true)
    public OrderResponse get(Long id) {
        return OrderResponse.from(findOrder(currentUserProvider.currentBusinessId(), id));
    }

    @Transactional
    public OrderResponse changeStatus(Long id, ChangeOrderStatusRequest request) {
        CurrentUser currentUser = currentUserProvider.getCurrentUser();
        CustomerOrder order = findOrder(currentUser.businessId(), id);
        OrderStatus oldStatus = order.getStatus();
        if (!TRANSITIONS.getOrDefault(oldStatus, Set.of()).contains(request.status())) {
            throw new ConflictException(
                    "INVALID_ORDER_STATUS",
                    "Order cannot move from " + oldStatus + " to " + request.status() + "."
            );
        }

        order.setStatus(request.status());
        addHistory(order, oldStatus, request.status(), currentUser.userId(), blankToNull(request.notes()));
        orderRepository.flush();
        return OrderResponse.from(order);
    }

    private CustomerOrder findOrder(Long businessId, Long id) {
        return orderRepository.findByIdAndBusiness_Id(id, businessId)
                .orElseThrow(() -> new NotFoundException("ORDER_NOT_FOUND", "Order was not found."));
    }

    private void addHistory(
            CustomerOrder order,
            OrderStatus oldStatus,
            OrderStatus newStatus,
            Long userId,
            String notes
    ) {
        OrderStatusHistory history = new OrderStatusHistory();
        history.setOldStatus(oldStatus);
        history.setNewStatus(newStatus);
        if (userId != null) history.setChangedBy(userRepository.getReferenceById(userId));
        history.setNotes(notes);
        order.addStatusHistory(history);
    }

    private static void validateQuantity(Product product, OrderItemRequest item) {
        BigDecimal quantity = item.quantity();
        BigDecimal minimum = product.getMinQuantity();
        BigDecimal step = product.getQuantityStep();
        if (quantity.compareTo(minimum) < 0
                || quantity.subtract(minimum).remainder(step).compareTo(BigDecimal.ZERO) != 0) {
            throw new ConflictException(
                    "INVALID_QUANTITY",
                    "Quantity for " + product.getName() + " must be at least " + minimum + " and increase by " + step + "."
            );
        }
    }

    private static String newOrderNumber() {
        return "LF-" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
    }

    private static String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    private static String defaultValue(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value.trim();
    }

    private static boolean sameAddress(CustomerAddress address, CreateStorefrontOrderRequest request) {
        return normalize(address.getAddressLine1()).equals(normalize(request.addressLine1()))
                && normalize(address.getAddressLine2()).equals(normalize(request.addressLine2()))
                && normalize(address.getLocality()).equals(normalize(request.locality()))
                && normalize(address.getCity()).equals(normalize(request.city()))
                && normalize(address.getState()).equals(normalize(request.state()))
                && normalize(address.getPostalCode()).equals(normalize(request.postalCode()))
                && normalize(address.getCountryCode()).equals(normalize(defaultValue(request.countryCode(), "IN")));
    }

    private static String normalize(String value) {
        return value == null ? "" : value.trim().toLowerCase();
    }

    private static PageRequest pageable(int page, int size) {
        int safePage = Math.max(0, page);
        int safeSize = Math.min(100, Math.max(1, size));
        return PageRequest.of(safePage, safeSize, Sort.by(Sort.Direction.DESC, "createdAt"));
    }

    private static Map<OrderStatus, Set<OrderStatus>> transitions() {
        Map<OrderStatus, Set<OrderStatus>> allowed = new EnumMap<>(OrderStatus.class);
        allowed.put(OrderStatus.NEW, EnumSet.of(OrderStatus.CONFIRMED, OrderStatus.REJECTED, OrderStatus.CANCELLED));
        allowed.put(OrderStatus.CONFIRMED, EnumSet.of(OrderStatus.PACKING, OrderStatus.CANCELLED));
        allowed.put(OrderStatus.PACKING, EnumSet.of(OrderStatus.READY));
        allowed.put(OrderStatus.READY, EnumSet.of(OrderStatus.OUT_FOR_DELIVERY, OrderStatus.PICKUP_READY));
        allowed.put(OrderStatus.OUT_FOR_DELIVERY, EnumSet.of(OrderStatus.DELIVERED));
        allowed.put(OrderStatus.PICKUP_READY, EnumSet.of(OrderStatus.COMPLETED));
        return Map.copyOf(allowed);
    }
}
