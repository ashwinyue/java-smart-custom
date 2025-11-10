package com.smartcustom.tool.calculator;

import com.smartcustom.tool.AbstractTool;
import com.smartcustom.tool.ToolResult;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

/**
 * 计算器工具
 * 
 * @author SmartCustom Team
 */
@Component
public class CalculatorTool extends AbstractTool {
    
    private static final String NAME = "calculator";
    private static final String DESCRIPTION = "用于执行各种数学计算，包括基本运算、百分比计算和复杂表达式";
    
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
        
        if (operation == null) {
            return ToolResult.error("操作类型不能为空");
        }
        
        try {
            switch (operation.toLowerCase()) {
                case "add":
                    return performAddition(parameters);
                case "subtract":
                    return performSubtraction(parameters);
                case "multiply":
                    return performMultiplication(parameters);
                case "divide":
                    return performDivision(parameters);
                case "power":
                    return performPower(parameters);
                case "sqrt":
                    return performSquareRoot(parameters);
                case "percentage":
                    return performPercentage(parameters);
                case "expression":
                    return evaluateExpression(parameters);
                default:
                    return ToolResult.error("不支持的操作: " + operation);
            }
        } catch (Exception e) {
            return ToolResult.error("计算时出错: " + e.getMessage());
        }
    }
    
    private ToolResult performAddition(Map<String, Object> parameters) {
        BigDecimal operand1 = getBigDecimalParameter(parameters, "operand1");
        BigDecimal operand2 = getBigDecimalParameter(parameters, "operand2");
        
        if (operand1 == null || operand2 == null) {
            return ToolResult.error("加法运算需要两个操作数");
        }
        
        BigDecimal result = operand1.add(operand2);
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
        
        BigDecimal result = operand1.subtract(operand2);
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
        
        BigDecimal result = operand1.multiply(operand2);
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
        
        if (operand1 == null || operand2 == null) {
            return ToolResult.error("除法运算需要两个操作数");
        }
        
        if (operand2.compareTo(BigDecimal.ZERO) == 0) {
            return ToolResult.error("除数不能为零");
        }
        
        BigDecimal result = operand1.divide(operand2, 10, RoundingMode.HALF_UP);
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("result", result);
        resultMap.put("operation", "division");
        resultMap.put("operand1", operand1);
        resultMap.put("operand2", operand2);
        
        return ToolResult.success("除法计算完成", resultMap);
    }
    
    private ToolResult performPower(Map<String, Object> parameters) {
        BigDecimal base = getBigDecimalParameter(parameters, "operand1");
        BigDecimal exponent = getBigDecimalParameter(parameters, "operand2");
        
        if (base == null || exponent == null) {
            return ToolResult.error("幂运算需要两个操作数");
        }
        
        try {
            double result = Math.pow(base.doubleValue(), exponent.doubleValue());
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("result", result);
            resultMap.put("operation", "power");
            resultMap.put("base", base);
            resultMap.put("exponent", exponent);
            
            return ToolResult.success("幂运算计算完成", resultMap);
        } catch (Exception e) {
            return ToolResult.error("幂运算计算失败: " + e.getMessage());
        }
    }
    
    private ToolResult performSquareRoot(Map<String, Object> parameters) {
        BigDecimal operand = getBigDecimalParameter(parameters, "operand1");
        
        if (operand == null) {
            return ToolResult.error("平方根运算需要一个操作数");
        }
        
        if (operand.compareTo(BigDecimal.ZERO) < 0) {
            return ToolResult.error("不能计算负数的平方根");
        }
        
        try {
            double result = Math.sqrt(operand.doubleValue());
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("result", result);
            resultMap.put("operation", "square_root");
            resultMap.put("operand", operand);
            
            return ToolResult.success("平方根计算完成", resultMap);
        } catch (Exception e) {
            return ToolResult.error("平方根计算失败: " + e.getMessage());
        }
    }
    
    private ToolResult performPercentage(Map<String, Object> parameters) {
        BigDecimal value = getBigDecimalParameter(parameters, "operand1");
        BigDecimal percentage = getBigDecimalParameter(parameters, "operand2");
        
        if (value == null || percentage == null) {
            return ToolResult.error("百分比计算需要两个操作数");
        }
        
        BigDecimal result = value.multiply(percentage).divide(new BigDecimal("100"), 10, RoundingMode.HALF_UP);
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("result", result);
        resultMap.put("operation", "percentage");
        resultMap.put("value", value);
        resultMap.put("percentage", percentage);
        
        return ToolResult.success("百分比计算完成", resultMap);
    }
    
    private ToolResult evaluateExpression(Map<String, Object> parameters) {
        String expression = (String) parameters.get("expression");
        
        if (expression == null || expression.trim().isEmpty()) {
            return ToolResult.error("表达式不能为空");
        }
        
        try {
            // 简单的表达式求值实现（实际应用中应使用更安全的表达式解析库）
            // 这里仅支持基本的加减乘除
            expression = expression.replaceAll("\\s+", "");
            
            if (!expression.matches("[0-9+\\-*/.()]+")) {
                return ToolResult.error("表达式包含非法字符");
            }
            
            // 使用JavaScript引擎或其他表达式解析库会更安全
            // 这里为了简化，使用一个简单的实现
            double result = evaluateSimpleExpression(expression);
            
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("result", result);
            resultMap.put("operation", "expression");
            resultMap.put("expression", expression);
            
            return ToolResult.success("表达式计算完成", resultMap);
        } catch (Exception e) {
            return ToolResult.error("表达式计算失败: " + e.getMessage());
        }
    }
    
    private double evaluateSimpleExpression(String expression) {
        // 这是一个非常简化的表达式求值实现
        // 实际应用中应该使用更安全、更完整的表达式解析库
        try {
            // 使用Java内置的ScriptEngine会更安全，但这里为了简化
            // 仅支持基本的加减乘除和括号
            return new javax.script.ScriptEngineManager()
                    .getEngineByName("js")
                    .eval(expression)
                    .toString()
                    .contains(".") ? 
                    Double.parseDouble(
                        new javax.script.ScriptEngineManager()
                        .getEngineByName("js")
                        .eval(expression)
                        .toString()
                    ) : 
                    Double.parseDouble(
                        new javax.script.ScriptEngineManager()
                        .getEngineByName("js")
                        .eval(expression)
                        .toString()
                    );
        } catch (Exception e) {
            throw new RuntimeException("表达式求值失败: " + e.getMessage());
        }
    }
    
    private BigDecimal getBigDecimalParameter(Map<String, Object> parameters, String key) {
        Object value = parameters.get(key);
        if (value == null) {
            return null;
        }
        
        if (value instanceof BigDecimal) {
            return (BigDecimal) value;
        } else if (value instanceof Number) {
            return BigDecimal.valueOf(((Number) value).doubleValue());
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