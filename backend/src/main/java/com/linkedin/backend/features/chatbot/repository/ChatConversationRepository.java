
package com.linkedin.backend.features.chatbot.repository;

import com.linkedin.backend.features.authentication.model.User;
import com.linkedin.backend.features.chatbot.model.ChatConversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatConversationRepository extends JpaRepository<ChatConversation, Long> {
    Optional<ChatConversation> findByUserAndSessionId(User user, String sessionId);
    List<ChatConversation> findByUserOrderByLastMessageAtDesc(User user);
    Optional<ChatConversation> findBySessionId(String sessionId);
}