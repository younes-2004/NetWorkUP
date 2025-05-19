 
// src/features/chatbot/services/chatbotService.ts
import { request } from "../../../utils/api";

export interface IChatRequest {
  message: string;
  sessionId?: string;
}

export interface IChatMessage {
  id: number;
  content: string;
  role: 'USER' | 'ASSISTANT' | 'SYSTEM';
  timestamp: string;
}

export interface IChatConversation {
  id: number;
  sessionId: string;
  createdAt: string;
  lastMessageAt: string;
}

export interface IChatResponse {
  response: string;
  sessionId: string;
  timestamp: string;
  conversationHistory: IChatMessage[];
}

export const chatbotService = {
  sendMessage: async (
    message: string, 
    sessionId?: string
  ): Promise<IChatResponse> => {
    return new Promise((resolve, reject) => {
      request<IChatResponse>({
        endpoint: '/api/v1/chatbot/chat',
        method: 'POST',
        body: JSON.stringify({ message, sessionId }),
        onSuccess: resolve,
        onFailure: (error) => reject(new Error(error))
      });
    });
  },

  getUserConversations: async (): Promise<IChatConversation[]> => {
    return new Promise((resolve, reject) => {
      request<IChatConversation[]>({
        endpoint: '/api/v1/chatbot/conversations',
        onSuccess: resolve,
        onFailure: (error) => reject(new Error(error))
      });
    });
  },

  getConversationMessages: async (sessionId: string): Promise<IChatMessage[]> => {
    return new Promise((resolve, reject) => {
      request<IChatMessage[]>({
        endpoint: `/api/v1/chatbot/conversations/${sessionId}/messages`,
        onSuccess: resolve,
        onFailure: (error) => reject(new Error(error))
      });
    });
  }
};