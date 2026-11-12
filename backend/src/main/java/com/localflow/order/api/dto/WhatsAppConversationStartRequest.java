package com.localflow.order.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record WhatsAppConversationStartRequest(
        @NotBlank @Size(max = 160) String messageId,
        @NotBlank @Size(max = 32) String customerPhone,
        @NotBlank @Size(max = 1000) String text
) { }
