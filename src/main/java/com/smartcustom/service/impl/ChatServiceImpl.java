package com.smartcustom.service.impl;

import com.smartcustom.config.SmartCustomProperties;
import com.smartcustom.model.ChatMessage;
import com.smartcustom.model.ChatSession;
import com.smartcustom.model.dto.ChatRequest;
import com.smartcustom.model.dto.ChatResponse;
import com.smartcustom.service.ChatService;
import com.smartcustom.service.ToolManager;
import com.smartcustom.tool.ToolResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.PromptChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 聊天服务实现类
 * 
 * LangGraph功能在Spring AI中的体现：
 * 1. 状态管理 - 通过ChatMemory和ChatSession实现对话状态的持久化和管理
 * 2. 节点流程 - 通过Advisor链实现处理流程的编排，如MessageChatMemoryAdvisor
 * 3. 条件路由 - 可以基于不同条件选择不同的处理路径
 * 4. 工具调用 - 通过FunctionCallback集成外部工具，类似于LangGraph的工具节点
 * 5. 记忆管理 - 通过ChatMemory实现短期和长期记忆，支持上下文保持
 * 
 * @author SmartCustom Team
 */
@Service
public class ChatServiceImpl implements ChatService {
    
    private static final Logger logger = LoggerFactory.getLogger(ChatServiceImpl.class);
    
    private final ChatClient chatClient;
    private final ChatMemory chatMemory;
    private final SmartCustomProperties properties;
    private final ToolManager toolManager;
    
    // 使用内存存储会话（生产环境应使用数据库）
    private final Map<String, ChatSession> sessionStore = new ConcurrentHashMap<>();
    private final Map<String, List<String>> userSessions = new ConcurrentHashMap<>();
    
    @Autowired
    public ChatServiceImpl(ChatClient chatClient, ChatMemory chatMemory, 
                          SmartCustomProperties properties, ToolManager toolManager) {
        this.chatClient = chatClient;
        this.chatMemory = chatMemory;
        this.properties = properties;
        this.toolManager = toolManager;
    }
    
    @Override
    public ChatResponse chat(ChatRequest request) {
        try {
            // 获取或创建会话
            String sessionId = request.getSessionId();
            ChatSession session = sessionId != null ? getSession(sessionId) : null;
            
            if (session == null) {
                session = createSession(request.getUserId(), "新对话");
                sessionId = session.getSessionId();
            }
            
            // 添加用户消息到会话
            ChatMessage userMessage = new ChatMessage(
                sessionId, 
                ChatMessage.MessageType.USER, 
                request.getMessage()
            );
            session.addMessage(userMessage);
            
            // 使用Spring AI生成响应
            String conversationId = sessionId; // 使用sessionId作为conversationId
            // 添加null检查
            int maxHistory = properties != null && properties.getChat() != null ? 
                             properties.getChat().getMaxHistory() : 20;
            MessageChatMemoryAdvisor advisor = new MessageChatMemoryAdvisor(chatMemory, conversationId, maxHistory);
            
            // 添加用户消息到记忆
            chatMemory.add(conversationId, new UserMessage(request.getMessage()));
            
            String responseContent = chatClient
                .prompt()
                .user(request.getMessage())
                .advisors(advisor)
                .call()
                .content();
            
            // 添加助手回复到记忆
            chatMemory.add(conversationId, new AssistantMessage(responseContent));
            
            // 创建助手消息
            ChatMessage assistantMessage = new ChatMessage(
                sessionId,
                ChatMessage.MessageType.ASSISTANT,
                responseContent
            );
            session.addMessage(assistantMessage);
            
            // 更新会话
            sessionStore.put(sessionId, session);
            
            // 返回响应
            return ChatResponse.fromChatMessage(assistantMessage);
            
        } catch (Exception e) {
            logger.error("处理聊天请求时出错", e);
            return ChatResponse.error(request.getSessionId(), "处理聊天请求时出错: " + e.getMessage());
        }
    }
    
    @Override
    @Async("taskExecutor")
    public CompletableFuture<ChatResponse> chatAsync(ChatRequest request) {
        return CompletableFuture.completedFuture(chat(request));
    }
    
