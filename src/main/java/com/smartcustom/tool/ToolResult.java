package com.smartcustom.tool;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 工具执行结果
 * 
 * @author SmartCustom Team
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ToolResult {
    
    private boolean success;
    private String message;
    private Object data;
    private String error;
    private Map<String, Object> metadata;
    private LocalDateTime timestamp;
    
    public ToolResult() {
        this.timestamp = LocalDateTime.now();
        this.metadata = new HashMap<>();
    }
    
    public ToolResult(boolean success, String message) {
        this();
        this.success = success;
        this.message = message;
    }
    
    public ToolResult(boolean success, String message, Object data) {
        this(success, message);
        this.data = data;
    }
    
    public static ToolResult success(String message) {
        return new ToolResult(true, message);
    }
    
    public static ToolResult success(String message, Object data) {
        return new ToolResult(true, message, data);
    }
    
    public static ToolResult error(String error) {
        ToolResult result = new ToolResult(false, null);
        result.setError(error);
        return result;
    }
    
    public boolean isSuccess() {
        return success;
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public Object getData() {
        return data;
    }
    
    public void setData(Object data) {
        this.data = data;
    }
    
    public String getError() {
        return error;
    }
    
    public void setError(String error) {
        this.error = error;
    }
    
    public Map<String, Object> getMetadata() {
        return metadata;
    }
    
    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
    
    public ToolResult addMetadata(String key, Object value) {
        this.metadata.put(key, value);
        return this;
    }
}