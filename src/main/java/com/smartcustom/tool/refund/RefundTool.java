package com.smartcustom.tool.refund;

import com.smartcustom.tool.AbstractTool;
import com.smartcustom.tool.ToolResult;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 退款申请工具
 * 提供退款申请提交、退款状态查询等功能
 */
@Component
public class RefundTool extends AbstractTool {
    
    private final Map<String, RefundRecord> refundsDatabase = new ConcurrentHashMap<>();
    private final List<String> refundReasons = Arrays.asList(
            "商品质量问题",
            "商品与描述不符",
            "不想要了/买错了",
            "商品损坏",
            "发货延迟",
            "其他原因"
    );
    
    public RefundTool() {
        super("refund", "退款申请工具，用于提交和查询退款申请");
        
        // 添加参数定义
        addParameter("action", "操作类型：submit(提交申请)或query(查询状态)");
        addParameter("order_id", "订单号（提交申请时必需）");
        addParameter("refund_id", "退款申请ID（查询状态时必需）");
        addParameter("reason", "退款原因（提交申请时必需）");
        addParameter("description", "退款描述（可选）");
    }
    
    @Override
    protected ToolResult doExecute(Map<String, Object> parameters) {
        try {
            String action = (String) parameters.get("action");
            if (action == null) {
                return ToolResult.error("操作类型(action)不能为空");
            }
            
            switch (action.toLowerCase()) {
                case "submit":
                    return submitRefundRequest(parameters);
                case "query":
                    return queryRefundStatus(parameters);
                case "get_reasons":
                    return getRefundReasons();
                default:
                    return ToolResult.error("不支持的操作类型: " + action);
            }
        } catch (Exception e) {
            return ToolResult.error("处理退款请求时出错: " + e.getMessage());
        }
    }
    
    /**
     * 提交退款申请
     */
    private ToolResult submitRefundRequest(Map<String, Object> parameters) {
        try {
            // 模拟处理延迟
            Thread.sleep(500);
            
            String orderId = (String) parameters.get("order_id");
            String reason = (String) parameters.get("reason");
            String description = (String) parameters.getOrDefault("description", "");
            
            // 验证必需参数
            if (orderId == null || orderId.trim().isEmpty()) {
                return ToolResult.error("订单号不能为空");
            }
            
            if (reason == null || reason.trim().isEmpty()) {
                return ToolResult.error("退款原因不能为空");
            }
            
            // 验证退款原因是否有效
            if (!refundReasons.contains(reason)) {
                return ToolResult.error("无效的退款原因，请从以下选项中选择: " + String.join(", ", refundReasons));
            }
            
            // 生成退款申请ID
            String refundId = generateRefundId();
            
            // 创建退款申请记录
            RefundRecord refundRecord = new RefundRecord();
            refundRecord.setRefundId(refundId);
            refundRecord.setOrderId(orderId);
            refundRecord.setReason(reason);
            refundRecord.setDescription(description);
            refundRecord.setStatus("处理中");
            refundRecord.setApplyTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            refundRecord.setEstimatedProcessTime(
                LocalDateTime.now().withHour(23).withMinute(59).withSecond(0)
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
            );
            
            // 保存到数据库
            refundsDatabase.put(refundId, refundRecord);
            
            Map<String, Object> result = new HashMap<>();
            result.put("refund_id", refundId);
            result.put("message", String.format("您的退款申请已提交，申请编号：%s，我们将在24小时内处理您的申请。", refundId));
            
            return ToolResult.success("退款申请提交成功", result);
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return ToolResult.error("提交退款申请过程中被中断");
        } catch (Exception e) {
            return ToolResult.error("提交退款申请时出错: " + e.getMessage());
        }
    }
    
