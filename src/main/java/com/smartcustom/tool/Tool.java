package com.smartcustom.tool;

import java.util.Map;

/**
 * 工具接口
 * 
 * @author SmartCustom Team
 */
public interface Tool {
    
    /**
     * 获取工具名称
     * 
     * @return 工具名称
     */
    String getName();
    
    /**
     * 获取工具描述
     * 
     * @return 工具描述
     */
    String getDescription();
    
    /**
     * 获取工具参数定义
     * 
     * @return 参数定义
     */
    Map<String, Object> getParameters();
    
    /**
     * 执行工具
     * 
     * @param parameters 工具参数
     * @return 执行结果
     */
    ToolResult execute(Map<String, Object> parameters);
    
    /**
     * 是否启用
     * 
     * @return 是否启用
     */
    default boolean isEnabled() {
        return true;
    }
    
    /**
     * 获取工具版本
     * 
     * @return 工具版本
     */
    default String getVersion() {
        return "1.0.0";
    }
}