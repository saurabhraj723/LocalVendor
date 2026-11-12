package com.localflow.order.api;

import com.localflow.order.api.dto.WhatsAppConversationMessageRequest;
import com.localflow.order.api.dto.WhatsAppConversationResponse;
import com.localflow.order.api.dto.WhatsAppConversationStartRequest;
import com.localflow.order.service.WhatsAppConversationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/whatsapp/test/conversations")
public class WhatsAppTestConversationController {

    private final WhatsAppConversationService conversationService;

    public WhatsAppTestConversationController(WhatsAppConversationService conversationService) {
        this.conversationService = conversationService;
    }

    @PostMapping
    public ResponseEntity<WhatsAppConversationResponse> start(
            @Valid @RequestBody WhatsAppConversationStartRequest request
    ) {
        WhatsAppConversationResponse response = conversationService.start(request);
        return ResponseEntity.status(response.replayed() ? HttpStatus.OK : HttpStatus.CREATED).body(response);
    }

    @PostMapping("/{conversationId}/messages")
    public WhatsAppConversationResponse message(
            @PathVariable Long conversationId,
            @Valid @RequestBody WhatsAppConversationMessageRequest request
    ) {
        return conversationService.message(conversationId, request);
    }
}
