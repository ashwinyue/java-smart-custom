package com.smartcustom.service.impl;

import com.smartcustom.service.ToolManager;
import com.smartcustom.tool.AbstractTool;
import com.smartcustom.tool.Tool;
import com.smartcustom.tool.ToolResult;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 工具管理器实现类
 * 
 * @author SmartCustom Team
 */
@Service
public class ToolManagerImpl implements ToolManager {
    
    private final Map<String, Tool> tools = new ConcurrentHashMap<>();
    
    @Override
    public boolean registerTool(Tool tool) {
        if (tool == null || tool.getName() == null || tool.getName().trim().isEmpty()) {
            return false;
        }
        
        String toolName = tool.getName();
        if (tools.containsKey(toolName)) {
            return false;
        }
        
        tools.put(toolName, tool);
        return true;
    }
    
    @Override
    @CacheEvict(value = {"toolCache", "enabledToolCache"}, allEntries = true)
    public boolean unregisterTool(String toolName) {
        return tools.remove(toolName) != null;
    }
    
    @Override
    @Cacheable(value = "toolCache", key = "#toolName")
    public Tool getTool(String toolName) {
        return tools.get(toolName);
    }
    
    @Override
    @Cacheable(value = "toolCache")
    public List<Tool> getAllTools() {
        return new ArrayList<>(tools.values());
    }
    
    @Override
    @Cacheable(value = "enabledToolCache")
    public List<Tool> getEnabledTools() {
        return tools.values().stream()
                .filter(Tool::isEnabled)
                .toList();
    }
    
    @Override
    public ToolResult executeTool(String toolName, Map<String, Object> parameters) {
        Tool tool = getTool(toolName);
        if (tool == null) {
            return ToolResult.error("工具不存在: " + toolName);
        }
        
        if (!tool.isEnabled()) {
            return ToolResult.error("工具已禁用: " + toolName);
        }
        
        return tool.execute(parameters);
    }
    
    @Override
    @CacheEvict(value = "enabledToolCache", allEntries = true)
    public boolean enableTool(String toolName) {
        Tool tool = getTool(toolName);
        if (tool == null) {
            return false;
        }
        
        if (tool instanceof AbstractTool) {
            ((AbstractTool) tool).setEnabled(true);
            return true;
        }
        
        return false;
    }
    
    @Override
    @CacheEvict(value = "enabledToolCache", allEntries = true)
    public boolean disableTool(String toolName) {
        Tool tool = getTool(toolName);
        if (tool == null) {
            return false;
        }
        
        if (tool instanceof AbstractTool) {
            ((AbstractTool) tool).setEnabled(false);
            return true;
        }
        
        return false;
    }
    
    @Override
    public boolean hasTool(String toolName) {
        return tools.containsKey(toolName);
    }
}