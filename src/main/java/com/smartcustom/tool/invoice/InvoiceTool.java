package com.smartcustom.tool.invoice;

import com.smartcustom.tool.AbstractTool;
import com.smartcustom.tool.ToolResult;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 发票工具
 * 
 * @author SmartCustom Team
 */
@Component
public class InvoiceTool extends AbstractTool {
    private static final String NAME = "invoice";
    private static final String DESCRIPTION = "发票管理工具，用于创建、查询和列出发票";
    
    // 用于存储创建的发票（实际应用中应使用数据库）
    private static final Map<String, Invoice> invoiceStore = new HashMap<>();
    
    public InvoiceTool() {
        super(NAME, DESCRIPTION);
        
        // 定义创建发票的参数
        Map<String, Object> createParams = new HashMap<>();
        createParams.put("type", "object");
        Map<String, Object> createProperties = new HashMap<>();
        
        Map<String, Object> customerName = new HashMap<>();
        customerName.put("type", "string");
        customerName.put("description", "客户名称");
        createProperties.put("customer_name", customerName);
        
        Map<String, Object> customerEmail = new HashMap<>();
        customerEmail.put("type", "string");
        customerEmail.put("description", "客户邮箱");
        createProperties.put("customer_email", customerEmail);
        
        Map<String, Object> items = new HashMap<>();
        items.put("type", "object");
        items.put("description", "商品项目");
        createProperties.put("items", items);
        
        createProperties.put("required", new String[]{"customer_name", "items"});
        createParams.put("properties", createProperties);
        
        addParameter("create", createParams);
        
        // 定义查询发票的参数
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("type", "object");
        Map<String, Object> queryProperties = new HashMap<>();
        
        Map<String, Object> invoiceId = new HashMap<>();
        invoiceId.put("type", "string");
        invoiceId.put("description", "发票ID");
        queryProperties.put("invoice_id", invoiceId);
        
        queryParams.put("properties", queryProperties);
        addParameter("query", queryParams);
        
        // 定义列表发票的参数
        Map<String, Object> listParams = new HashMap<>();
        listParams.put("type", "object");
        listParams.put("properties", new HashMap<>());
        addParameter("list", listParams);
    }
    
    @Override
    protected ToolResult doExecute(Map<String, Object> parameters) {
        String action = (String) parameters.get("action");
        
        if (action == null || action.trim().isEmpty()) {
            return ToolResult.error("操作类型不能为空");
        }
        
        if ("create".equals(action)) {
            return createInvoice(parameters);
        } else if ("query".equals(action)) {
            return queryInvoice(parameters);
        } else if ("list".equals(action)) {
            return listInvoices(parameters);
        } else {
            return ToolResult.error("不支持的操作: " + action);
        }
    }
    
    private ToolResult createInvoice(Map<String, Object> parameters) {
        try {
            String customerName = (String) parameters.get("customer_name");
            String customerEmail = (String) parameters.get("customer_email");
            @SuppressWarnings("unchecked")
            Map<String, Object> items = (Map<String, Object>) parameters.get("items");
            
            // 验证必填字段
            if (customerName == null || customerName.trim().isEmpty()) {
                return ToolResult.error("客户姓名不能为空");
            }
            
            if (items == null || items.isEmpty()) {
                return ToolResult.error("商品项目不能为空");
            }
            
            // 计算总金额
            BigDecimal totalAmount = BigDecimal.ZERO;
            for (Map.Entry<String, Object> entry : items.entrySet()) {
                @SuppressWarnings("unchecked")
                Map<String, Object> itemDetails = (Map<String, Object>) entry.getValue();
                Integer quantity = (Integer) itemDetails.get("quantity");
                Double price = (Double) itemDetails.get("price");
                
                if (quantity != null && price != null) {
                    totalAmount = totalAmount.add(BigDecimal.valueOf(quantity * price));
                }
            }
            
            // 创建发票对象（实际应用中应保存到数据库）
            String invoiceId = "INV-" + UUID.randomUUID().toString().substring(0, 8);
            Invoice invoice = new Invoice(
                UUID.randomUUID().toString(),
                invoiceId,
                "INV-" + System.currentTimeMillis(),
                customerName,
                customerEmail,
                totalAmount,
                LocalDate.now(),
                LocalDate.now().plusDays(30),
                "UNPAID"
            );
            
            // 将发票存储到内存中
            invoiceStore.put(invoiceId, invoice);
            
            // 返回成功结果
            Map<String, Object> result = new HashMap<>();
            result.put("invoice_id", invoiceId);
            result.put("message", "发票创建成功");
            return ToolResult.success("发票创建成功", result);
            
        } catch (Exception e) {
            return ToolResult.error("创建发票时出错: " + e.getMessage());
        }
    }
    
