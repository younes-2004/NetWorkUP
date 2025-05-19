
package com.linkedin.backend.features.chatbot.dto;

import jakarta.validation.constraints.NotBlank;

public record ChatRequestDto(
        @NotBlank(message = "Message is mandatory") String message,
        String sessionId
) {}