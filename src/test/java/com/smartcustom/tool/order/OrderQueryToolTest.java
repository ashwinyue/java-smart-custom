package com.smartcustom.tool.order;

import com.smartcustom.tool.ToolResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class OrderQueryToolTest {
    
    private OrderQueryTool orderQueryTool;
    
    @BeforeEach
    void setUp() {
        orderQueryTool = new OrderQueryTool();
    }
    
    @Test
    void testGetName() {
        assertEquals("order-query", orderQueryTool.getName());
    }
    
    @Test
    void testGetDescription() {
        assertNotNull(orderQueryTool.getDescription());
        assertTrue(orderQueryTool.getDescription().contains("订单查询"));
    }
    
    @Test
    void testQueryExistingOrder() {
        Map<String, Object> params = new HashMap<>();
        params.put("order_id", "ORD202311001");
        
        ToolResult result = orderQueryTool.execute(params);
        
        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
        
        @SuppressWarnings("unchecked")
        Map<String, Object> data = (Map<String, Object>) result.getData();
        assertTrue(data.containsKey("order_info"));
        assertTrue(data.containsKey("status_description"));
        
        @SuppressWarnings("unchecked")
        OrderQueryTool.OrderInfo orderInfo = (OrderQueryTool.OrderInfo) data.get("order_info");
        assertEquals("ORD202311001", orderInfo.getOrderId());
        assertEquals("智能手表", orderInfo.getProductName());
        assertEquals("已发货", orderInfo.getStatus());
    }
    
    @Test
    void testQueryNonExistingOrder() {
        Map<String, Object> params = new HashMap<>();
        params.put("order_id", "NONEXISTING");
        
        ToolResult result = orderQueryTool.execute(params);
        
        assertFalse(result.isSuccess());
        assertTrue(result.getError().contains("不存在"));
    }
    
    @Test
    void testQueryWithEmptyOrderId() {
        Map<String, Object> params = new HashMap<>();
        params.put("order_id", "");
        
        ToolResult result = orderQueryTool.execute(params);
        
        assertFalse(result.isSuccess());
        assertTrue(result.getError().contains("订单号不能为空"));
    }
    
    @Test
    void testQueryWithNullOrderId() {
        Map<String, Object> params = new HashMap<>();
        params.put("order_id", null);
        
        ToolResult result = orderQueryTool.execute(params);
        
        assertFalse(result.isSuccess());
        assertTrue(result.getError().contains("订单号不能为空"));
    }
    
    @Test
    void testQueryWithMissingOrderId() {
        Map<String, Object> params = new HashMap<>();
        // 不包含 order_id 参数
        
        ToolResult result = orderQueryTool.execute(params);
        
        assertFalse(result.isSuccess());
        assertTrue(result.getError().contains("订单号不能为空"));
    }
    
    @Test
    void testQueryDeliveredOrder() {
        Map<String, Object> params = new HashMap<>();
        params.put("order_id", "ORD202311002");
        
        ToolResult result = orderQueryTool.execute(params);
        
        assertTrue(result.isSuccess());
        
        @SuppressWarnings("unchecked")
        Map<String, Object> data = (Map<String, Object>) result.getData();
        @SuppressWarnings("unchecked")
        OrderQueryTool.OrderInfo orderInfo = (OrderQueryTool.OrderInfo) data.get("order_info");
        assertEquals("ORD202311002", orderInfo.getOrderId());
        assertEquals("无线耳机", orderInfo.getProductName());
        assertEquals("已签收", orderInfo.getStatus());
        assertNotNull(orderInfo.getDeliveryDate());
    }
    
    @Test
    void testQueryProcessingOrder() {
        Map<String, Object> params = new HashMap<>();
        params.put("order_id", "ORD202311003");
        
        ToolResult result = orderQueryTool.execute(params);
        
        assertTrue(result.isSuccess());
        
        @SuppressWarnings("unchecked")
        Map<String, Object> data = (Map<String, Object>) result.getData();
        @SuppressWarnings("unchecked")
        OrderQueryTool.OrderInfo orderInfo = (OrderQueryTool.OrderInfo) data.get("order_info");
        assertEquals("ORD202311003", orderInfo.getOrderId());
        assertEquals("智能音箱", orderInfo.getProductName());
        assertEquals("处理中", orderInfo.getStatus());
        assertNull(orderInfo.getTrackingNumber());
    }
}