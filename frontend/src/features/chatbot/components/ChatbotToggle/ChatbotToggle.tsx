  
// src/features/chatbot/components/ChatbotToggle/ChatbotToggle.tsx
import { useState } from "react";
import { Chatbot } from "../Chatbot/Chatbot";
import classes from "./ChatbotToggle.module.scss";

export function ChatbotToggle() {
  const [isChatOpen, setIsChatOpen] = useState(false);

  const toggleChat = () => {
    setIsChatOpen(!isChatOpen);
  };

  return (
    <>
      <button 
        className={`${classes.toggle} ${isChatOpen ? classes.open : ''}`}
        onClick={toggleChat}
        title={isChatOpen ? "Close AI Assistant" : "Open AI Assistant"}
      >
        {isChatOpen ? '✕' : '💬'}
      </button>
      
      <Chatbot 
        isOpen={isChatOpen} 
        onClose={() => setIsChatOpen(false)} 
      />
    </>
  );
}