package com.smartcustom.tool.calculator;

import com.smartcustom.tool.AbstractTool;
import com.smartcustom.tool.ToolResult;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import org.springframework.stereotype.Component;

/**
 * 计算器工具
 * 
 * @author SmartCustom Team
 */
@Component
public class CalculatorTool extends AbstractTool {
    
    private static final String NAME = "calculator";
    private static final String DESCRIPTION = "计算器工具，用于执行各种数学计算，包括基本运算、百分比计算和复杂表达式";
    
    public CalculatorTool() {
        super(NAME, DESCRIPTION);
        
        // 定义工具参数
        Map<String, Object> calculateParams = new HashMap<>();
        calculateParams.put("type", "object");
        Map<String, Object> calculateProperties = new HashMap<>();
        
        Map<String, Object> expression = new HashMap<>();
        expression.put("type", "string");
        expression.put("description", "数学表达式，例如: 2 + 3 * 4");
        calculateProperties.put("expression", expression);
        
        Map<String, Object> operation = new HashMap<>();
        operation.put("type", "string");
        operation.put("description", "操作类型: add, subtract, multiply, divide, power, sqrt, percentage");
        calculateProperties.put("operation", operation);
        
        Map<String, Object> operand1 = new HashMap<>();
        operand1.put("type", "number");
        operand1.put("description", "第一个操作数");
        calculateProperties.put("operand1", operand1);
        
        Map<String, Object> operand2 = new HashMap<>();
        operand2.put("type", "number");
        operand2.put("description", "第二个操作数（对于单操作数运算可选）");
        calculateProperties.put("operand2", operand2);
        
        calculateProperties.put("required", new String[]{"operation"});
        calculateParams.put("properties", calculateProperties);
        
        addParameter("calculate", calculateParams);
    }
    
    @Override
    protected ToolResult doExecute(Map<String, Object> parameters) {
        String operation = (String) parameters.get("operation");
        
        // 修复参数验证
        if (operation == null || operation.trim().isEmpty()) {
            // 检查是否是表达式计算
            String expression = (String) parameters.get("expression");
            if (expression != null && !expression.trim().isEmpty()) {
                return evaluateExpression(parameters);
            }
            return ToolResult.error("操作类型不能为空");
        }
        
        switch (operation.toLowerCase()) {
            case "add":
            case "addition":
                return performAddition(parameters);
            case "subtract":
            case "subtraction":
                return performSubtraction(parameters);
            case "multiply":
            case "multiplication":
                return performMultiplication(parameters);
            case "divide":
            case "division":
                return performDivision(parameters);
            case "power":
            case "exponentiation":
                return performPower(parameters);
            case "sqrt":
            case "square_root":
                return performSquareRoot(parameters);
            case "percentage":
                return performPercentage(parameters);
            case "expression":
                String expression = (String) parameters.get("expression");
                if (expression == null || expression.trim().isEmpty()) {
                    return ToolResult.error("表达式不能为空");
                }
                return evaluateExpression(parameters);
            default:
                return ToolResult.error("不支持的操作: " + operation);
        }
    }
    
    private ToolResult performAddition(Map<String, Object> parameters) {
        BigDecimal operand1 = getBigDecimalParameter(parameters, "operand1");
        BigDecimal operand2 = getBigDecimalParameter(parameters, "operand2");
        
        if (operand1 == null || operand2 == null) {
            return ToolResult.error("加法运算需要两个操作数");
        }
        
        BigDecimal result = operand1.add(operand2).stripTrailingZeros();
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("result", result);
        resultMap.put("operation", "addition");
        resultMap.put("operand1", operand1);
        resultMap.put("operand2", operand2);
        
        return ToolResult.success("加法计算完成", resultMap);
    }
    
