  
// src/features/chatbot/hooks/useChatbot.ts
import { useEffect, useState } from "react";
import { useAuthentication } from "../../authentication/contexts/AuthenticationContextProvider";
import { chatbotService, IChatConversation, IChatMessage } from "../services/chatbotService";

export function useChatbot() {
  const [conversations, setConversations] = useState<IChatConversation[]>([]);
  const [currentMessages, setCurrentMessages] = useState<IChatMessage[]>([]);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const { user } = useAuthentication();

  // Charger les conversations de l'utilisateur
  const loadConversations = async () => {
    try {
      const conversations = await chatbotService.getUserConversations();
      setConversations(conversations);
    } catch (error) {
      setError(error instanceof Error ? error.message : "Error loading conversations");
    }
  };

  // Charger les messages d'une conversation
  const loadConversationMessages = async (sessionId: string) => {
    try {
      setIsLoading(true);
      const messages = await chatbotService.getConversationMessages(sessionId);
      setCurrentMessages(messages);
    } catch (error) {
      setError(error instanceof Error ? error.message : "Error loading messages");
    } finally {
      setIsLoading(false);
    }
  };

  // Envoyer un message
  const sendMessage = async (message: string, sessionId?: string) => {
    try {
      setIsLoading(true);
      setError(null);
      const response = await chatbotService.sendMessage(message, sessionId);
      
      // Ajouter les messages à l'état local
      const userMessage: IChatMessage = {
        id: Date.now(),
        content: message,
        role: 'USER',
        timestamp: new Date().toISOString()
      };
      
      const assistantMessage: IChatMessage = {
        id: Date.now() + 1,
        content: response.response,
        role: 'ASSISTANT',
        timestamp: response.timestamp
      };

      setCurrentMessages(prev => [...prev, userMessage, assistantMessage]);
      return response;
    } catch (error) {
      setError(error instanceof Error ? error.message : "Error sending message");
      throw error;
    } finally {
      setIsLoading(false);
    }
  };

  // Charger les conversations au montage du hook
  useEffect(() => {
    if (user) {
      loadConversations();
    }
  }, [user]);

  return {
    conversations,
    currentMessages,
    isLoading,
    error,
    loadConversations,
    loadConversationMessages,
    sendMessage,
    setCurrentMessages,
    setError
  };
}