    /**
     * 查询退款申请状态
     */
    private ToolResult queryRefundStatus(Map<String, Object> parameters) {
        try {
            // 模拟查询延迟
            Thread.sleep(300);
            
            String refundId = (String) parameters.get("refund_id");
            if (refundId == null || refundId.trim().isEmpty()) {
                return ToolResult.error("退款申请ID不能为空");
            }
            
            RefundRecord refundRecord = refundsDatabase.get(refundId);
            if (refundRecord == null) {
                return ToolResult.error("退款申请编号 " + refundId + " 不存在，请检查编号是否正确");
            }
            
            // 构建退款状态描述
            String statusDescription = getRefundStatusDescription(refundRecord);
            
            Map<String, Object> result = new HashMap<>();
            result.put("refund_info", refundRecord);
            result.put("status_description", statusDescription);
            
            return ToolResult.success("查询成功", result);
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return ToolResult.error("查询退款状态过程中被中断");
        } catch (Exception e) {
            return ToolResult.error("查询退款状态时出错: " + e.getMessage());
        }
    }
    
    /**
     * 获取退款原因选项
     */
    private ToolResult getRefundReasons() {
        Map<String, Object> result = new HashMap<>();
        result.put("reasons", refundReasons);
        return ToolResult.success("获取退款原因选项成功", result);
    }
    
    /**
     * 生成退款申请ID
     */
    private String generateRefundId() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String uuid = UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
        return "REF" + timestamp + uuid;
    }
    
    /**
     * 获取退款状态描述
     */
    private String getRefundStatusDescription(RefundRecord refundInfo) {
        String refundId = refundInfo.getRefundId();
        String orderId = refundInfo.getOrderId();
        String status = refundInfo.getStatus();
        String applyTime = refundInfo.getApplyTime();
        String estimatedProcessTime = refundInfo.getEstimatedProcessTime();
        String processResult = refundInfo.getProcessResult();
        
        switch (status) {
            case "处理中":
                return String.format("您的退款申请 %s（订单号：%s）正在处理中，" +
                        "申请时间：%s，预计处理完成时间：%s。" +
                        "请您耐心等待，处理结果将通过短信通知您。",
                        refundId, orderId, applyTime, estimatedProcessTime);
                        
            case "已批准":
                String refundAmount = refundInfo.getRefundAmount() != null ? refundInfo.getRefundAmount() : "0.00";
                return String.format("您的退款申请 %s（订单号：%s）已批准，" +
                        "退款金额：%s元，款项将在3-5个工作日内原路退回到您的支付账户。" +
                        "如有问题请联系客服。",
                        refundId, orderId, refundAmount);
                        
            case "已拒绝":
                return String.format("您的退款申请 %s（订单号：%s）已被拒绝，" +
                        "拒绝原因：%s。" +
                        "如有疑问请联系客服。",
                        refundId, orderId, processResult != null ? processResult : "请联系客服了解详情");
                        
            default:
                return String.format("您的退款申请 %s 当前状态为：%s，如需了解更多信息请联系客服。",
                        refundId, status);
        }
    }
    
    /**
     * 退款记录内部类
     */
    public static class RefundRecord {
        private String refundId;
        private String orderId;
        private String reason;
        private String description;
        private String status;
        private String applyTime;
        private String estimatedProcessTime;
        private String refundAmount;
        private String processResult;
        
        // Getters and Setters
        public String getRefundId() {
            return refundId;
        }
        
        public void setRefundId(String refundId) {
            this.refundId = refundId;
        }
        
        public String getOrderId() {
            return orderId;
        }
        
        public void setOrderId(String orderId) {
            this.orderId = orderId;
        }
        
        public String getReason() {
            return reason;
        }
        
        public void setReason(String reason) {
            this.reason = reason;
        }
        
        public String getDescription() {
            return description;
        }
        
        public void setDescription(String description) {
            this.description = description;
        }
        
        public String getStatus() {
            return status;
        }
        
        public void setStatus(String status) {
            this.status = status;
        }
        
        public String getApplyTime() {
            return applyTime;
        }
        
        public void setApplyTime(String applyTime) {
            this.applyTime = applyTime;
        }
        
        public String getEstimatedProcessTime() {
            return estimatedProcessTime;
        }
        
        public void setEstimatedProcessTime(String estimatedProcessTime) {
            this.estimatedProcessTime = estimatedProcessTime;
        }
        
        public String getRefundAmount() {
            return refundAmount;
        }
        
        public void setRefundAmount(String refundAmount) {
            this.refundAmount = refundAmount;
        }
        
        public String getProcessResult() {
            return processResult;
        }
        
        public void setProcessResult(String processResult) {
            this.processResult = processResult;
        }
    }
}