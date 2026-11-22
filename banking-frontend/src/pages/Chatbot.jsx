import API from "../api";
import { useState, useRef, useEffect } from "react";
import { useNavigate } from "react-router-dom";

function Chatbot() {
  const navigate = useNavigate();
  const fullName = localStorage.getItem("fullName") || "there";
  const chatMessagesRef = useRef(null);

  const [question, setQuestion] = useState("");
  const [messages, setMessages] = useState([
    {
      role: "assistant",
      content:
        "Hello! 👋 I'm your personal Banking AI Assistant. I can help you understand your finances, check account balances, analyze income and expenses, track savings, and answer your banking questions. What would you like to know today?",
    },
  ]);
  const [loading, setLoading] = useState(false);
  useEffect(() => {
        if (chatMessagesRef.current) {
            chatMessagesRef.current.scrollTo({
            top: chatMessagesRef.current.scrollHeight,
            behavior: "smooth",
            });
        }
        }, [messages, loading]);

  const handleSend = async (e) => {
    e.preventDefault();

    if (!question.trim() || loading) return;

    const userQuestion = question.trim();

    setMessages((prev) => [
      ...prev,
      { role: "user", content: userQuestion },
    ]);

    setQuestion("");
    setLoading(true);

    try {
      const token = localStorage.getItem("token");

      const response = await API.post(
                                "/api/chat/ask",
        { question: userQuestion }, 
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );

      setMessages((prev) => [
        ...prev,
        {
          role: "assistant",
          content: response.data.reply,
        },
      ]);
    } catch (error) {
      if (error.response?.status === 401) {
        localStorage.removeItem("token");
        navigate("/");
        return;
      }

      setMessages((prev) => [
        ...prev,
        {
          role: "assistant",
          content: "Sorry, something went wrong. Please try again.",
        },
      ]);
    } finally {
      setLoading(false);
    }
  };

  const handleLogout = () => {
    localStorage.removeItem("token");
    localStorage.removeItem("fullName");
    navigate("/");
  };

  return (
    <div className="chat-container">
      <header className="chat-header">
        <div>
          <h2>Banking AI Assistant 🤖</h2>
          <p>Your personal financial assistant</p>
        </div>

        <button onClick={handleLogout} className="logout-button">
          Logout
        </button>
      </header>

      <main className="chat-main">
        <div className="welcome-section">
          <h1>Welcome back, {fullName}! 👋</h1>
          <p>How can I help you with your banking today?</p>
        </div>

        <div className="chat-messages" ref={chatMessagesRef}>
          
            {messages.map((message, index) => (
            <div
                key={index}
                className={`message ${message.role}`}
            >
                <div className="message-avatar">
                {message.role === "assistant" ? "🤖" : "👤"}
                </div>

                <div className="message-body">
                <div className="message-label">
                    {message.role === "assistant" ? "Banking AI" : "You"}
                </div>

                <div className="message-content">
                    {message.content}
                </div>
                </div>
            </div>
            ))}


          {loading && (
            <div className="message assistant">
                <div className="message-avatar">🤖</div>

                <div className="message-body">
                <div className="message-label">Banking AI</div>

                <div className="message-content">
                    Analyzing your banking information... ⏳
                </div>
                </div>
            </div>
            )}
        </div>

        <form className="chat-input-form" onSubmit={handleSend}>
          <input
            type="text"
            placeholder="Ask me anything about your banking..."
            value={question}
            onChange={(e) => setQuestion(e.target.value)}
            disabled={loading}
          />

          <button type="submit" disabled={loading || !question.trim()}>
            Send
          </button>
        </form>
      </main>
    </div>
  );
}

export default Chatbot;
