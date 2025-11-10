package com.smartcustom.tool.order;

import com.smartcustom.tool.AbstractTool;
import com.smartcustom.tool.ToolResult;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 订单查询工具
 * 提供订单状态查询、物流信息跟踪等功能
 */
@Component
public class OrderQueryTool extends AbstractTool {
    
    private final Map<String, OrderInfo> ordersDatabase = new ConcurrentHashMap<>();
    
    public OrderQueryTool() {
        super("order-query", "订单查询工具，用于查询订单状态和物流信息");
        
        // 添加参数定义
        addParameter("order_id", "订单号");
        
        // 初始化模拟订单数据
        initMockData();
    }
    
    /**
     * 初始化模拟订单数据
     */
    private void initMockData() {
        LocalDate now = LocalDate.now();
        
        // 订单1：已发货
        OrderInfo order1 = new OrderInfo();
        order1.setOrderId("ORD202311001");
        order1.setStatus("已发货");
        order1.setProductName("智能手表");
        order1.setOrderDate(now.minusDays(2).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        order1.setEstimatedDelivery(now.plusDays(3).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        order1.setTrackingNumber("SF1234567890");
        order1.setLogisticsCompany("顺丰快递");
        order1.setLogisticsStatus("运输中");
        order1.setCurrentLocation("上海转运中心");
        ordersDatabase.put(order1.getOrderId(), order1);
        
        // 订单2：已签收
        OrderInfo order2 = new OrderInfo();
        order2.setOrderId("ORD202311002");
        order2.setStatus("已签收");
        order2.setProductName("无线耳机");
        order2.setOrderDate(now.minusDays(4).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        order2.setDeliveryDate(now.minusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        order2.setTrackingNumber("YT9876543210");
        order2.setLogisticsCompany("圆通快递");
        order2.setLogisticsStatus("已签收");
        order2.setCurrentLocation("已送达");
        ordersDatabase.put(order2.getOrderId(), order2);
        
        // 订单3：处理中
        OrderInfo order3 = new OrderInfo();
        order3.setOrderId("ORD202311003");
        order3.setStatus("处理中");
        order3.setProductName("智能音箱");
        order3.setOrderDate(now.minusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        order3.setEstimatedDelivery(now.plusDays(5).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        order3.setTrackingNumber(null);
        order3.setLogisticsCompany(null);
        order3.setLogisticsStatus("仓库处理中");
        order3.setCurrentLocation("北京仓库");
        ordersDatabase.put(order3.getOrderId(), order3);
    }
    
    @Override
    protected ToolResult doExecute(Map<String, Object> parameters) {
        try {
            // 模拟查询延迟
            Thread.sleep(500);
            
            String orderId = (String) parameters.get("order_id");
            if (orderId == null || orderId.trim().isEmpty()) {
                return ToolResult.error("订单号不能为空");
            }
            
            OrderInfo orderInfo = ordersDatabase.get(orderId);
            if (orderInfo == null) {
                return ToolResult.error("订单号 " + orderId + " 不存在，请检查订单号是否正确");
            }
            
            // 构建订单状态描述
            String statusDescription = getOrderStatusDescription(orderInfo);
            
            Map<String, Object> result = new HashMap<>();
            result.put("order_info", orderInfo);
            result.put("status_description", statusDescription);
            
            return ToolResult.success("查询成功", result);
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return ToolResult.error("查询过程中被中断");
        } catch (Exception e) {
            return ToolResult.error("查询订单时出错: " + e.getMessage());
        }
    }
    
    /**
     * 获取订单状态描述
     */
    private String getOrderStatusDescription(OrderInfo orderInfo) {
        String status = orderInfo.getStatus();
        String orderId = orderInfo.getOrderId();
        String productName = orderInfo.getProductName();
        
        switch (status) {
            case "已发货":
                return String.format("您的订单 %s（%s）已发货，物流公司：%s，" +
                        "运单号：%s，当前位置：%s，" +
                        "预计送达时间：%s。",
                        orderId, productName, orderInfo.getLogisticsCompany(),
                        orderInfo.getTrackingNumber(), orderInfo.getCurrentLocation(),
                        orderInfo.getEstimatedDelivery());
                        
            case "已签收":
                return String.format("您的订单 %s（%s）已于 %s 签收成功。" +
                        "感谢您的购买，如有问题请联系客服。",
                        orderId, productName, orderInfo.getDeliveryDate());
                        
            case "处理中":
                return String.format("您的订单 %s（%s）正在%s处理中，" +
                        "预计 %s 发货，请耐心等待。",
                        orderId, productName, orderInfo.getCurrentLocation(),
                        orderInfo.getEstimatedDelivery());
                        
            default:
                return String.format("您的订单 %s 当前状态为：%s，如需了解更多信息请联系客服。",
                        orderId, status);
        }
    }
    
    /**
     * 订单信息内部类
     */
    public static class OrderInfo {
        private String orderId;
        private String status;
        private String productName;
        private String orderDate;
        private String deliveryDate;
        private String estimatedDelivery;
        private String trackingNumber;
        private String logisticsCompany;
        private String logisticsStatus;
        private String currentLocation;
        
        // Getters and Setters
        public String getOrderId() {
            return orderId;
        }
        
        public void setOrderId(String orderId) {
            this.orderId = orderId;
        }
        
        public String getStatus() {
            return status;
        }
        
        public void setStatus(String status) {
            this.status = status;
        }
        
        public String getProductName() {
            return productName;
        }
        
        public void setProductName(String productName) {
            this.productName = productName;
        }
        
        public String getOrderDate() {
            return orderDate;
        }
        
        public void setOrderDate(String orderDate) {
            this.orderDate = orderDate;
        }
        
        public String getDeliveryDate() {
            return deliveryDate;
        }
        
        public void setDeliveryDate(String deliveryDate) {
            this.deliveryDate = deliveryDate;
        }
        
        public String getEstimatedDelivery() {
            return estimatedDelivery;
        }
        
        public void setEstimatedDelivery(String estimatedDelivery) {
            this.estimatedDelivery = estimatedDelivery;
        }
        
        public String getTrackingNumber() {
            return trackingNumber;
        }
        
        public void setTrackingNumber(String trackingNumber) {
            this.trackingNumber = trackingNumber;
        }
        
        public String getLogisticsCompany() {
            return logisticsCompany;
        }
        
        public void setLogisticsCompany(String logisticsCompany) {
            this.logisticsCompany = logisticsCompany;
        }
        
        public String getLogisticsStatus() {
            return logisticsStatus;
        }
        
        public void setLogisticsStatus(String logisticsStatus) {
            this.logisticsStatus = logisticsStatus;
        }
        
        public String getCurrentLocation() {
            return currentLocation;
        }
        
        public void setCurrentLocation(String currentLocation) {
            this.currentLocation = currentLocation;
        }
    }
}