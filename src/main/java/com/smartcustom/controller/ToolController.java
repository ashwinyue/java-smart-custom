package com.smartcustom.controller;

import com.smartcustom.service.ToolManager;
import com.smartcustom.tool.Tool;
import com.smartcustom.tool.ToolResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 工具API控制器
 * 
 * @author SmartCustom Team
 */
@RestController
@RequestMapping("/api/tools")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ToolController {
    
    private final ToolManager toolManager;
    
    @Autowired
    public ToolController(ToolManager toolManager) {
        this.toolManager = toolManager;
    }
    
    /**
     * 获取所有工具
     * 
     * @return 工具列表
     */
    @GetMapping
    public ResponseEntity<List<Tool>> getAllTools() {
        List<Tool> tools = toolManager.getAllTools();
        return ResponseEntity.ok(tools);
    }
    
    /**
     * 获取所有已启用的工具
     * 
     * @return 工具列表
     */
    @GetMapping("/enabled")
    public ResponseEntity<List<Tool>> getEnabledTools() {
        List<Tool> tools = toolManager.getEnabledTools();
        return ResponseEntity.ok(tools);
    }
    
    /**
     * 获取指定工具
     * 
     * @param toolName 工具名称
     * @return 工具信息
     */
    @GetMapping("/{toolName}")
    public ResponseEntity<Tool> getTool(@PathVariable String toolName) {
        Tool tool = toolManager.getTool(toolName);
        if (tool == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(tool);
    }
    
    /**
     * 执行工具
     * 
     * @param toolName 工具名称
     * @param parameters 工具参数
     * @return 执行结果
     */
    @PostMapping("/{toolName}/execute")
    public ResponseEntity<ToolResult> executeTool(
            @PathVariable String toolName,
            @RequestBody Map<String, Object> parameters) {
        ToolResult result = toolManager.executeTool(toolName, parameters);
        if (!result.isSuccess()) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        }
        return ResponseEntity.ok(result);
    }
    
    /**
     * 启用工具
     * 
     * @param toolName 工具名称
     * @return 操作结果
     */
    @PostMapping("/{toolName}/enable")
    public ResponseEntity<?> enableTool(@PathVariable String toolName) {
        boolean success = toolManager.enableTool(toolName);
        if (success) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * 禁用工具
     * 
     * @param toolName 工具名称
     * @return 操作结果
     */
    @PostMapping("/{toolName}/disable")
    public ResponseEntity<?> disableTool(@PathVariable String toolName) {
        boolean success = toolManager.disableTool(toolName);
        if (success) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * 检查工具是否存在
     * 
     * @param toolName 工具名称
     * @return 是否存在
     */
    @GetMapping("/{toolName}/exists")
    public ResponseEntity<Boolean> hasTool(@PathVariable String toolName) {
        boolean exists = toolManager.hasTool(toolName);
        return ResponseEntity.ok(exists);
    }
}