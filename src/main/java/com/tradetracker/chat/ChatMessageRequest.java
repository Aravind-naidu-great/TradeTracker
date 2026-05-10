package com.tradetracker.chat;

import jakarta.validation.constraints.NotBlank;

public record ChatMessageRequest(
        @NotBlank String conversationName,
        @NotBlank String senderBusinessName,
        @NotBlank String recipientBusinessName,
        @NotBlank String messageText
) {
}
