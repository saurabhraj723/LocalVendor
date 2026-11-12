package com.localflow.order;

import com.localflow.business.domain.Business;
import com.localflow.business.repository.BusinessRepository;
import com.localflow.catalogue.domain.Product;
import com.localflow.catalogue.repository.ProductRepository;
import com.localflow.common.domain.UserRole;
import com.localflow.common.exception.ConflictException;
import com.localflow.common.exception.NotFoundException;
import com.localflow.common.security.CurrentUser;
import com.localflow.common.security.CurrentUserProvider;
import com.localflow.customer.domain.Customer;
import com.localflow.customer.repository.CustomerRepository;
import com.localflow.order.api.dto.ChangeOrderStatusRequest;
import com.localflow.order.api.dto.CreateOrderRequest;
import com.localflow.order.api.dto.CreateWhatsAppOrderRequest;
import com.localflow.order.api.dto.OrderItemRequest;
import com.localflow.order.domain.CustomerOrder;
import com.localflow.order.domain.OrderStatus;
import com.localflow.order.repository.OrderRepository;
import com.localflow.order.service.OrderService;
import com.localflow.user.domain.User;
import com.localflow.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class OrderServiceTests {

    private static final long BUSINESS_ID = 41L;
    private static final long USER_ID = 17L;
    private static final long CUSTOMER_ID = 23L;
    private static final long PRODUCT_ID = 31L;

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private BusinessRepository businessRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private CurrentUserProvider currentUserProvider;

    @InjectMocks
    private OrderService orderService;

    private Customer customer;
    private Product product;
    private Business business;

    @BeforeEach
    void setUp() {
        when(currentUserProvider.getCurrentUser()).thenReturn(
                new CurrentUser(USER_ID, BUSINESS_ID, UserRole.BUSINESS_OWNER, "owner@example.com"));
        when(currentUserProvider.currentBusinessId()).thenReturn(BUSINESS_ID);

        business = new Business();
        business.setName("Local Shop");
        business.setBusinessType("GROCERY");
        when(businessRepository.getReferenceById(BUSINESS_ID)).thenReturn(business);

        customer = new Customer();
        customer.setName("Asha");
        when(customerRepository.findByIdAndBusiness_Id(CUSTOMER_ID, BUSINESS_ID))
                .thenReturn(Optional.of(customer));

        product = new Product();
        product.setName("Mango");
        product.setUnit("kg");
        product.setPrice(new BigDecimal("200.00"));
        product.setMinQuantity(new BigDecimal("1.000"));
        product.setQuantityStep(new BigDecimal("0.500"));
        when(productRepository.findByIdAndBusiness_IdAndActiveTrue(PRODUCT_ID, BUSINESS_ID))
                .thenReturn(Optional.of(product));

        when(userRepository.getReferenceById(USER_ID)).thenReturn(new User());
        when(orderRepository.save(any(CustomerOrder.class))).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void createCalculatesTotalsAndPreservesProductSnapshot() {
        var response = orderService.create(new CreateOrderRequest(
                CUSTOMER_ID,
                List.of(new OrderItemRequest(PRODUCT_ID, new BigDecimal("2.000"))),
                null));

        assertEquals(new BigDecimal("400.00"), response.subtotal());
        assertEquals(new BigDecimal("400.00"), response.totalAmount());
        assertEquals("Mango", response.items().getFirst().productName());
        assertEquals("kg", response.items().getFirst().unit());
        assertEquals(new BigDecimal("200.00"), response.items().getFirst().unitPrice());
        assertEquals(new BigDecimal("400.00"), response.items().getFirst().lineTotal());

        ArgumentCaptor<CustomerOrder> orderCaptor = ArgumentCaptor.forClass(CustomerOrder.class);
        verify(orderRepository).save(orderCaptor.capture());
        assertEquals(OrderStatus.NEW, orderCaptor.getValue().getStatus());
        assertEquals(customer, orderCaptor.getValue().getCustomer());
    }

    @Test
    void createAppliesDiscountBeforeCalculatingTax() {
        product.setDiscountPercent(new BigDecimal("10.00"));
        product.setTaxType("GST");
        product.setTaxPercent(new BigDecimal("5.00"));

        var response = orderService.create(new CreateOrderRequest(
                CUSTOMER_ID,
                List.of(new OrderItemRequest(PRODUCT_ID, new BigDecimal("2.000"))),
                null));

        assertEquals(new BigDecimal("400.00"), response.subtotal());
        assertEquals(new BigDecimal("40.00"), response.discount());
        assertEquals(new BigDecimal("18.00"), response.tax());
        assertEquals(new BigDecimal("378.00"), response.totalAmount());
        assertEquals(new BigDecimal("360.00"), response.items().getFirst().lineTotal());
        assertEquals(new BigDecimal("18.00"), response.items().getFirst().taxAmount());
    }

    @Test
    void simulatedWhatsAppOrderIsTaggedAndSavedWithMessageId() {
        when(orderRepository.findByBusiness_IdAndWhatsappMessageId(BUSINESS_ID, "wamid.test.1"))
                .thenReturn(Optional.empty());

        var result = orderService.createFromWhatsAppTest(new CreateWhatsAppOrderRequest(
                "wamid.test.1", CUSTOMER_ID,
                List.of(new OrderItemRequest(PRODUCT_ID, new BigDecimal("2.000"))), null));

        assertEquals("WHATSAPP", result.order().source());
        assertTrue(result.created());
        ArgumentCaptor<CustomerOrder> orderCaptor = ArgumentCaptor.forClass(CustomerOrder.class);
        verify(orderRepository).save(orderCaptor.capture());
        assertEquals("WHATSAPP", orderCaptor.getValue().getSource());
        assertEquals("wamid.test.1", orderCaptor.getValue().getWhatsappMessageId());
    }

    @Test
    void repeatedWhatsAppMessageReturnsExistingOrderWithoutCreatingDuplicate() {
        CustomerOrder existing = orderInBusiness(OrderStatus.NEW);
        existing.setWhatsappMessageId("wamid.test.1");
        when(orderRepository.findByBusiness_IdAndWhatsappMessageId(BUSINESS_ID, "wamid.test.1"))
                .thenReturn(Optional.of(existing));

        var result = orderService.createFromWhatsAppTest(new CreateWhatsAppOrderRequest(
                "wamid.test.1", CUSTOMER_ID,
                List.of(new OrderItemRequest(PRODUCT_ID, new BigDecimal("2.000"))), null));

        assertEquals("LF-TESTORDER01", result.order().orderNumber());
        assertFalse(result.created());
        verify(orderRepository, org.mockito.Mockito.never()).save(any());
    }

    @Test
    void statusChangeAllowsOnlyConfiguredTransitions() {
        CustomerOrder order = orderInBusiness(OrderStatus.READY);
        when(orderRepository.findByIdAndBusiness_Id(5L, BUSINESS_ID)).thenReturn(Optional.of(order));

        var response = orderService.changeStatus(5L,
                new ChangeOrderStatusRequest(OrderStatus.OUT_FOR_DELIVERY, "Courier picked up"));

        assertEquals(OrderStatus.OUT_FOR_DELIVERY, response.status());
        assertEquals(OrderStatus.OUT_FOR_DELIVERY, order.getStatus());
        assertEquals(OrderStatus.READY, response.statusHistory().getLast().oldStatus());
        assertEquals(OrderStatus.OUT_FOR_DELIVERY, response.statusHistory().getLast().newStatus());
        verify(orderRepository).flush();
    }

    @Test
    void statusChangeRejectsInvalidTransitionWithoutChangingOrder() {
        CustomerOrder order = orderInBusiness(OrderStatus.NEW);
        when(orderRepository.findByIdAndBusiness_Id(5L, BUSINESS_ID)).thenReturn(Optional.of(order));

        assertThrows(ConflictException.class, () -> orderService.changeStatus(5L,
                new ChangeOrderStatusRequest(OrderStatus.DELIVERED, null)));
        assertEquals(OrderStatus.NEW, order.getStatus());
        assertEquals(0, order.getStatusHistory().size());
    }

    @Test
    void createCannotUseAnotherBusinessCustomerOrProduct() {
        when(customerRepository.findByIdAndBusiness_Id(CUSTOMER_ID, BUSINESS_ID)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> orderService.create(orderRequest()));

        when(customerRepository.findByIdAndBusiness_Id(CUSTOMER_ID, BUSINESS_ID))
                .thenReturn(Optional.of(customer));
        when(productRepository.findByIdAndBusiness_IdAndActiveTrue(PRODUCT_ID, BUSINESS_ID))
                .thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> orderService.create(orderRequest()));
    }

    @Test
    void cannotReadOrChangeAnotherBusinessesOrder() {
        when(orderRepository.findByIdAndBusiness_Id(99L, BUSINESS_ID)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> orderService.get(99L));
        assertThrows(NotFoundException.class, () -> orderService.changeStatus(99L,
                new ChangeOrderStatusRequest(OrderStatus.CONFIRMED, null)));
        verify(orderRepository, org.mockito.Mockito.times(2)).findByIdAndBusiness_Id(99L, BUSINESS_ID);
    }

    private CreateOrderRequest orderRequest() {
        return new CreateOrderRequest(CUSTOMER_ID,
                List.of(new OrderItemRequest(PRODUCT_ID, new BigDecimal("2.000"))), null);
    }

    private CustomerOrder orderInBusiness(OrderStatus status) {
        CustomerOrder order = new CustomerOrder();
        order.setBusiness(business);
        order.setCustomer(customer);
        order.setOrderNumber("LF-TESTORDER01");
        order.setCurrency("INR");
        order.setSubtotal(BigDecimal.ZERO);
        order.setTotalAmount(BigDecimal.ZERO);
        order.setStatus(status);
        return order;
    }
}
