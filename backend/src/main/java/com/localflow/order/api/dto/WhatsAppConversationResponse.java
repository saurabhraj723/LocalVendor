package com.localflow.order.api.dto;

import com.localflow.order.domain.WhatsAppTestConversationState;

public record WhatsAppConversationResponse(
        Long conversationId,
        WhatsAppTestConversationState state,
        String reply,
        Long orderId,
        String orderNumber,
        boolean replayed
) { }