    private ToolResult queryInvoice(Map<String, Object> parameters) {
        try {
            String invoiceId = (String) parameters.get("invoice_id");
            
            // 验证必填字段
            if (invoiceId == null || invoiceId.trim().isEmpty()) {
                return ToolResult.error("发票ID不能为空");
            }
            
            // 从存储中查询发票
            Invoice invoice = invoiceStore.get(invoiceId);
            
            // 如果找不到发票，返回错误
            if (invoice == null) {
                return ToolResult.error("发票不存在");
            }
            
            Map<String, Object> result = new HashMap<>();
            result.put("invoice", invoice);
            return ToolResult.success("查询成功", result);
            
        } catch (Exception e) {
            return ToolResult.error("查询发票时出错: " + e.getMessage());
        }
    }
    
    private ToolResult listInvoices(Map<String, Object> parameters) {
        try {
            // 这里应该从数据库查询发票列表，为演示目的返回模拟数据
            Invoice invoice1 = new Invoice(
                UUID.randomUUID().toString(),
                "INV-" + UUID.randomUUID().toString().substring(0, 8),
                "INV-2023-001",
                "客户A",
                null, // customerEmail
                new BigDecimal("1000.00"),
                LocalDate.now(),
                LocalDate.now().plusDays(30),
                "UNPAID"
            );
            
            Invoice invoice2 = new Invoice(
                UUID.randomUUID().toString(),
                "INV-" + UUID.randomUUID().toString().substring(0, 8),
                "INV-2023-002",
                "客户B",
                null, // customerEmail
                new BigDecimal("2000.00"),
                LocalDate.now().minusDays(10),
                LocalDate.now().plusDays(20),
                "PAID"
            );
            
            Map<String, Object> result = new HashMap<>();
            result.put("invoices", java.util.Arrays.asList(invoice1, invoice2));
            result.put("count", 2);
            
            return ToolResult.success("查询成功", result);
            
        } catch (Exception e) {
            return ToolResult.error("查询发票列表时出错: " + e.getMessage());
        }
    }
    
    /**
     * 发票内部类
     */
    public static class Invoice {
        private String id;
        private String invoiceId;
        private String invoiceNumber;
        private String customerName;
        private String customerEmail;
        private BigDecimal totalAmount;
        private LocalDate issueDate;
        private LocalDate dueDate;
        private String status;
        
        // 构造函数
        public Invoice() {}
        
        public Invoice(String id, String invoiceId, String invoiceNumber, String customerName, 
                      String customerEmail, BigDecimal totalAmount, LocalDate issueDate, 
                      LocalDate dueDate, String status) {
            this.id = id;
            this.invoiceId = invoiceId;
            this.invoiceNumber = invoiceNumber;
            this.customerName = customerName;
            this.customerEmail = customerEmail;
            this.totalAmount = totalAmount;
            this.issueDate = issueDate;
            this.dueDate = dueDate;
            this.status = status;
        }
        
        // Getter和Setter方法
        public String getId() {
            return id;
        }
        
        public void setId(String id) {
            this.id = id;
        }
        
        public String getInvoiceId() {
            return invoiceId;
        }
        
        public void setInvoiceId(String invoiceId) {
            this.invoiceId = invoiceId;
        }
        
        public String getInvoiceNumber() {
            return invoiceNumber;
        }
        
        public void setInvoiceNumber(String invoiceNumber) {
            this.invoiceNumber = invoiceNumber;
        }
        
        public String getCustomerName() {
            return customerName;
        }
        
        public void setCustomerName(String customerName) {
            this.customerName = customerName;
        }
        
        public String getCustomerEmail() {
            return customerEmail;
        }
        
        public void setCustomerEmail(String customerEmail) {
            this.customerEmail = customerEmail;
        }
        
        public BigDecimal getTotalAmount() {
            return totalAmount;
        }
        
        public void setTotalAmount(BigDecimal totalAmount) {
            this.totalAmount = totalAmount;
        }
        
        public LocalDate getIssueDate() {
            return issueDate;
        }
        
        public void setIssueDate(LocalDate issueDate) {
            this.issueDate = issueDate;
        }
        
        public LocalDate getDueDate() {
            return dueDate;
        }
        
        public void setDueDate(LocalDate dueDate) {
            this.dueDate = dueDate;
        }
        
        public String getStatus() {
            return status;
        }
        
        public void setStatus(String status) {
            this.status = status;
        }
    }
}