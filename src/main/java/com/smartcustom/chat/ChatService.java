package com.smartcustom.service;

import com.smartcustom.model.ChatSession;
import com.smartcustom.model.dto.ChatRequest;
import com.smartcustom.model.dto.ChatResponse;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 聊天服务接口
 * 
 * @author SmartCustom Team
 */
public interface ChatService {
    
    /**
     * 处理聊天请求
     * 
     * @param request 聊天请求
     * @return 聊天响应
     */
    ChatResponse chat(ChatRequest request);
    
    /**
     * 异步处理聊天请求
     * 
     * @param request 聊天请求
     * @return 异步聊天响应
     */
    CompletableFuture<ChatResponse> chatAsync(ChatRequest request);
    
    /**
     * 获取会话
     * 
     * @param sessionId 会话ID
     * @return 聊天会话
     */
    ChatSession getSession(String sessionId);
    
    /**
     * 获取用户的所有会话
     * 
     * @param userId 用户ID
     * @return 会话列表
     */
    List<ChatSession> getUserSessions(String userId);
    
    /**
     * 创建新会话
     * 
     * @param userId 用户ID
     * @param title 会话标题
     * @return 新创建的会话
     */
    ChatSession createSession(String userId, String title);
    
    /**
     * 删除会话
     * 
     * @param sessionId 会话ID
     * @return 是否成功删除
     */
    boolean deleteSession(String sessionId);
    
    /**
     * 清理过期会话
     */
    void cleanupExpiredSessions();
}