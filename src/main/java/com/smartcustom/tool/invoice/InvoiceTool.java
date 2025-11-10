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
    private static final String DESCRIPTION = "用于处理发票相关操作，包括创建、查询和管理发票";
    
    public InvoiceTool() {
        super(NAME, DESCRIPTION);
        
        // 定义工具参数
        Map<String, Object> createParams = new HashMap<>();
        createParams.put("type", "object");
        Map<String, Object> createProperties = new HashMap<>();
        
        Map<String, Object> invoiceNumber = new HashMap<>();
        invoiceNumber.put("type", "string");
        invoiceNumber.put("description", "发票号码");
        createProperties.put("invoiceNumber", invoiceNumber);
        
        Map<String, Object> customerName = new HashMap<>();
        customerName.put("type", "string");
        customerName.put("description", "客户名称");
        createProperties.put("customerName", customerName);
        
        Map<String, Object> amount = new HashMap<>();
        amount.put("type", "string");
        amount.put("description", "发票金额");
        createProperties.put("amount", amount);
        
        Map<String, Object> issueDate = new HashMap<>();
        issueDate.put("type", "string");
        issueDate.put("description", "开票日期 (格式: yyyy-MM-dd)");
        createProperties.put("issueDate", issueDate);
        
        Map<String, Object> dueDate = new HashMap<>();
        dueDate.put("type", "string");
        dueDate.put("description", "到期日期 (格式: yyyy-MM-dd)");
        createProperties.put("dueDate", dueDate);
        
        createProperties.put("required", new String[]{"invoiceNumber", "customerName", "amount"});
        createParams.put("properties", createProperties);
        
        addParameter("create", createParams);
        
        // 查询参数
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("type", "object");
        Map<String, Object> queryProperties = new HashMap<>();
        
        Map<String, Object> invoiceNumberParam = new HashMap<>();
        invoiceNumberParam.put("type", "string");
        invoiceNumberParam.put("description", "发票号码");
        queryProperties.put("invoiceNumber", invoiceNumberParam);
        
        Map<String, Object> customerNameParam = new HashMap<>();
        customerNameParam.put("type", "string");
        customerNameParam.put("description", "客户名称");
        queryProperties.put("customerName", customerNameParam);
        
        queryParams.put("properties", queryProperties);
        addParameter("query", queryParams);
    }
    
    @Override
    protected ToolResult doExecute(Map<String, Object> parameters) {
        String action = (String) parameters.get("action");
        
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
            String invoiceNumber = (String) parameters.get("invoiceNumber");
            String customerName = (String) parameters.get("customerName");
            String amountStr = (String) parameters.get("amount");
            String issueDateStr = (String) parameters.get("issueDate");
            String dueDateStr = (String) parameters.get("dueDate");
            
            // 验证必填字段
            if (invoiceNumber == null || invoiceNumber.trim().isEmpty()) {
                return ToolResult.error("发票号码不能为空");
            }
            
            if (customerName == null || customerName.trim().isEmpty()) {
                return ToolResult.error("客户名称不能为空");
            }
            
            if (amountStr == null || amountStr.trim().isEmpty()) {
                return ToolResult.error("发票金额不能为空");
            }
            
            // 解析金额
            BigDecimal amount;
            try {
                amount = new BigDecimal(amountStr);
            } catch (NumberFormatException e) {
                return ToolResult.error("无效的金额格式: " + amountStr);
            }
            
            // 解析日期
            LocalDate issueDate = LocalDate.now();
            if (issueDateStr != null && !issueDateStr.trim().isEmpty()) {
                try {
                    issueDate = LocalDate.parse(issueDateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                } catch (Exception e) {
                    return ToolResult.error("无效的开票日期格式: " + issueDateStr);
                }
            }
            
            LocalDate dueDate = issueDate.plusDays(30); // 默认30天后到期
            if (dueDateStr != null && !dueDateStr.trim().isEmpty()) {
                try {
                    dueDate = LocalDate.parse(dueDateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                } catch (Exception e) {
                    return ToolResult.error("无效的到期日期格式: " + dueDateStr);
                }
            }
            
            // 创建发票对象（实际应用中应保存到数据库）
            Map<String, Object> invoice = new HashMap<>();
            invoice.put("id", UUID.randomUUID().toString());
            invoice.put("invoiceNumber", invoiceNumber);
            invoice.put("customerName", customerName);
            invoice.put("amount", amount);
            invoice.put("issueDate", issueDate);
            invoice.put("dueDate", dueDate);
            invoice.put("status", "UNPAID");
            invoice.put("createdAt", LocalDate.now());
            
            // 返回成功结果
            return ToolResult.success("发票创建成功", invoice);
            
        } catch (Exception e) {
            return ToolResult.error("创建发票时出错: " + e.getMessage());
        }
    }
    
    private ToolResult queryInvoice(Map<String, Object> parameters) {
        try {
            String invoiceNumber = (String) parameters.get("invoiceNumber");
            String customerName = (String) parameters.get("customerName");
            
            // 这里应该从数据库查询发票，为演示目的返回模拟数据
            Map<String, Object> invoice = new HashMap<>();
            invoice.put("id", UUID.randomUUID().toString());
            invoice.put("invoiceNumber", invoiceNumber != null ? invoiceNumber : "INV-2023-001");
            invoice.put("customerName", customerName != null ? customerName : "示例客户");
            invoice.put("amount", new BigDecimal("1000.00"));
            invoice.put("issueDate", LocalDate.now());
            invoice.put("dueDate", LocalDate.now().plusDays(30));
            invoice.put("status", "UNPAID");
            invoice.put("createdAt", LocalDate.now());
            
            return ToolResult.success("查询成功", invoice);
            
        } catch (Exception e) {
            return ToolResult.error("查询发票时出错: " + e.getMessage());
        }
    }
    
    private ToolResult listInvoices(Map<String, Object> parameters) {
        try {
            // 这里应该从数据库查询发票列表，为演示目的返回模拟数据
            Map<String, Object> invoice1 = new HashMap<>();
            invoice1.put("id", UUID.randomUUID().toString());
            invoice1.put("invoiceNumber", "INV-2023-001");
            invoice1.put("customerName", "客户A");
            invoice1.put("amount", new BigDecimal("1000.00"));
            invoice1.put("issueDate", LocalDate.now());
            invoice1.put("dueDate", LocalDate.now().plusDays(30));
            invoice1.put("status", "UNPAID");
            
            Map<String, Object> invoice2 = new HashMap<>();
            invoice2.put("id", UUID.randomUUID().toString());
            invoice2.put("invoiceNumber", "INV-2023-002");
            invoice2.put("customerName", "客户B");
            invoice2.put("amount", new BigDecimal("2000.00"));
            invoice2.put("issueDate", LocalDate.now().minusDays(10));
            invoice2.put("dueDate", LocalDate.now().plusDays(20));
            invoice2.put("status", "PAID");
            
            Map<String, Object> result = new HashMap<>();
            result.put("invoices", new Object[]{invoice1, invoice2});
            result.put("total", 2);
            
            return ToolResult.success("查询成功", result);
            
        } catch (Exception e) {
            return ToolResult.error("查询发票列表时出错: " + e.getMessage());
        }
    }
}