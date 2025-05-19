import { Outlet } from "react-router-dom";
import { WebSocketContextProvider } from "../../features/ws/WebSocketContextProvider";
import { ChatbotToggle } from "../../features/chatbot/components/ChatbotToggle/ChatbotToggle";
import { Header } from "../Header/Header";
import classes from "./ApplicationLayout.module.scss";

export function ApplicationLayout() {
  return (
    <WebSocketContextProvider>
      <div className={classes.root}>
        <Header />
        <main className={classes.container}>
          <Outlet />
        </main>
        {/* Chatbot flottant disponible sur toutes les pages */}
        <ChatbotToggle />
      </div>
    </WebSocketContextProvider>
  );
}