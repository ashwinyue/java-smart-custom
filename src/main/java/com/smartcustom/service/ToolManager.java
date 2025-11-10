package com.smartcustom.service;

import com.smartcustom.tool.Tool;
import com.smartcustom.tool.ToolResult;

import java.util.List;
import java.util.Map;

/**
 * 工具管理器接口
 * 
 * @author SmartCustom Team
 */
public interface ToolManager {
    
    /**
     * 注册工具
     * 
     * @param tool 工具实例
     * @return 是否注册成功
     */
    boolean registerTool(Tool tool);
    
    /**
     * 注销工具
     * 
     * @param toolName 工具名称
     * @return 是否注销成功
     */
    boolean unregisterTool(String toolName);
    
    /**
     * 获取工具
     * 
     * @param toolName 工具名称
     * @return 工具实例
     */
    Tool getTool(String toolName);
    
    /**
     * 获取所有已注册的工具
     * 
     * @return 工具列表
     */
    List<Tool> getAllTools();
    
    /**
     * 获取所有已启用的工具
     * 
     * @return 工具列表
     */
    List<Tool> getEnabledTools();
    
    /**
     * 执行工具
     * 
     * @param toolName 工具名称
     * @param parameters 工具参数
     * @return 执行结果
     */
    ToolResult executeTool(String toolName, Map<String, Object> parameters);
    
    /**
     * 启用工具
     * 
     * @param toolName 工具名称
     * @return 是否成功
     */
    boolean enableTool(String toolName);
    
    /**
     * 禁用工具
     * 
     * @param toolName 工具名称
     * @return 是否成功
     */
    boolean disableTool(String toolName);
    
    /**
     * 检查工具是否存在
     * 
     * @param toolName 工具名称
     * @return 是否存在
     */
    boolean hasTool(String toolName);
}