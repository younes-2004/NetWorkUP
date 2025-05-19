  
// src/features/chatbot/components/Chatbot/Chatbot.tsx
import { FormEvent, useEffect, useRef, useState } from "react";
import { useAuthentication } from "../../../authentication/contexts/AuthenticationContextProvider";
import { chatbotService, IChatMessage } from "../../services/chatbotService";
import classes from "./Chatbot.module.scss";

interface IChatbotProps {
  isOpen: boolean;
  onClose: () => void;
}

export function Chatbot({ isOpen, onClose }: IChatbotProps) {
  const [messages, setMessages] = useState<IChatMessage[]>([]);
  const [inputMessage, setInputMessage] = useState("");
  const [isLoading, setIsLoading] = useState(false);
  const [sessionId, setSessionId] = useState<string | null>(null);
  const [error, setError] = useState<string | null>(null);
  const messagesEndRef = useRef<HTMLDivElement>(null);
  const { user } = useAuthentication();

  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
  };

  useEffect(() => {
    scrollToBottom();
  }, [messages]);

  useEffect(() => {
    if (isOpen && messages.length === 0) {
      // Message d'accueil personnalisé
      const welcomeMessage: IChatMessage = {
        id: 0,
        content: `Hello ${user?.firstName}! I'm your professional assistant. How can I help you today with your career development or networking goals?`,
        role: 'ASSISTANT',
        timestamp: new Date().toISOString()
      };
      setMessages([welcomeMessage]);
    }
  }, [isOpen, user?.firstName, messages.length]);

  const handleSubmit = async (e: FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    if (!inputMessage.trim() || isLoading) return;

    const userMessage: IChatMessage = {
      id: Date.now(),
      content: inputMessage,
      role: 'USER',
      timestamp: new Date().toISOString()
    };

    setMessages(prev => [...prev, userMessage]);
    setInputMessage("");
    setIsLoading(true);
    setError(null);

    try {
      const response = await chatbotService.sendMessage(inputMessage, sessionId || undefined);
      
      const assistantMessage: IChatMessage = {
        id: Date.now() + 1,
        content: response.response,
        role: 'ASSISTANT',
        timestamp: response.timestamp
      };

      setMessages(prev => [...prev, assistantMessage]);
      setSessionId(response.sessionId);
    } catch (error) {
      setError(error instanceof Error ? error.message : "Une erreur est survenue");
      console.error('Chat error:', error);
    } finally {
      setIsLoading(false);
    }
  };

  const handleNewChat = () => {
    const welcomeMessage: IChatMessage = {
      id: 0,
      content: `Hello ${user?.firstName}! I'm your professional assistant. How can I help you today?`,
      role: 'ASSISTANT',
      timestamp: new Date().toISOString()
    };
    setMessages([welcomeMessage]);
    setSessionId(null);
    setError(null);
  };

  const formatTime = (dateString: string) => {
    return new Date(dateString).toLocaleTimeString();
  };

  if (!isOpen) return null;

  return (
    <div className={classes.overlay}>
      <div className={classes.container}>
        <div className={classes.header}>
          <div className={classes.title}>
            <h3>Professional Assistant</h3>
            <span className={classes.status}>
              {isLoading ? 'Typing...' : 'Online'}
            </span>
          </div>
          <div className={classes.actions}>
            <button 
              className={classes.action}
              onClick={handleNewChat}
              title="New conversation"
            >
              🔄
            </button>
            <button 
              className={classes.action}
              onClick={onClose}
              title="Close chat"
            >
              ✕
            </button>
          </div>
        </div>

        {error && (
          <div className={classes.error}>
            <span>⚠️ {error}</span>
            <button onClick={() => setError(null)}>✕</button>
          </div>
        )}

        <div className={classes.messages}>
          {messages.map((message) => (
            <div 
              key={message.id} 
              className={`${classes.message} ${classes[message.role.toLowerCase()]}`}
            >
              <div className={classes.content}>
                {message.content}
              </div>
              <div className={classes.timestamp}>
                {formatTime(message.timestamp)}
              </div>
            </div>
          ))}
          {isLoading && (
            <div className={`${classes.message} ${classes.assistant}`}>
              <div className={classes.typing}>
                <span></span>
                <span></span>
                <span></span>
              </div>
            </div>
          )}
          <div ref={messagesEndRef} />
        </div>

        <form className={classes.inputForm} onSubmit={handleSubmit}>
          <input
            type="text"
            value={inputMessage}
            onChange={(e) => setInputMessage(e.target.value)}
            placeholder="Type your message..."
            className={classes.input}
            disabled={isLoading}
          />
          <button 
            type="submit" 
            className={classes.send}
            disabled={!inputMessage.trim() || isLoading}
          >
            {isLoading ? '⏳' : '➤'}
          </button>
        </form>
      </div>
    </div>
  );
}