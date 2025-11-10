package com.smartcustom.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;

/**
 * 聊天请求DTO
 * 
 * @author SmartCustom Team
 */
public class ChatRequest {
    
    /**
     * 用户消息
     */
    @NotBlank(message = "消息内容不能为空")
    @JsonProperty("message")
    private String message;
    
    /**
     * 会话ID（可选，如果不提供则创建新会话）
     */
    @JsonProperty("sessionId")
    private String sessionId;
    
    /**
     * 用户ID（可选）
     */
    @JsonProperty("userId")
    private String userId;
    
    /**
     * 是否使用流式响应
     */
    @JsonProperty(value = "stream", defaultValue = "false")
    private boolean stream = false;
    
    /**
     * 模型参数
     */
    @JsonProperty("model")
    private String model = "qwen-plus";
    
    /**
     * 温度参数
     */
    @JsonProperty(value = "temperature", defaultValue = "0.7")
    private float temperature = 0.7f;
    
    /**
     * 最大令牌数
     */
    @JsonProperty(value = "maxTokens", defaultValue = "1500")
    private int maxTokens = 1500;
    
    public ChatRequest() {
    }
    
    public ChatRequest(String message) {
        this.message = message;
    }
    
    @JsonProperty("message")
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    @JsonProperty("sessionId")
    public String getSessionId() {
        return sessionId;
    }
    
    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
    
    @JsonProperty("userId")
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    @JsonProperty("stream")
    public boolean isStream() {
        return stream;
    }
    
    public void setStream(boolean stream) {
        this.stream = stream;
    }
    
    @JsonProperty("model")
    public String getModel() {
        return model;
    }
    
    public void setModel(String model) {
        this.model = model;
    }
    
    @JsonProperty("temperature")
    public float getTemperature() {
        return temperature;
    }
    
    public void setTemperature(float temperature) {
        this.temperature = temperature;
    }
    
    @JsonProperty("maxTokens")
    public int getMaxTokens() {
        return maxTokens;
    }
    
    public void setMaxTokens(int maxTokens) {
        this.maxTokens = maxTokens;
    }
}