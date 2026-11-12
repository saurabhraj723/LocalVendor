package com.localflow.order.domain;

import com.localflow.business.domain.Business;
import com.localflow.customer.domain.Customer;
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
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "whatsapp_test_conversations")
public class WhatsAppTestConversation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "business_id", nullable = false)
    private Business business;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private WhatsAppTestConversationState state;

    @Column(name = "product_id")
    private Long productId;

    @Column(name = "selected_address_id")
    private Long selectedAddressId;

    @Column(precision = 12, scale = 3)
    private BigDecimal quantity;

    @Column(name = "quoted_unit_price", precision = 12, scale = 2)
    private BigDecimal quotedUnitPrice;

    @Column(name = "order_id")
    private Long orderId;

    @Column(name = "order_number", length = 32)
    private String orderNumber;

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

    public Long getId() { return id; }
    public Business getBusiness() { return business; }
    public void setBusiness(Business business) { this.business = business; }
    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }
    public WhatsAppTestConversationState getState() { return state; }
    public void setState(WhatsAppTestConversationState state) { this.state = state; }
    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
    public Long getSelectedAddressId() { return selectedAddressId; }
    public void setSelectedAddressId(Long selectedAddressId) { this.selectedAddressId = selectedAddressId; }
    public BigDecimal getQuantity() { return quantity; }
    public void setQuantity(BigDecimal quantity) { this.quantity = quantity; }
    public BigDecimal getQuotedUnitPrice() { return quotedUnitPrice; }
    public void setQuotedUnitPrice(BigDecimal quotedUnitPrice) { this.quotedUnitPrice = quotedUnitPrice; }
    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }
    public String getOrderNumber() { return orderNumber; }
    public void setOrderNumber(String orderNumber) { this.orderNumber = orderNumber; }
}
