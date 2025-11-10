package com.smartcustom.tool.invoice;

import com.smartcustom.tool.ToolResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class InvoiceToolTest {
    
    private InvoiceTool invoiceTool;
    
    @BeforeEach
    void setUp() {
        invoiceTool = new InvoiceTool();
    }
    
    @Test
    void testGetName() {
        assertEquals("invoice", invoiceTool.getName());
    }
    
    @Test
    void testGetDescription() {
        assertNotNull(invoiceTool.getDescription());
        assertTrue(invoiceTool.getDescription().contains("发票"));
    }
    
    @Test
    void testCreateInvoice() {
        Map<String, Object> params = new HashMap<>();
        params.put("action", "create");
        params.put("customer_name", "张三");
        params.put("customer_email", "zhangsan@example.com");
        params.put("items", Map.of(
            "商品A", Map.of("quantity", 2, "price", 100.00),
            "商品B", Map.of("quantity", 1, "price", 200.00)
        ));
        
        ToolResult result = invoiceTool.execute(params);
        
        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
        
        @SuppressWarnings("unchecked")
        Map<String, Object> data = (Map<String, Object>) result.getData();
        assertTrue(data.containsKey("invoice_id"));
        assertTrue(data.containsKey("message"));
        
        String invoiceId = (String) data.get("invoice_id");
        assertNotNull(invoiceId);
        assertTrue(invoiceId.startsWith("INV"));
    }
    
    @Test
    void testCreateInvoiceWithMissingCustomerName() {
        Map<String, Object> params = new HashMap<>();
        params.put("action", "create");
        params.put("customer_email", "zhangsan@example.com");
        params.put("items", Map.of(
            "商品A", Map.of("quantity", 2, "price", 100.00)
        ));
        // 缺少 customer_name
        
        ToolResult result = invoiceTool.execute(params);
        
        assertFalse(result.isSuccess());
        assertTrue(result.getError().contains("客户姓名不能为空"));
    }
    
    @Test
    void testCreateInvoiceWithMissingItems() {
        Map<String, Object> params = new HashMap<>();
        params.put("action", "create");
        params.put("customer_name", "张三");
        params.put("customer_email", "zhangsan@example.com");
        // 缺少 items
        
        ToolResult result = invoiceTool.execute(params);
        
        assertFalse(result.isSuccess());
        assertTrue(result.getError().contains("商品项目不能为空"));
    }
    
    @Test
    void testCreateAndQueryInvoice() {
        // 先创建发票
        Map<String, Object> createParams = new HashMap<>();
        createParams.put("action", "create");
        createParams.put("customer_name", "李四");
        createParams.put("customer_email", "lisi@example.com");
        createParams.put("items", Map.of(
            "商品C", Map.of("quantity", 3, "price", 150.00)
        ));
        
        ToolResult createResult = invoiceTool.execute(createParams);
        assertTrue(createResult.isSuccess());
        
        @SuppressWarnings("unchecked")
        String invoiceId = (String) ((Map<String, Object>) createResult.getData()).get("invoice_id");
        
        // 然后查询发票
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("action", "query");
        queryParams.put("invoice_id", invoiceId);
        
        ToolResult queryResult = invoiceTool.execute(queryParams);
        
        assertTrue(queryResult.isSuccess());
        assertNotNull(queryResult.getData());
        
        @SuppressWarnings("unchecked")
        Map<String, Object> data = (Map<String, Object>) queryResult.getData();
        assertTrue(data.containsKey("invoice"));
        
        @SuppressWarnings("unchecked")
        InvoiceTool.Invoice invoice = (InvoiceTool.Invoice) data.get("invoice");
        assertEquals(invoiceId, invoice.getInvoiceId());
        assertEquals("李四", invoice.getCustomerName());
        assertEquals("lisi@example.com", invoice.getCustomerEmail());
        assertEquals(0, new BigDecimal("450.00").compareTo(invoice.getTotalAmount()));
    }
    
    @Test
    void testQueryNonExistingInvoice() {
        Map<String, Object> params = new HashMap<>();
        params.put("action", "query");
        params.put("invoice_id", "NONEXISTING");
        
        ToolResult result = invoiceTool.execute(params);
        
        assertFalse(result.isSuccess());
        assertTrue(result.getError().contains("不存在"));
    }
    
    @Test
    void testQueryWithEmptyInvoiceId() {
        Map<String, Object> params = new HashMap<>();
        params.put("action", "query");
        params.put("invoice_id", "");
        
        ToolResult result = invoiceTool.execute(params);
        
        assertFalse(result.isSuccess());
        assertTrue(result.getError().contains("发票ID不能为空"));
    }
    
    @Test
    void testListInvoices() {
        Map<String, Object> params = new HashMap<>();
        params.put("action", "list");
        
        ToolResult result = invoiceTool.execute(params);
        
        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
        
        @SuppressWarnings("unchecked")
        Map<String, Object> data = (Map<String, Object>) result.getData();
        assertTrue(data.containsKey("invoices"));
        assertTrue(data.containsKey("count"));
        
        @SuppressWarnings("unchecked")
        java.util.List<InvoiceTool.Invoice> invoices = (java.util.List<InvoiceTool.Invoice>) data.get("invoices");
        Integer count = (Integer) data.get("count");
        
        assertEquals(invoices.size(), count.intValue());
    }
    
    @Test
    void testInvalidAction() {
        Map<String, Object> params = new HashMap<>();
        params.put("action", "invalid");
        
        ToolResult result = invoiceTool.execute(params);
        
        assertFalse(result.isSuccess());
        assertTrue(result.getError().contains("不支持的操作"));
    }
    
    @Test
    void testMissingAction() {
        Map<String, Object> params = new HashMap<>();
        // 不包含 action 参数
        
        ToolResult result = invoiceTool.execute(params);
        
        assertFalse(result.isSuccess());
        assertTrue(result.getError().contains("操作类型不能为空"));
    }
    
    @Test
    void testCalculateTotalAmount() {
        Map<String, Object> params = new HashMap<>();
        params.put("action", "create");
        params.put("customer_name", "测试用户");
        params.put("customer_email", "test@example.com");
        params.put("items", Map.of(
            "商品1", Map.of("quantity", 2, "price", 50.00),
            "商品2", Map.of("quantity", 3, "price", 30.00)
        ));
        
        ToolResult result = invoiceTool.execute(params);
        assertTrue(result.isSuccess());
        
        @SuppressWarnings("unchecked")
        String invoiceId = (String) ((Map<String, Object>) result.getData()).get("invoice_id");
        
        // 查询发票验证总金额
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("action", "query");
        queryParams.put("invoice_id", invoiceId);
        
        ToolResult queryResult = invoiceTool.execute(queryParams);
        assertTrue(queryResult.isSuccess());
        
        @SuppressWarnings("unchecked")
        InvoiceTool.Invoice invoice = (InvoiceTool.Invoice) ((Map<String, Object>) queryResult.getData()).get("invoice");
        assertEquals(0, new BigDecimal("190.00").compareTo(invoice.getTotalAmount())); // 2*50 + 3*30 = 190
    }
}