    private ToolResult performSubtraction(Map<String, Object> parameters) {
        BigDecimal operand1 = getBigDecimalParameter(parameters, "operand1");
        BigDecimal operand2 = getBigDecimalParameter(parameters, "operand2");
        
        if (operand1 == null || operand2 == null) {
            return ToolResult.error("减法运算需要两个操作数");
        }
        
        BigDecimal result = operand1.subtract(operand2).stripTrailingZeros();
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("result", result);
        resultMap.put("operation", "subtraction");
        resultMap.put("operand1", operand1);
        resultMap.put("operand2", operand2);
        
        return ToolResult.success("减法计算完成", resultMap);
    }
    
    private ToolResult performMultiplication(Map<String, Object> parameters) {
        BigDecimal operand1 = getBigDecimalParameter(parameters, "operand1");
        BigDecimal operand2 = getBigDecimalParameter(parameters, "operand2");
        
        if (operand1 == null || operand2 == null) {
            return ToolResult.error("乘法运算需要两个操作数");
        }
        
        BigDecimal result = operand1.multiply(operand2).stripTrailingZeros();
        // 确保科学计数法转换为普通数字格式
        if (result.scale() < 0) {
            result = result.setScale(0);
        }
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("result", result);
        resultMap.put("operation", "multiplication");
        resultMap.put("operand1", operand1);
        resultMap.put("operand2", operand2);
        
        return ToolResult.success("乘法计算完成", resultMap);
    }
    
    private ToolResult performDivision(Map<String, Object> parameters) {
        BigDecimal operand1 = getBigDecimalParameter(parameters, "operand1");
        BigDecimal operand2 = getBigDecimalParameter(parameters, "operand2");
        
        // 修复参数验证
        if (operand1 == null || operand2 == null) {
            return ToolResult.error("除法运算需要两个操作数");
        }
        
        if (operand2.compareTo(BigDecimal.ZERO) == 0) {
            return ToolResult.error("除数不能为零");
        }
        
        BigDecimal result = operand1.divide(operand2, 10, RoundingMode.HALF_UP).stripTrailingZeros();
        // 确保科学计数法转换为普通数字格式
        if (result.scale() < 0) {
            result = result.setScale(0);
        }
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("result", result);
        resultMap.put("operation", "division");
        resultMap.put("operand1", operand1);
        resultMap.put("operand2", operand2);
        
        return ToolResult.success("除法计算完成", resultMap);
    }
    
    private ToolResult performSquareRoot(Map<String, Object> parameters) {
        BigDecimal operand = getBigDecimalParameter(parameters, "operand1");
        
        // 修复参数验证
        if (operand == null) {
            return ToolResult.error("平方根运算需要一个操作数");
        }
        
        if (operand.compareTo(BigDecimal.ZERO) < 0) {
            return ToolResult.error("不能计算负数的平方根");
        }
        
        try {
            // 使用BigDecimal进行平方根运算
            double result = Math.sqrt(operand.doubleValue());
            BigDecimal resultBD = new BigDecimal(String.valueOf(result)).stripTrailingZeros();
            // 确保科学计数法转换为普通数字格式
            if (resultBD.scale() < 0) {
                resultBD = resultBD.setScale(0);
            }
            
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("result", resultBD);
            resultMap.put("operation", "square_root");
            resultMap.put("operand", operand);
            
            return ToolResult.success("平方根计算完成", resultMap);
        } catch (Exception e) {
            return ToolResult.error("平方根计算失败: " + e.getMessage());
        }
    }
    
    private ToolResult performPower(Map<String, Object> parameters) {
        BigDecimal base = getBigDecimalParameter(parameters, "operand1");
        BigDecimal exponent = getBigDecimalParameter(parameters, "operand2");
        
        if (base == null || exponent == null) {
            return ToolResult.error("幂运算需要两个操作数");
        }
        
        try {
            // 使用BigDecimal进行幂运算
            double result = Math.pow(base.doubleValue(), exponent.doubleValue());
            BigDecimal resultBD = new BigDecimal(String.valueOf(result)).stripTrailingZeros();
            // 确保科学计数法转换为普通数字格式
            if (resultBD.scale() < 0) {
                resultBD = resultBD.setScale(0);
            }
            
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("result", resultBD);
            resultMap.put("operation", "power");
            resultMap.put("base", base);
            resultMap.put("exponent", exponent);
            
            return ToolResult.success("幂运算计算完成", resultMap);
        } catch (Exception e) {
            return ToolResult.error("幂运算计算失败: " + e.getMessage());
        }
    }
    
