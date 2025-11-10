package com.smartcustom.tool.calculator;

import com.smartcustom.tool.ToolResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CalculatorToolTest {
    
    private CalculatorTool calculatorTool;
    
    @BeforeEach
    void setUp() {
        calculatorTool = new CalculatorTool();
    }
    
    @Test
    void testGetName() {
        assertEquals("calculator", calculatorTool.getName());
    }
    
    @Test
    void testGetDescription() {
        assertNotNull(calculatorTool.getDescription());
        assertTrue(calculatorTool.getDescription().contains("计算器"));
    }
    
    @Test
    void testAddition() {
        Map<String, Object> params = new HashMap<>();
        params.put("operation", "add");
        params.put("a", new BigDecimal("10"));
        params.put("b", new BigDecimal("5"));
        
        ToolResult result = calculatorTool.execute(params);
        
        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
        
        @SuppressWarnings("unchecked")
        Map<String, Object> data = (Map<String, Object>) result.getData();
        assertEquals(new BigDecimal("15"), data.get("result"));
    }
    
    @Test
    void testSubtraction() {
        Map<String, Object> params = new HashMap<>();
        params.put("operation", "subtract");
        params.put("a", new BigDecimal("10"));
        params.put("b", new BigDecimal("5"));
        
        ToolResult result = calculatorTool.execute(params);
        
        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
        
        @SuppressWarnings("unchecked")
        Map<String, Object> data = (Map<String, Object>) result.getData();
        assertEquals(new BigDecimal("5"), data.get("result"));
    }
    
    @Test
    void testMultiplication() {
        Map<String, Object> params = new HashMap<>();
        params.put("operation", "multiply");
        params.put("a", new BigDecimal("10"));
        params.put("b", new BigDecimal("5"));
        
        ToolResult result = calculatorTool.execute(params);
        
        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
        
        @SuppressWarnings("unchecked")
        Map<String, Object> data = (Map<String, Object>) result.getData();
        assertEquals(new BigDecimal("50"), data.get("result"));
    }
    
    @Test
    void testDivision() {
        Map<String, Object> params = new HashMap<>();
        params.put("operation", "divide");
        params.put("a", new BigDecimal("10"));
        params.put("b", new BigDecimal("5"));
        
        ToolResult result = calculatorTool.execute(params);
        
        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
        
        @SuppressWarnings("unchecked")
        Map<String, Object> data = (Map<String, Object>) result.getData();
        assertEquals(new BigDecimal("2"), data.get("result"));
    }
    
    @Test
    void testDivisionByZero() {
        Map<String, Object> params = new HashMap<>();
        params.put("operation", "divide");
        params.put("a", new BigDecimal("10"));
        params.put("b", new BigDecimal("0"));
        
        ToolResult result = calculatorTool.execute(params);
        
        assertFalse(result.isSuccess());
        assertNotNull(result.getMessage());
        assertTrue(result.getMessage().contains("除数不能为零"));
    }
    
    @Test
    void testPower() {
        Map<String, Object> params = new HashMap<>();
        params.put("operation", "power");
        params.put("a", new BigDecimal("2"));
        params.put("b", new BigDecimal("3"));
        
        ToolResult result = calculatorTool.execute(params);
        
        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
        
        @SuppressWarnings("unchecked")
        Map<String, Object> data = (Map<String, Object>) result.getData();
        assertEquals(new BigDecimal("8"), data.get("result"));
    }
    
    @Test
    void testSquareRoot() {
        Map<String, Object> params = new HashMap<>();
        params.put("operation", "sqrt");
        params.put("a", new BigDecimal("16"));
        
        ToolResult result = calculatorTool.execute(params);
        
        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
        
        @SuppressWarnings("unchecked")
        Map<String, Object> data = (Map<String, Object>) result.getData();
        assertEquals(new BigDecimal("4"), data.get("result"));
    }
    
    @Test
    void testSquareRootOfNegativeNumber() {
        Map<String, Object> params = new HashMap<>();
        params.put("operation", "sqrt");
        params.put("a", new BigDecimal("-16"));
        
        ToolResult result = calculatorTool.execute(params);
        
        assertFalse(result.isSuccess());
        assertNotNull(result.getMessage());
        assertTrue(result.getMessage().contains("不能计算负数的平方根"));
    }
    
    @Test
    void testPercentage() {
        Map<String, Object> params = new HashMap<>();
        params.put("operation", "percentage");
        params.put("a", new BigDecimal("50"));
        
        ToolResult result = calculatorTool.execute(params);
        
        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
        
        @SuppressWarnings("unchecked")
        Map<String, Object> data = (Map<String, Object>) result.getData();
        assertEquals(new BigDecimal("0.5"), data.get("result"));
    }
    
    @Test
    void testExpression() {
        Map<String, Object> params = new HashMap<>();
        params.put("operation", "expression");
        params.put("expr", "2 + 3 * 4");
        
        ToolResult result = calculatorTool.execute(params);
        
        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
        
        @SuppressWarnings("unchecked")
        Map<String, Object> data = (Map<String, Object>) result.getData();
        assertEquals(new BigDecimal("14"), data.get("result"));
    }
    
    @Test
    void testInvalidOperation() {
        Map<String, Object> params = new HashMap<>();
        params.put("operation", "invalid_op");
        params.put("a", new BigDecimal("10"));
        params.put("b", new BigDecimal("5"));
        
        ToolResult result = calculatorTool.execute(params);
        
        assertFalse(result.isSuccess());
        assertNotNull(result.getMessage());
        assertTrue(result.getMessage().contains("不支持的操作"));
    }
    
    @Test
    void testMissingParameters() {
        Map<String, Object> params = new HashMap<>();
        params.put("operation", "add");
        // 缺少参数 a 和 b
        
        ToolResult result = calculatorTool.execute(params);
        
        assertFalse(result.isSuccess());
        assertNotNull(result.getMessage());
        assertTrue(result.getMessage().contains("缺少必需参数"));
    }
}