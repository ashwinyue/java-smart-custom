package com.smartcustom.controller;

import com.smartcustom.model.dto.ChatRequest;
import com.smartcustom.model.dto.ChatResponse;
import com.smartcustom.service.ChatService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 聊天API控制器
 * 
 * @author SmartCustom Team
 */
@RestController
@RequestMapping("/chat")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ChatController {
    
    private final ChatService chatService;
    
    @Autowired
    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }
    
    /**
     * 发送聊天消息
     * 
     * @param request 聊天请求
     * @return 聊天响应
     */
    @PostMapping
    public ResponseEntity<ChatResponse> chat(@Valid @RequestBody ChatRequest request) {
        ChatResponse response = chatService.chat(request);
        if (response.getError() != null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
        return ResponseEntity.ok(response);
    }
    
    /**
     * 异步发送聊天消息
     * 
     * @param request 聊天请求
     * @return 异步聊天响应
     */
    @PostMapping("/async")
    public CompletableFuture<ResponseEntity<ChatResponse>> chatAsync(@Valid @RequestBody ChatRequest request) {
        return chatService.chatAsync(request)
                .thenApply(response -> {
                    if (response.getError() != null) {
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
                    }
                    return ResponseEntity.ok(response);
                });
    }
    
    /**
     * 获取会话
     * 
     * @param sessionId 会话ID
     * @return 聊天会话
     */
    @GetMapping("/sessions/{sessionId}")
    public ResponseEntity<?> getSession(@PathVariable String sessionId) {
        var session = chatService.getSession(sessionId);
        if (session == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(session);
    }
    
    /**
     * 获取用户的所有会话
     * 
     * @param userId 用户ID
     * @return 会话列表
     */
    @GetMapping("/sessions")
    public ResponseEntity<List<?>> getUserSessions(@RequestParam String userId) {
        List<?> sessions = chatService.getUserSessions(userId);
        return ResponseEntity.ok(sessions);
    }
    
    /**
     * 创建新会话
     * 
     * @param userId 用户ID
     * @param title 会话标题
     * @return 新创建的会话
     */
    @PostMapping("/sessions")
    public ResponseEntity<?> createSession(
            @RequestParam String userId,
            @RequestParam(required = false, defaultValue = "新对话") String title) {
        var session = chatService.createSession(userId, title);
        return ResponseEntity.ok(session);
    }
    
    /**
     * 删除会话
     * 
     * @param sessionId 会话ID
     * @return 操作结果
     */
    @DeleteMapping("/sessions/{sessionId}")
    public ResponseEntity<?> deleteSession(@PathVariable String sessionId) {
        boolean deleted = chatService.deleteSession(sessionId);
        if (deleted) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * 清理过期会话
     * 
     * @return 操作结果
     */
    @PostMapping("/cleanup")
    public ResponseEntity<?> cleanupExpiredSessions() {
        chatService.cleanupExpiredSessions();
        return ResponseEntity.ok().build();
    }
}