package com.localflow.order.repository;

import com.localflow.order.domain.WhatsAppTestCartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WhatsAppTestCartItemRepository extends JpaRepository<WhatsAppTestCartItem, Long> {
    List<WhatsAppTestCartItem> findAllByConversation_IdOrderByIdAsc(Long conversationId);
    Optional<WhatsAppTestCartItem> findByConversation_IdAndProduct_Id(Long conversationId, Long productId);
    void deleteByConversation_IdAndProduct_Id(Long conversationId, Long productId);
}
