package com.tradetracker.chat;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
public class ChatMessageController {
    private final ChatMessageRepository chatMessageRepository;

    public ChatMessageController(ChatMessageRepository chatMessageRepository) {
        this.chatMessageRepository = chatMessageRepository;
    }

    @GetMapping("/api/chat/messages")
    public List<ChatMessage> messages() {
        return chatMessageRepository.findAllByOrderBySentAtAsc();
    }

    @PostMapping("/api/chat/messages")
    @ResponseStatus(HttpStatus.CREATED)
    public ChatMessage createMessage(@Valid @RequestBody ChatMessageRequest request) {
        ChatMessage message = new ChatMessage();
        message.setConversationName(request.conversationName().trim());
        message.setSenderBusinessName(request.senderBusinessName().trim());
        message.setRecipientBusinessName(request.recipientBusinessName().trim());
        message.setMessageText(request.messageText().trim());
        message.setSentAt(LocalDateTime.now());
        return chatMessageRepository.save(message);
    }
}
