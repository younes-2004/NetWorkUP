
package com.linkedin.backend.features.chatbot.repository;

import com.linkedin.backend.features.chatbot.model.ChatConversation;
import com.linkedin.backend.features.chatbot.model.ApplicationChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ApplicationChatMessage, Long> {
    List<ApplicationChatMessage> findByConversationOrderByTimestampAsc(ChatConversation conversation);
}