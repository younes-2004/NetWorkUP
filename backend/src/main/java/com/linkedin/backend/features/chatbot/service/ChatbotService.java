package com.linkedin.backend.features.chatbot.service;

import com.linkedin.backend.features.authentication.model.User;
import com.linkedin.backend.features.chatbot.dto.ChatResponseDto;
import com.linkedin.backend.features.chatbot.model.ChatConversation;
import com.linkedin.backend.features.chatbot.model.ApplicationChatMessage;
import com.linkedin.backend.features.chatbot.model.MessageRole;
import com.linkedin.backend.features.chatbot.repository.ChatConversationRepository;
import com.linkedin.backend.features.chatbot.repository.ChatMessageRepository;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
// ✅ Import de la classe OpenAI sans alias
import com.theokanning.openai.service.OpenAiService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class ChatbotService {

    private final OpenAiService openAiService;
    private final ChatConversationRepository conversationRepository;
    private final ChatMessageRepository messageRepository;

    @Value("${openai.api.model:gpt-3.5-turbo}")
    private String model;

    @Value("${openai.api.max-tokens:1000}")
    private Integer maxTokens;

    @Value("${openai.api.temperature:0.7}")
    private Double temperature;

    public ChatbotService(OpenAiService openAiService,
                          ChatConversationRepository conversationRepository,
                          ChatMessageRepository messageRepository) {
        this.openAiService = openAiService;
        this.conversationRepository = conversationRepository;
        this.messageRepository = messageRepository;
    }

    public ChatResponseDto sendMessage(User user, String message, String sessionId) {
        try {
            // Créer ou récupérer la conversation
            ChatConversation conversation = getOrCreateConversation(user, sessionId);

            // Sauvegarder le message de l'utilisateur
            ApplicationChatMessage userMessage = new ApplicationChatMessage(conversation, message, MessageRole.USER);
            messageRepository.save(userMessage);

            // Préparer l'historique pour OpenAI
            List<com.theokanning.openai.completion.chat.ChatMessage> openAiMessages = buildConversationHistory(conversation);

            // Créer la requête OpenAI
            ChatCompletionRequest chatRequest = ChatCompletionRequest.builder()
                    .model(model)
                    .messages(openAiMessages)
                    .maxTokens(maxTokens)
                    .temperature(temperature)
                    .build();

            // Appeler OpenAI
            String aiResponse = openAiService.createChatCompletion(chatRequest)
                    .getChoices().get(0).getMessage().getContent();

            // Sauvegarder la réponse de l'IA
            ApplicationChatMessage assistantMessage = new ApplicationChatMessage(conversation, aiResponse, MessageRole.ASSISTANT);
            messageRepository.save(assistantMessage);

            // Mettre à jour la conversation
            conversation.setLastMessageAt(LocalDateTime.now());
            conversationRepository.save(conversation);

            // Récupérer l'historique complet
            List<ApplicationChatMessage> conversationHistory = messageRepository
                    .findByConversationOrderByTimestampAsc(conversation);

            return new ChatResponseDto(
                    aiResponse,
                    conversation.getSessionId(),
                    LocalDateTime.now(),
                    conversationHistory
            );

        } catch (Exception e) {
            throw new RuntimeException("Error processing chat message: " + e.getMessage(), e);
        }
    }

    private ChatConversation getOrCreateConversation(User user, String sessionId) {
        if (sessionId != null) {
            return conversationRepository.findByUserAndSessionId(user, sessionId)
                    .orElseGet(() -> createNewConversation(user, sessionId));
        } else {
            return createNewConversation(user, UUID.randomUUID().toString());
        }
    }

    private ChatConversation createNewConversation(User user, String sessionId) {
        ChatConversation conversation = new ChatConversation(user, sessionId);

        // Ajouter un message système pour contextualiser l'IA
        String systemPrompt = createSystemPrompt(user);
        ApplicationChatMessage systemMessage = new ApplicationChatMessage(conversation, systemPrompt, MessageRole.SYSTEM);

        conversation = conversationRepository.save(conversation);
        systemMessage.setConversation(conversation);
        messageRepository.save(systemMessage);

        return conversation;
    }

    private String createSystemPrompt(User user) {
        return String.format(
                "You are an AI assistant for a professional networking platform (similar to LinkedIn). " +
                        "You are helping %s %s (position: %s at %s). " +
                        "Please provide helpful, professional advice related to career development, networking, " +
                        "and professional growth. Keep responses concise and actionable.",
                user.getFirstName() != null ? user.getFirstName() : "the user",
                user.getLastName() != null ? user.getLastName() : "",
                user.getPosition() != null ? user.getPosition() : "Professional",
                user.getCompany() != null ? user.getCompany() : "their company"
        );
    }

    // ✅ Utilisation du nom complet pour éviter la confusion
    private List<com.theokanning.openai.completion.chat.ChatMessage> buildConversationHistory(ChatConversation conversation) {
        List<ApplicationChatMessage> messages = messageRepository
                .findByConversationOrderByTimestampAsc(conversation);

        List<com.theokanning.openai.completion.chat.ChatMessage> openAiMessages = new ArrayList<>();

        for (ApplicationChatMessage msg : messages) {
            String role = switch (msg.getRole()) {
                case USER -> "user";
                case ASSISTANT -> "assistant";
                case SYSTEM -> "system";
            };
            // ✅ Utilisation du nom complet de la classe OpenAI
            openAiMessages.add(new com.theokanning.openai.completion.chat.ChatMessage(role, msg.getContent()));
        }

        return openAiMessages;
    }

    public List<ChatConversation> getUserConversations(User user) {
        return conversationRepository.findByUserOrderByLastMessageAtDesc(user);
    }

    public List<ApplicationChatMessage> getConversationMessages(String sessionId, User user) {
        ChatConversation conversation = conversationRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Conversation not found"));

        if (!conversation.getUser().getId().equals(user.getId())) {
            throw new SecurityException("User not authorized to access this conversation");
        }

        return messageRepository.findByConversationOrderByTimestampAsc(conversation);
    }
}