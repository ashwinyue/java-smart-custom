package com.smartcustom.config;

import com.smartcustom.service.ToolManager;
import com.smartcustom.tool.Tool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * 工具自动配置类
 * 
 * @author SmartCustom Team
 */
@Configuration
public class ToolAutoConfiguration implements ApplicationListener<ApplicationReadyEvent> {
    
    private final ToolManager toolManager;
    private final List<Tool> tools;
    
    @Autowired
    public ToolAutoConfiguration(ToolManager toolManager, List<Tool> tools) {
        this.toolManager = toolManager;
        this.tools = tools;
    }
    
    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        // 自动注册所有实现了Tool接口的Bean
        for (Tool tool : tools) {
            boolean registered = toolManager.registerTool(tool);
            if (registered) {
                System.out.println("工具注册成功: " + tool.getName());
            } else {
                System.err.println("工具注册失败: " + tool.getName());
            }
        }
    }
}