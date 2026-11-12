package com.localflow.order.domain;

import com.localflow.catalogue.domain.Product;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.math.BigDecimal;

@Entity
@Table(name = "whatsapp_test_cart_items", uniqueConstraints =
        @UniqueConstraint(name = "uq_whatsapp_test_cart_product", columnNames = {"conversation_id", "product_id"}))
public class WhatsAppTestCartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "conversation_id", nullable = false)
    private WhatsAppTestConversation conversation;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false, precision = 12, scale = 3)
    private BigDecimal quantity;

    @Column(name = "quoted_unit_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal quotedUnitPrice;

    @Column(name = "quoted_discount_percent", precision = 5, scale = 2)
    private BigDecimal quotedDiscountPercent;

    @Column(name = "quoted_tax_type", length = 24)
    private String quotedTaxType;

    @Column(name = "quoted_tax_percent", precision = 5, scale = 2)
    private BigDecimal quotedTaxPercent;

    public Long getId() { return id; }
    public WhatsAppTestConversation getConversation() { return conversation; }
    public void setConversation(WhatsAppTestConversation conversation) { this.conversation = conversation; }
    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }
    public BigDecimal getQuantity() { return quantity; }
    public void setQuantity(BigDecimal quantity) { this.quantity = quantity; }
    public BigDecimal getQuotedUnitPrice() { return quotedUnitPrice; }
    public void setQuotedUnitPrice(BigDecimal quotedUnitPrice) { this.quotedUnitPrice = quotedUnitPrice; }
    public BigDecimal getQuotedDiscountPercent() { return quotedDiscountPercent; }
    public void setQuotedDiscountPercent(BigDecimal quotedDiscountPercent) { this.quotedDiscountPercent = quotedDiscountPercent; }
    public String getQuotedTaxType() { return quotedTaxType; }
    public void setQuotedTaxType(String quotedTaxType) { this.quotedTaxType = quotedTaxType; }
    public BigDecimal getQuotedTaxPercent() { return quotedTaxPercent; }
    public void setQuotedTaxPercent(BigDecimal quotedTaxPercent) { this.quotedTaxPercent = quotedTaxPercent; }
}
