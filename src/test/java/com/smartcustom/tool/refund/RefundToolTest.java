package com.smartcustom.tool.refund;

import com.smartcustom.tool.ToolResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class RefundToolTest {
    
    private RefundTool refundTool;
    
    @BeforeEach
    void setUp() {
        refundTool = new RefundTool();
    }
    
    @Test
    void testGetName() {
        assertEquals("refund", refundTool.getName());
    }
    
    @Test
    void testGetDescription() {
        assertNotNull(refundTool.getDescription());
        assertTrue(refundTool.getDescription().contains("退款申请"));
    }
    
    @Test
    void testGetRefundReasons() {
        Map<String, Object> params = new HashMap<>();
        params.put("action", "get_reasons");
        
        ToolResult result = refundTool.execute(params);
        
        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
        
        @SuppressWarnings("unchecked")
        Map<String, Object> data = (Map<String, Object>) result.getData();
        assertTrue(data.containsKey("reasons"));
        
        @SuppressWarnings("unchecked")
        java.util.List<String> reasons = (java.util.List<String>) data.get("reasons");
        assertFalse(reasons.isEmpty());
        assertTrue(reasons.contains("商品质量问题"));
        assertTrue(reasons.contains("商品与描述不符"));
    }
    
    @Test
    void testSubmitRefundRequest() {
        Map<String, Object> params = new HashMap<>();
        params.put("action", "submit");
        params.put("order_id", "ORD202311001");
        params.put("reason", "商品质量问题");
        params.put("description", "商品有划痕");
        
        ToolResult result = refundTool.execute(params);
        
        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
        
        @SuppressWarnings("unchecked")
        Map<String, Object> data = (Map<String, Object>) result.getData();
        assertTrue(data.containsKey("refund_id"));
        assertTrue(data.containsKey("message"));
        
        String refundId = (String) data.get("refund_id");
        assertNotNull(refundId);
        assertTrue(refundId.startsWith("REF"));
    }
    
    @Test
    void testSubmitRefundRequestWithMissingOrderId() {
        Map<String, Object> params = new HashMap<>();
        params.put("action", "submit");
        params.put("reason", "商品质量问题");
        // 缺少 order_id
        
        ToolResult result = refundTool.execute(params);
        
        assertFalse(result.isSuccess());
        assertTrue(result.getError().contains("订单号不能为空"));
    }
    
    @Test
    void testSubmitRefundRequestWithMissingReason() {
        Map<String, Object> params = new HashMap<>();
        params.put("action", "submit");
        params.put("order_id", "ORD202311001");
        // 缺少 reason
        
        ToolResult result = refundTool.execute(params);
        
        assertFalse(result.isSuccess());
        assertTrue(result.getError().contains("退款原因不能为空"));
    }
    
    @Test
    void testSubmitRefundRequestWithInvalidReason() {
        Map<String, Object> params = new HashMap<>();
        params.put("action", "submit");
        params.put("order_id", "ORD202311001");
        params.put("reason", "无效原因");
        
        ToolResult result = refundTool.execute(params);
        
        assertFalse(result.isSuccess());
        assertTrue(result.getError().contains("无效的退款原因"));
    }
    
    @Test
    void testSubmitAndQueryRefundRequest() {
        // 先提交退款申请
        Map<String, Object> submitParams = new HashMap<>();
        submitParams.put("action", "submit");
        submitParams.put("order_id", "ORD202311002");
        submitParams.put("reason", "商品与描述不符");
        
        ToolResult submitResult = refundTool.execute(submitParams);
        assertTrue(submitResult.isSuccess());
        
        @SuppressWarnings("unchecked")
        String refundId = (String) ((Map<String, Object>) submitResult.getData()).get("refund_id");
        
        // 然后查询退款状态
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("action", "query");
        queryParams.put("refund_id", refundId);
        
        ToolResult queryResult = refundTool.execute(queryParams);
        
        assertTrue(queryResult.isSuccess());
        assertNotNull(queryResult.getData());
        
        @SuppressWarnings("unchecked")
        Map<String, Object> data = (Map<String, Object>) queryResult.getData();
        assertTrue(data.containsKey("refund_info"));
        assertTrue(data.containsKey("status_description"));
        
        @SuppressWarnings("unchecked")
        RefundTool.RefundRecord refundInfo = (RefundTool.RefundRecord) data.get("refund_info");
        assertEquals(refundId, refundInfo.getRefundId());
        assertEquals("ORD202311002", refundInfo.getOrderId());
        assertEquals("商品与描述不符", refundInfo.getReason());
        assertEquals("处理中", refundInfo.getStatus());
    }
    
    @Test
    void testQueryNonExistingRefund() {
        Map<String, Object> params = new HashMap<>();
        params.put("action", "query");
        params.put("refund_id", "NONEXISTING");
        
        ToolResult result = refundTool.execute(params);
        
        assertFalse(result.isSuccess());
        assertTrue(result.getError().contains("不存在"));
    }
    
    @Test
    void testQueryWithEmptyRefundId() {
        Map<String, Object> params = new HashMap<>();
        params.put("action", "query");
        params.put("refund_id", "");
        
        ToolResult result = refundTool.execute(params);
        
        assertFalse(result.isSuccess());
        assertTrue(result.getError().contains("退款申请ID不能为空"));
    }
    
    @Test
    void testInvalidAction() {
        Map<String, Object> params = new HashMap<>();
        params.put("action", "invalid");
        
        ToolResult result = refundTool.execute(params);
        
        assertFalse(result.isSuccess());
        assertTrue(result.getError().contains("不支持的操作类型"));
    }
    
    @Test
    void testMissingAction() {
        Map<String, Object> params = new HashMap<>();
        // 不包含 action 参数
        
        ToolResult result = refundTool.execute(params);
        
        assertFalse(result.isSuccess());
        assertTrue(result.getError().contains("操作类型(action)不能为空"));
    }
}