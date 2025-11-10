package com.smartcustom.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 聊天消息模型
 * 
 * @author SmartCustom Team
 */
public class ChatMessage {
    
    /**
     * 消息ID
     */
    private String messageId;
    
    /**
     * 会话ID
     */
    private String sessionId;
    
    /**
     * 消息类型：USER, ASSISTANT, SYSTEM
     */
    private MessageType type;
    
    /**
     * 消息内容
     */
    private String content;
    
    /**
     * 消息时间戳
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;
    
    /**
     * 是否已读
     */
    private boolean read;
    
    /**
     * 令牌使用数量
     */
    private TokenUsage tokenUsage;
    
    /**
     * 消息类型枚举
     */
    public enum MessageType {
        @JsonProperty("USER")
        USER,
        
        @JsonProperty("ASSISTANT")
        ASSISTANT,
        
        @JsonProperty("SYSTEM")
        SYSTEM
    }
    
    /**
     * 令牌使用情况
     */
    public static class TokenUsage {
        /**
         * 提示令牌数
         */
        private int promptTokens;
        
        /**
         * 生成令牌数
         */
        private int generationTokens;
        
        /**
         * 总令牌数
         */
        private int totalTokens;
        
        public TokenUsage() {
        }
        
        public TokenUsage(int promptTokens, int generationTokens, int totalTokens) {
            this.promptTokens = promptTokens;
            this.generationTokens = generationTokens;
            this.totalTokens = totalTokens;
        }
        
        @JsonProperty("promptTokens")
        public int getPromptTokens() {
            return promptTokens;
        }
        
        public void setPromptTokens(int promptTokens) {
            this.promptTokens = promptTokens;
        }
        
        @JsonProperty("generationTokens")
        public int getGenerationTokens() {
            return generationTokens;
        }
        
        public void setGenerationTokens(int generationTokens) {
            this.generationTokens = generationTokens;
        }
        
        @JsonProperty("totalTokens")
        public int getTotalTokens() {
            return totalTokens;
        }
        
        public void setTotalTokens(int totalTokens) {
            this.totalTokens = totalTokens;
        }
    }
    
    public ChatMessage() {
        this.messageId = UUID.randomUUID().toString();
        this.timestamp = LocalDateTime.now();
        this.read = false;
    }
    
    public ChatMessage(String sessionId, MessageType type, String content) {
        this();
        this.sessionId = sessionId;
        this.type = type;
        this.content = content;
    }
    
    @JsonProperty("messageId")
    public String getMessageId() {
        return messageId;
    }
    
    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }
    
    @JsonProperty("sessionId")
    public String getSessionId() {
        return sessionId;
    }
    
    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
    
    @JsonProperty("type")
    public MessageType getType() {
        return type;
    }
    
    public void setType(MessageType type) {
        this.type = type;
    }
    
    @JsonProperty("content")
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    @JsonProperty("timestamp")
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
    
    @JsonProperty("read")
    public boolean isRead() {
        return read;
    }
    
    public void setRead(boolean read) {
        this.read = read;
    }
    
    @JsonProperty("tokenUsage")
    public TokenUsage getTokenUsage() {
        return tokenUsage;
    }
    
    public void setTokenUsage(TokenUsage tokenUsage) {
        this.tokenUsage = tokenUsage;
    }
    
    /**
     * 标记消息为已读
     */
    public void markAsRead() {
        this.read = true;
    }
}