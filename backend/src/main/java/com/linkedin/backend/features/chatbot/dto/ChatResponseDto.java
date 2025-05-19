
package com.linkedin.backend.features.chatbot.dto;

import com.linkedin.backend.features.chatbot.model.ApplicationChatMessage;

import java.time.LocalDateTime;
import java.util.List;

public record ChatResponseDto(
        String response,
        String sessionId,
        LocalDateTime timestamp,
        List<ApplicationChatMessage> conversationHistory
) {}