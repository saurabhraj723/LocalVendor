package com.localflow.order.repository;

import com.localflow.order.domain.WhatsAppTestConversation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WhatsAppTestConversationRepository extends JpaRepository<WhatsAppTestConversation, Long> {
    Optional<WhatsAppTestConversation> findByIdAndBusiness_Id(Long id, Long businessId);
}
