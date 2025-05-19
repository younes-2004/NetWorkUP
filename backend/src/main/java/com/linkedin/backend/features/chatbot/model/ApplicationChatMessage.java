package com.linkedin.backend.features.chatbot.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity(name = "chat_messages")
public class ApplicationChatMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "conversation_id", nullable = false)
    @JsonIgnore
    private ChatConversation conversation;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    private MessageRole role; // USER, ASSISTANT, SYSTEM

    @CreationTimestamp
    private LocalDateTime timestamp;

    // Constructors
    public ApplicationChatMessage() {}

    public ApplicationChatMessage(ChatConversation conversation, String content, MessageRole role) {
        this.conversation = conversation;
        this.content = content;
        this.role = role;
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public ChatConversation getConversation() { return conversation; }
    public void setConversation(ChatConversation conversation) { this.conversation = conversation; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public MessageRole getRole() { return role; }
    public void setRole(MessageRole role) { this.role = role; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}