    private ToolResult performPercentage(Map<String, Object> parameters) {
        BigDecimal value = getBigDecimalParameter(parameters, "operand1");
        BigDecimal percentage = getBigDecimalParameter(parameters, "operand2");
        
        // 如果只有一个参数，假设是求该值的百分比（除以100）
        if (value != null && percentage == null) {
            BigDecimal result = value.divide(new BigDecimal("100"), 10, RoundingMode.HALF_UP).stripTrailingZeros();
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("result", result);
            resultMap.put("operation", "percentage");
            resultMap.put("value", value);
            
            return ToolResult.success("百分比计算完成", resultMap);
        }
        
        if (value == null || percentage == null) {
            return ToolResult.error("百分比计算需要至少一个操作数");
        }
        
        BigDecimal result = value.multiply(percentage).divide(new BigDecimal("100"), 10, RoundingMode.HALF_UP).stripTrailingZeros();
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("result", result);
        resultMap.put("operation", "percentage");
        resultMap.put("value", value);
        resultMap.put("percentage", percentage);
        
        return ToolResult.success("百分比计算完成", resultMap);
    }
    
    private ToolResult evaluateExpression(Map<String, Object> parameters) {
        String expression = (String) parameters.get("expression");
        if (expression == null) {
            expression = (String) parameters.get("expr"); // 尝试获取"expr"参数
        }
        
        if (expression == null || expression.trim().isEmpty()) {
            return ToolResult.error("表达式不能为空");
        }
        
        try {
            // 验证表达式格式
            if (!isValidExpression(expression)) {
                return ToolResult.error("无效的表达式格式");
            }
            
            // 使用ScriptEngine计算表达式
            ScriptEngineManager manager = new ScriptEngineManager();
            ScriptEngine engine = manager.getEngineByName("JavaScript");
            
            if (engine == null) {
                return ToolResult.error("无法初始化JavaScript引擎");
            }
            
            Object result = engine.eval(expression);
            
            if (result instanceof Number) {
                BigDecimal resultBD = new BigDecimal(result.toString()).stripTrailingZeros();
                // 确保科学计数法转换为普通数字格式
                if (resultBD.scale() < 0) {
                    resultBD = resultBD.setScale(0);
                }
                
                Map<String, Object> resultMap = new HashMap<>();
                resultMap.put("result", resultBD);
                resultMap.put("expression", expression);
                return ToolResult.success("表达式计算完成", resultMap);
            } else {
                return ToolResult.error("表达式计算结果不是有效数字");
            }
        } catch (Exception e) {
            return ToolResult.error("表达式计算失败: " + e.getMessage());
        }
    }
    
    private boolean isValidExpression(String expression) {
        // 验证表达式只包含数字、运算符、括号和空格
        return expression.matches("[0-9+\\-*/().\\s]+");
    }
    
    private BigDecimal getBigDecimalParameter(Map<String, Object> parameters, String key) {
        // 首先尝试获取指定key的值
        Object value = parameters.get(key);
        if (value == null) {
            // 如果是operand1，尝试获取"a"
            if ("operand1".equals(key)) {
                value = parameters.get("a");
            }
            // 如果是operand2，尝试获取"b"
            else if ("operand2".equals(key)) {
                value = parameters.get("b");
            }
        }
        
        if (value == null) {
            return null;
        }
        
        if (value instanceof BigDecimal) {
            return (BigDecimal) value;
        } else if (value instanceof Number) {
            return new BigDecimal(value.toString());
        } else if (value instanceof String) {
            try {
                return new BigDecimal((String) value);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        
        return null;
    }
}