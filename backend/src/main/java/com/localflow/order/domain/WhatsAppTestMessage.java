package com.localflow.order.domain;

import com.localflow.business.domain.Business;
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
import jakarta.persistence.Table;

import java.time.OffsetDateTime;

@Entity
@Table(name = "whatsapp_test_messages")
public class WhatsAppTestMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "business_id", nullable = false)
    private Business business;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "conversation_id", nullable = false)
    private WhatsAppTestConversation conversation;

    @Column(name = "message_id", nullable = false, length = 160)
    private String messageId;

    @Column(name = "reply_text", nullable = false, length = 2000)
    private String replyText;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private WhatsAppTestConversationState state;

    @Column(name = "order_id")
    private Long orderId;

    @Column(name = "order_number", length = 32)
    private String orderNumber;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @PrePersist
    void prePersist() { createdAt = OffsetDateTime.now(); }

    public Long getId() { return id; }
    public Business getBusiness() { return business; }
    public void setBusiness(Business business) { this.business = business; }
    public WhatsAppTestConversation getConversation() { return conversation; }
    public void setConversation(WhatsAppTestConversation conversation) { this.conversation = conversation; }
    public String getMessageId() { return messageId; }
    public void setMessageId(String messageId) { this.messageId = messageId; }
    public String getReplyText() { return replyText; }
    public void setReplyText(String replyText) { this.replyText = replyText; }
    public WhatsAppTestConversationState getState() { return state; }
    public void setState(WhatsAppTestConversationState state) { this.state = state; }
    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }
    public String getOrderNumber() { return orderNumber; }
    public void setOrderNumber(String orderNumber) { this.orderNumber = orderNumber; }
}
