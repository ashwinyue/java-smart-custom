package com.smartcustom.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring AI配置类
 * 
 * @author SmartCustom Team
 */
@Configuration
public class SpringAiConfig {
    
    /**
     * 配置聊天内存
     */
    @Bean
    public ChatMemory chatMemory() {
        return new InMemoryChatMemory();
    }
    
    /**
     * 配置聊天客户端
     */
    @Bean
    public ChatClient chatClient(ChatClient.Builder builder) {
        return builder
                .defaultSystem("你是一个智能客服助手，负责回答用户的问题并提供帮助。")
                .build();
    }
}