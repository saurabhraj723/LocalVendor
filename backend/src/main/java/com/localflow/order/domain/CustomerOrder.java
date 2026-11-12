package com.localflow.order.domain;

import com.localflow.business.domain.Business;
import com.localflow.customer.domain.Customer;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
public class CustomerOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "business_id", nullable = false)
    private Business business;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Column(name = "order_number", nullable = false, length = 32)
    private String orderNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private OrderStatus status = OrderStatus.NEW;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal subtotal;

    @Column(name = "delivery_charge", nullable = false, precision = 12, scale = 2)
    private BigDecimal deliveryCharge = BigDecimal.ZERO;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal discount = BigDecimal.ZERO;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal tax = BigDecimal.ZERO;

    @Column(name = "total_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalAmount;

    @Column(nullable = false, length = 3)
    private String currency;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false, length = 24)
    private PaymentStatus paymentStatus = PaymentStatus.UNPAID;

    @Column(nullable = false, length = 24)
    private String source = "VENDOR_APP";

    @Column(name = "whatsapp_message_id", length = 160)
    private String whatsappMessageId;

    @Column(name = "storefront_request_id", length = 80)
    private String storefrontRequestId;

    @Column(name = "delivery_recipient_name", length = 160)
    private String deliveryRecipientName;

    @Column(name = "delivery_phone", length = 32)
    private String deliveryPhone;

    @Column(name = "delivery_address_line1", length = 240)
    private String deliveryAddressLine1;

    @Column(name = "delivery_address_line2", length = 240)
    private String deliveryAddressLine2;

    @Column(name = "delivery_locality", length = 160)
    private String deliveryLocality;

    @Column(name = "delivery_city", length = 120)
    private String deliveryCity;

    @Column(name = "delivery_state", length = 120)
    private String deliveryState;

    @Column(name = "delivery_postal_code", length = 24)
    private String deliveryPostalCode;

    @Column(name = "delivery_country_code", length = 2)
    private String deliveryCountryCode;

    @Column(name = "delivery_instructions", length = 500)
    private String deliveryInstructions;

    @Column(length = 1000)
    private String notes;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id ASC")
    private List<OrderItem> items = new ArrayList<>();

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("changedAt ASC")
    private List<OrderStatusHistory> statusHistory = new ArrayList<>();

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @PrePersist
    void prePersist() {
        OffsetDateTime now = OffsetDateTime.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    void preUpdate() { updatedAt = OffsetDateTime.now(); }

    public void addItem(OrderItem item) {
        item.setOrder(this);
        items.add(item);
    }

    public void addStatusHistory(OrderStatusHistory history) {
        history.setOrder(this);
        statusHistory.add(history);
    }

    public Long getId() { return id; }
    public Business getBusiness() { return business; }
    public void setBusiness(Business business) { this.business = business; }
    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }
    public String getOrderNumber() { return orderNumber; }
    public void setOrderNumber(String orderNumber) { this.orderNumber = orderNumber; }
    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }
    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }
    public BigDecimal getDeliveryCharge() { return deliveryCharge; }
    public void setDeliveryCharge(BigDecimal deliveryCharge) { this.deliveryCharge = deliveryCharge; }
    public BigDecimal getDiscount() { return discount; }
    public void setDiscount(BigDecimal discount) { this.discount = discount; }
    public BigDecimal getTax() { return tax; }
    public void setTax(BigDecimal tax) { this.tax = tax; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    public PaymentStatus getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(PaymentStatus paymentStatus) { this.paymentStatus = paymentStatus; }
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
    public String getWhatsappMessageId() { return whatsappMessageId; }
    public void setWhatsappMessageId(String whatsappMessageId) { this.whatsappMessageId = whatsappMessageId; }
    public String getStorefrontRequestId() { return storefrontRequestId; }
    public void setStorefrontRequestId(String value) { storefrontRequestId = value; }
    public String getDeliveryRecipientName() { return deliveryRecipientName; }
    public void setDeliveryRecipientName(String value) { deliveryRecipientName = value; }
    public String getDeliveryPhone() { return deliveryPhone; }
    public void setDeliveryPhone(String value) { deliveryPhone = value; }
    public String getDeliveryAddressLine1() { return deliveryAddressLine1; }
    public void setDeliveryAddressLine1(String value) { deliveryAddressLine1 = value; }
    public String getDeliveryAddressLine2() { return deliveryAddressLine2; }
    public void setDeliveryAddressLine2(String value) { deliveryAddressLine2 = value; }
    public String getDeliveryLocality() { return deliveryLocality; }
    public void setDeliveryLocality(String value) { deliveryLocality = value; }
    public String getDeliveryCity() { return deliveryCity; }
    public void setDeliveryCity(String value) { deliveryCity = value; }
    public String getDeliveryState() { return deliveryState; }
    public void setDeliveryState(String value) { deliveryState = value; }
    public String getDeliveryPostalCode() { return deliveryPostalCode; }
    public void setDeliveryPostalCode(String value) { deliveryPostalCode = value; }
    public String getDeliveryCountryCode() { return deliveryCountryCode; }
    public void setDeliveryCountryCode(String value) { deliveryCountryCode = value; }
    public String getDeliveryInstructions() { return deliveryInstructions; }
    public void setDeliveryInstructions(String value) { deliveryInstructions = value; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public List<OrderItem> getItems() { return items; }
    public List<OrderStatusHistory> getStatusHistory() { return statusHistory; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public OffsetDateTime getUpdatedAt() { return updatedAt; }
}
