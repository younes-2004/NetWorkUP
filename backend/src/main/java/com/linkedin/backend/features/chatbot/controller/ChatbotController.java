
package com.linkedin.backend.features.chatbot.controller;

import com.linkedin.backend.features.authentication.model.User;
import com.linkedin.backend.features.chatbot.dto.ChatRequestDto;
import com.linkedin.backend.features.chatbot.dto.ChatResponseDto;
import com.linkedin.backend.features.chatbot.model.ChatConversation;
import com.linkedin.backend.features.chatbot.model.ApplicationChatMessage;
import com.linkedin.backend.features.chatbot.service.ChatbotService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/chatbot")
public class ChatbotController {

    private final ChatbotService chatbotService;

    public ChatbotController(ChatbotService chatbotService) {
        this.chatbotService = chatbotService;
    }

    @PostMapping("/chat")
    public ResponseEntity<ChatResponseDto> sendMessage(
            @RequestAttribute("authenticatedUser") User user,
            @Valid @RequestBody ChatRequestDto request) {

        ChatResponseDto response = chatbotService.sendMessage(
                user,
                request.message(),
                request.sessionId()
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/conversations")
    public ResponseEntity<List<ChatConversation>> getUserConversations(
            @RequestAttribute("authenticatedUser") User user) {

        List<ChatConversation> conversations = chatbotService.getUserConversations(user);
        return ResponseEntity.ok(conversations);
    }

    @GetMapping("/conversations/{sessionId}/messages")
    public ResponseEntity<List<ApplicationChatMessage>> getConversationMessages(
            @RequestAttribute("authenticatedUser") User user,
            @PathVariable String sessionId) {

        List<ApplicationChatMessage> messages = chatbotService.getConversationMessages(sessionId, user);
        return ResponseEntity.ok(messages);
    }
}