    /**
     * 带工具调用的聊天方法 - 展示LangGraph中的工具调用功能
     * 
     * @param request 聊天请求
     * @param toolNames 要使用的工具名称列表
     * @return 聊天响应
     */
    public ChatResponse chatWithTools(ChatRequest request, List<String> toolNames) {
        try {
            // 获取或创建会话
            String sessionId = request.getSessionId();
            ChatSession session = sessionId != null ? getSession(sessionId) : null;
            
            if (session == null) {
                session = createSession(request.getUserId(), "新对话");
                sessionId = session.getSessionId();
            }
            
            // 添加用户消息到会话
            ChatMessage userMessage = new ChatMessage(
                sessionId, 
                ChatMessage.MessageType.USER, 
                request.getMessage()
            );
            session.addMessage(userMessage);
            
            // 创建工具回调函数列表
            List<String> functionNames = new ArrayList<>();
            if (toolNames != null && !toolNames.isEmpty()) {
                for (String toolName : toolNames) {
                    if (toolManager.hasTool(toolName)) {
                        functionNames.add(toolName);
                    }
                }
            }
            
            // 使用Spring AI生成响应，集成工具调用
            String conversationId = sessionId;
            // 添加null检查
            int maxHistory = properties != null && properties.getChat() != null ? 
                             properties.getChat().getMaxHistory() : 20;
            MessageChatMemoryAdvisor advisor = new MessageChatMemoryAdvisor(chatMemory, conversationId, maxHistory);
            
            String systemPrompt = "你是一个智能助手，可以使用提供的工具来帮助用户回答问题。";
            SystemPromptTemplate systemPromptTemplate = new SystemPromptTemplate(systemPrompt);
            
            String responseContent = chatClient
                .prompt()
                .system(systemPromptTemplate.render())
                .user(request.getMessage())
                .advisors(advisor)
                .functions(functionNames.toArray(new String[0]))
                .call()
                .content();
            
            // 创建助手消息
            ChatMessage assistantMessage = new ChatMessage(
                sessionId,
                ChatMessage.MessageType.ASSISTANT,
                responseContent
            );
            session.addMessage(assistantMessage);
            
            // 更新会话
            sessionStore.put(sessionId, session);
            
            // 返回响应
            return ChatResponse.fromChatMessage(assistantMessage);
            
        } catch (Exception e) {
            return ChatResponse.error(request.getSessionId(), "处理带工具的聊天请求时出错: " + e.getMessage());
        }
    }
    
    @Override
    @Cacheable(value = "chatSessions", key = "#sessionId")
    public ChatSession getSession(String sessionId) {
        return sessionStore.get(sessionId);
    }
    
    @Override
    public List<ChatSession> getUserSessions(String userId) {
        List<String> sessionIds = userSessions.get(userId);
        if (sessionIds == null) {
            return new ArrayList<>();
        }
        
        return sessionIds.stream()
                .map(sessionStore::get)
                .filter(session -> session != null && session.isActive())
                .toList();
    }
    
    @Override
    public ChatSession createSession(String userId, String title) {
        String sessionId = UUID.randomUUID().toString();
        ChatSession session = new ChatSession(userId, title);
        session.setMessages(new ArrayList<>());
        
        // 添加系统欢迎消息
        ChatMessage welcomeMessage = new ChatMessage(
            sessionId,
            ChatMessage.MessageType.SYSTEM,
            "您好！我是智能客服助手，有什么可以帮助您的吗？"
        );
        session.addMessage(welcomeMessage);
        
        // 存储会话
        sessionStore.put(sessionId, session);
        
        // 更新用户会话列表 - 添加null检查
        if (userId != null) {
            userSessions.computeIfAbsent(userId, k -> new ArrayList<>()).add(sessionId);
        }
        
        return session;
    }
    
    @Override
    @CacheEvict(value = "chatSessions", key = "#sessionId")
    public boolean deleteSession(String sessionId) {
        ChatSession session = sessionStore.remove(sessionId);
        if (session != null && session.getUserId() != null) {
            List<String> sessions = userSessions.get(session.getUserId());
            if (sessions != null) {
                sessions.remove(sessionId);
                if (sessions.isEmpty()) {
                    userSessions.remove(session.getUserId());
                }
            }
            return true;
        }
        return false;
    }
    
    @Override
    @Scheduled(fixedRate = 300000) // 每5分钟执行一次
    public void cleanupExpiredSessions() {
        // 添加null检查
        long sessionTimeout = properties != null && properties.getChat() != null ? 
                             properties.getChat().getSessionTimeout() : 3600000;
        LocalDateTime cutoffTime = LocalDateTime.now().minusSeconds(sessionTimeout / 1000);
        
        sessionStore.entrySet().removeIf(entry -> {
            ChatSession session = entry.getValue();
            if (session.getUpdatedAt().isBefore(cutoffTime)) {
                // 从用户会话列表中移除
                if (session.getUserId() != null) {
                    List<String> sessions = userSessions.get(session.getUserId());
                    if (sessions != null) {
                        sessions.remove(entry.getKey());
                        if (sessions.isEmpty()) {
                            userSessions.remove(session.getUserId());
                        }
                    }
                }
                return true;
            }
            return false;
        });
    }
}