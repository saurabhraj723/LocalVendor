package com.localflow.order.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record WhatsAppConversationMessageRequest(
        @NotBlank @Size(max = 160) String messageId,
        @NotBlank @Size(max = 1000) String text
) { }
