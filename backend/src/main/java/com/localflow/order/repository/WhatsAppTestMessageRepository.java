package com.localflow.order.repository;

import com.localflow.order.domain.WhatsAppTestMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WhatsAppTestMessageRepository extends JpaRepository<WhatsAppTestMessage, Long> {
    Optional<WhatsAppTestMessage> findByBusiness_IdAndMessageId(Long businessId, String messageId);
}
