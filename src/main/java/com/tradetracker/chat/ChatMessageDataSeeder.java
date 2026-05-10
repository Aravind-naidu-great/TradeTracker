package com.tradetracker.chat;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class ChatMessageDataSeeder implements CommandLineRunner {
    private final ChatMessageRepository chatMessageRepository;

    public ChatMessageDataSeeder(ChatMessageRepository chatMessageRepository) {
        this.chatMessageRepository = chatMessageRepository;
    }

    @Override
    public void run(String... args) {
        if (chatMessageRepository.count() > 0) {
            return;
        }

        chatMessageRepository.saveAll(List.of(
                message("Turmeric inquiry", "Golden Harvest Foods", "Sunrise Spice Exports", "Can you support 18 MT turmeric for Jebel Ali next week?", LocalDateTime.now().minusMinutes(32)),
                message("Turmeric inquiry", "Sunrise Spice Exports", "Golden Harvest Foods", "Yes, we have finger turmeric ready. CIF Jebel Ali is available.", LocalDateTime.now().minusMinutes(25)),
                message("Fabric sourcing", "EcoWear Sourcing Co.", "EcoWeave Mills", "Please share organic cotton lead time and GOTS certificate copy.", LocalDateTime.now().minusMinutes(14))
        ));
    }

    private ChatMessage message(
            String conversationName,
            String senderBusinessName,
            String recipientBusinessName,
            String messageText,
            LocalDateTime sentAt
    ) {
        ChatMessage message = new ChatMessage();
        message.setConversationName(conversationName);
        message.setSenderBusinessName(senderBusinessName);
        message.setRecipientBusinessName(recipientBusinessName);
        message.setMessageText(messageText);
        message.setSentAt(sentAt);
        return message;
    }
}
