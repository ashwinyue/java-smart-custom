package com.smartcustom.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.smartcustom.model.ChatMessage;

import java.time.LocalDateTime;

/**
 * 聊天响应DTO
 * 
 * @author SmartCustom Team
 */
public class ChatResponse {
    
    /**
     * 消息类型枚举
     */
    public enum MessageType {
        USER, ASSISTANT, SYSTEM, ERROR
    }
    
    /**
     * 会话ID
     */
    @JsonProperty("sessionId")
    private String sessionId;
    
    /**
     * 响应消息
     */
    @JsonProperty("message")
    private String message;
    
    /**
     * 消息ID
     */
    @JsonProperty("messageId")
    private String messageId;
    
    /**
     * 是否完成
     */
    @JsonProperty("done")
    private boolean done;
    
    /**
     * 时间戳
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonProperty("timestamp")
    private LocalDateTime timestamp;
    
    /**
     * 令牌使用情况
     */
    @JsonProperty("tokenUsage")
    private TokenUsage tokenUsage;
    
    /**
     * 错误信息（如果有）
     */
    @JsonProperty("error")
    private String error;
    
    /**
     * 消息类型
     */
    @JsonProperty("type")
    private MessageType type;
    
    /**
     * 令牌使用情况内部类
     */
    public static class TokenUsage {
        /**
         * 提示令牌数
         */
        @JsonProperty("promptTokens")
        private int promptTokens;
        
        /**
         * 生成令牌数
         */
        @JsonProperty("generationTokens")
        private int generationTokens;
        
        /**
         * 总令牌数
         */
        @JsonProperty("totalTokens")
        private int totalTokens;
        
        public TokenUsage() {
        }
        
        public TokenUsage(int promptTokens, int generationTokens, int totalTokens) {
            this.promptTokens = promptTokens;
            this.generationTokens = generationTokens;
            this.totalTokens = totalTokens;
        }
        
        public int getPromptTokens() {
            return promptTokens;
        }
        
        public void setPromptTokens(int promptTokens) {
            this.promptTokens = promptTokens;
        }
        
        public int getGenerationTokens() {
            return generationTokens;
        }
        
        public void setGenerationTokens(int generationTokens) {
            this.generationTokens = generationTokens;
        }
        
        public int getTotalTokens() {
            return totalTokens;
        }
        
        public void setTotalTokens(int totalTokens) {
            this.totalTokens = totalTokens;
        }
    }
    
    public ChatResponse() {
        this.timestamp = LocalDateTime.now();
        this.done = true;
    }
    
    public ChatResponse(String sessionId, String message, String messageId) {
        this();
        this.sessionId = sessionId;
        this.message = message;
        this.messageId = messageId;
    }
    
    public static ChatResponse fromChatMessage(ChatMessage chatMessage) {
        ChatResponse response = new ChatResponse();
        response.setSessionId(chatMessage.getSessionId());
        response.setMessage(chatMessage.getContent());
        response.setMessageId(chatMessage.getMessageId());
        response.setTimestamp(chatMessage.getTimestamp());
        
        if (chatMessage.getTokenUsage() != null) {
            TokenUsage tokenUsage = new TokenUsage();
            tokenUsage.setPromptTokens(chatMessage.getTokenUsage().getPromptTokens());
            tokenUsage.setGenerationTokens(chatMessage.getTokenUsage().getGenerationTokens());
            tokenUsage.setTotalTokens(chatMessage.getTokenUsage().getTotalTokens());
            response.setTokenUsage(tokenUsage);
        }
        
        return response;
    }
    
    public static ChatResponse error(String sessionId, String error) {
        ChatResponse response = new ChatResponse();
        response.setSessionId(sessionId);
        response.setError(error);
        response.setDone(true);
        response.setType(MessageType.ERROR);
        return response;
    }
    
    @JsonProperty("sessionId")
    public String getSessionId() {
        return sessionId;
    }
    
    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
    
    @JsonProperty("message")
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    @JsonProperty("messageId")
    public String getMessageId() {
        return messageId;
    }
    
    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }
    
    @JsonProperty("done")
    public boolean isDone() {
        return done;
    }
    
    public void setDone(boolean done) {
        this.done = done;
    }
    
    @JsonProperty("timestamp")
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
    
    @JsonProperty("tokenUsage")
    public TokenUsage getTokenUsage() {
        return tokenUsage;
    }
    
    public void setTokenUsage(TokenUsage tokenUsage) {
        this.tokenUsage = tokenUsage;
    }
    
    @JsonProperty("error")
    public String getError() {
        return error;
    }
    
    public void setError(String error) {
        this.error = error;
    }
    
    @JsonProperty("type")
    public MessageType getType() {
        return type;
    }
    
    public void setType(MessageType type) {
        this.type = type;
    }
    
    public boolean isError() {
        return error != null && !error.isEmpty();
    }
}