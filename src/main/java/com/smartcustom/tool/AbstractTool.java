package com.smartcustom.tool;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * 抽象工具基类
 * 
 * @author SmartCustom Team
 */
public abstract class AbstractTool implements Tool {
    
    protected final Logger logger = LoggerFactory.getLogger(getClass());
    
    private String name;
    private String description;
    private Map<String, Object> parameters;
    private boolean enabled;
    
    protected AbstractTool(String name, String description) {
        this.name = name;
        this.description = description;
        this.parameters = new HashMap<>();
        this.enabled = true;
    }
    
    @Override
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    @Override
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    @Override
    public Map<String, Object> getParameters() {
        return parameters;
    }
    
    public void setParameters(Map<String, Object> parameters) {
        this.parameters = parameters;
    }
    
    public void addParameter(String name, Object parameter) {
        this.parameters.put(name, parameter);
    }
    
    @Override
    public boolean isEnabled() {
        return enabled;
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    @Override
    public ToolResult execute(Map<String, Object> parameters) {
        if (!isEnabled()) {
            return ToolResult.error("工具已禁用: " + getName());
        }
        
        try {
            logger.info("执行工具: {}, 参数: {}", getName(), parameters);
            ToolResult result = doExecute(parameters);
            logger.info("工具执行完成: {}, 成功: {}", getName(), result.isSuccess());
            return result;
        } catch (Exception e) {
            logger.error("工具执行失败: {}, 错误: {}", getName(), e.getMessage(), e);
            return ToolResult.error("工具执行失败: " + e.getMessage());
        }
    }
    
    /**
     * 子类实现具体的工具执行逻辑
     * 
     * @param parameters 工具参数
     * @return 执行结果
     */
    protected abstract ToolResult doExecute(Map<String, Object> parameters);
}