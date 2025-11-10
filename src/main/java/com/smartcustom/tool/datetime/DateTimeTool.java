package com.smartcustom.tool.datetime;

import com.smartcustom.tool.AbstractTool;
import com.smartcustom.tool.ToolResult;
import org.springframework.stereotype.Component;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

/**
 * 日期时间工具
 * 
 * @author SmartCustom Team
 */
@Component
public class DateTimeTool extends AbstractTool {
    
    private static final String NAME = "datetime";
    private static final String DESCRIPTION = "用于处理日期和时间相关操作，包括格式化、计算和转换";
    
    public DateTimeTool() {
        super(NAME, DESCRIPTION);
        
        // 定义工具参数
        Map<String, Object> formatParams = new HashMap<>();
        formatParams.put("type", "object");
        Map<String, Object> formatProperties = new HashMap<>();
        
        Map<String, Object> date = new HashMap<>();
        date.put("type", "string");
        date.put("description", "日期字符串");
        formatProperties.put("date", date);
        
        Map<String, Object> inputFormat = new HashMap<>();
        inputFormat.put("type", "string");
        inputFormat.put("description", "输入日期格式，例如: yyyy-MM-dd");
        formatProperties.put("inputFormat", inputFormat);
        
        Map<String, Object> outputFormat = new HashMap<>();
        outputFormat.put("type", "string");
        outputFormat.put("description", "输出日期格式，例如: dd/MM/yyyy");
        formatProperties.put("outputFormat", outputFormat);
        
        formatParams.put("properties", formatProperties);
        addParameter("format", formatParams);
        
        // 计算参数
        Map<String, Object> calculateParams = new HashMap<>();
        calculateParams.put("type", "object");
        Map<String, Object> calculateProperties = new HashMap<>();
        
        Map<String, Object> startDate = new HashMap<>();
        startDate.put("type", "string");
        startDate.put("description", "开始日期");
        calculateProperties.put("startDate", startDate);
        
        Map<String, Object> endDate = new HashMap<>();
        endDate.put("type", "string");
        endDate.put("description", "结束日期");
        calculateProperties.put("endDate", endDate);
        
        Map<String, Object> unit = new HashMap<>();
        unit.put("type", "string");
        unit.put("description", "计算单位: days, weeks, months, years");
        calculateProperties.put("unit", unit);
        
        calculateParams.put("properties", calculateProperties);
        addParameter("calculate", calculateParams);
        
        // 添加/减去时间参数
        Map<String, Object> addParams = new HashMap<>();
        addParams.put("type", "object");
        Map<String, Object> addProperties = new HashMap<>();
        
        Map<String, Object> baseDate = new HashMap<>();
        baseDate.put("type", "string");
        baseDate.put("description", "基准日期");
        addProperties.put("baseDate", baseDate);
        
        Map<String, Object> amount = new HashMap<>();
        amount.put("type", "integer");
        amount.put("description", "数量");
        addProperties.put("amount", amount);
        
        Map<String, Object> timeUnit = new HashMap<>();
        timeUnit.put("type", "string");
        timeUnit.put("description", "时间单位: days, weeks, months, years");
        addProperties.put("timeUnit", timeUnit);
        
        Map<String, Object> operation = new HashMap<>();
        operation.put("type", "string");
        operation.put("description", "操作: add, subtract");
        addProperties.put("operation", operation);
        
        addParams.put("properties", addProperties);
        addParameter("add", addParams);
    }
    
    @Override
    protected ToolResult doExecute(Map<String, Object> parameters) {
        String action = (String) parameters.get("action");
        
        if (action == null) {
            return ToolResult.error("操作类型不能为空");
        }
        
        try {
            switch (action.toLowerCase()) {
                case "format":
                    return formatDateTime(parameters);
                case "calculate":
                case "difference":
                    return calculateDifference(parameters);
                case "add":
                    return addOrSubtractTime(parameters);
                case "current":
                    return getCurrentDateTime(parameters);
                case "parse":
                    return parseDate(parameters);
                default:
                    return ToolResult.error("不支持的操作: " + action);
            }
        } catch (Exception e) {
            return ToolResult.error("日期时间操作出错: " + e.getMessage());
        }
    }
    
    private ToolResult formatDateTime(Map<String, Object> parameters) {
        String datetime = (String) parameters.get("datetime");
        String format = (String) parameters.get("format");
        
        if (datetime == null || datetime.trim().isEmpty()) {
            return ToolResult.error("日期时间不能为空");
        }
        
        // 默认输出格式
        if (format == null || format.trim().isEmpty()) {
            format = "yyyy-MM-dd HH:mm:ss";
        }
        
        try {
            DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern(format);
            
            // 解析输入的日期时间
            LocalDateTime dateTime;
            if (datetime.contains("T")) {
                // ISO格式日期时间
                dateTime = LocalDateTime.parse(datetime);
            } else if (datetime.contains(" ")) {
                // 自定义格式日期时间
                DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                dateTime = LocalDateTime.parse(datetime, inputFormatter);
            } else {
                // 只是日期
                LocalDate date = LocalDate.parse(datetime);
                dateTime = date.atStartOfDay();
            }
            
            String formatted = dateTime.format(outputFormatter);
            
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("formatted", formatted);
            resultMap.put("original", datetime);
            resultMap.put("format", format);
            
            return ToolResult.success("日期时间格式化完成", resultMap);
        } catch (Exception e) {
            return ToolResult.error("日期时间格式化失败: " + e.getMessage());
        }
    }
    
    private ToolResult formatDate(Map<String, Object> parameters) {
        String dateStr = (String) parameters.get("date");
        String inputFormatStr = (String) parameters.get("inputFormat");
        String outputFormatStr = (String) parameters.get("outputFormat");
        
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return ToolResult.error("日期不能为空");
        }
        
        // 默认输入格式
        if (inputFormatStr == null || inputFormatStr.trim().isEmpty()) {
            inputFormatStr = "yyyy-MM-dd";
        }
        
        // 默认输出格式
        if (outputFormatStr == null || outputFormatStr.trim().isEmpty()) {
            outputFormatStr = "yyyy-MM-dd";
        }
        
        try {
            DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern(inputFormatStr);
            DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern(outputFormatStr);
            
            LocalDate date;
            if (dateStr.contains("T") || dateStr.contains(" ")) {
                // 可能是日期时间
                LocalDateTime dateTime = LocalDateTime.parse(dateStr, inputFormatter);
                String formattedDate = dateTime.format(outputFormatter);
                
                Map<String, Object> resultMap = new HashMap<>();
                resultMap.put("formattedDate", formattedDate);
                resultMap.put("originalDate", dateStr);
                resultMap.put("inputFormat", inputFormatStr);
                resultMap.put("outputFormat", outputFormatStr);
                
                return ToolResult.success("日期格式化完成", resultMap);
            } else {
                // 只是日期
                date = LocalDate.parse(dateStr, inputFormatter);
                String formattedDate = date.format(outputFormatter);
                
                Map<String, Object> resultMap = new HashMap<>();
                resultMap.put("formattedDate", formattedDate);
                resultMap.put("originalDate", dateStr);
                resultMap.put("inputFormat", inputFormatStr);
                resultMap.put("outputFormat", outputFormatStr);
                
                return ToolResult.success("日期格式化完成", resultMap);
            }
        } catch (Exception e) {
            return ToolResult.error("日期格式化失败: " + e.getMessage());
        }
    }
    
    private ToolResult addOrSubtractTime(Map<String, Object> parameters) {
        String datetime = (String) parameters.get("datetime");
        Integer days = (Integer) parameters.get("days");
        String operation = (String) parameters.get("operation");
        
        if (datetime == null || datetime.trim().isEmpty()) {
            return ToolResult.error("日期时间不能为空");
        }
        
        if (days == null) {
            return ToolResult.error("天数不能为空");
        }
        
        try {
            LocalDateTime dateTime;
            if (datetime.contains("T")) {
                // ISO格式日期时间
                dateTime = LocalDateTime.parse(datetime);
            } else if (datetime.contains(" ")) {
                // 自定义格式日期时间
                DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                dateTime = LocalDateTime.parse(datetime, inputFormatter);
            } else {
                // 只是日期
                LocalDate date = LocalDate.parse(datetime);
                dateTime = date.atStartOfDay();
            }
            
            LocalDateTime resultDateTime = dateTime.plusDays(days);
            
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("result", resultDateTime.toString());
            resultMap.put("original", datetime);
            resultMap.put("days", days);
            
            return ToolResult.success("日期时间计算完成", resultMap);
        } catch (Exception e) {
            return ToolResult.error("日期时间计算失败: " + e.getMessage());
        }
    }
    
    private ToolResult calculateDifference(Map<String, Object> parameters) {
        String datetime1 = (String) parameters.get("datetime1");
        String datetime2 = (String) parameters.get("datetime2");
        
        if (datetime1 == null || datetime1.trim().isEmpty()) {
            return ToolResult.error("第一个日期时间不能为空");
        }
        
        if (datetime2 == null || datetime2.trim().isEmpty()) {
            return ToolResult.error("第二个日期时间不能为空");
        }
        
        try {
            LocalDateTime dateTime1;
            LocalDateTime dateTime2;
            
            // 解析第一个日期时间
            if (datetime1.contains("T")) {
                dateTime1 = LocalDateTime.parse(datetime1);
            } else if (datetime1.contains(" ")) {
                DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                dateTime1 = LocalDateTime.parse(datetime1, inputFormatter);
            } else {
                LocalDate date = LocalDate.parse(datetime1);
                dateTime1 = date.atStartOfDay();
            }
            
            // 解析第二个日期时间
            if (datetime2.contains("T")) {
                dateTime2 = LocalDateTime.parse(datetime2);
            } else if (datetime2.contains(" ")) {
                DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                dateTime2 = LocalDateTime.parse(datetime2, inputFormatter);
            } else {
                LocalDate date = LocalDate.parse(datetime2);
                dateTime2 = date.atStartOfDay();
            }
            
            // 计算差异
            long days = ChronoUnit.DAYS.between(dateTime2, dateTime1);
            long hours = ChronoUnit.HOURS.between(dateTime2, dateTime1) % 24;
            long minutes = ChronoUnit.MINUTES.between(dateTime2, dateTime1) % 60;
            
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("days", days);
            resultMap.put("hours", hours);
            resultMap.put("minutes", minutes);
            resultMap.put("datetime1", datetime1);
            resultMap.put("datetime2", datetime2);
            
            return ToolResult.success("日期时间差计算完成", resultMap);
        } catch (Exception e) {
            return ToolResult.error("日期时间差计算失败: " + e.getMessage());
        }
    }
    
    private ToolResult getCurrentDateTime(Map<String, Object> parameters) {
        try {
            String format = (String) parameters.get("format");
            String type = (String) parameters.get("type");
            
            if (format == null || format.trim().isEmpty()) {
                format = "yyyy-MM-dd";
            }
            
            if (type == null || type.trim().isEmpty()) {
                type = "date";
            }
            
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
            String formattedDateTime;
            
            switch (type.toLowerCase()) {
                case "date":
                    formattedDateTime = LocalDate.now().format(formatter);
                    break;
                case "time":
                    formattedDateTime = LocalTime.now().format(formatter);
                    break;
                case "datetime":
                    formattedDateTime = LocalDateTime.now().format(formatter);
                    break;
                default:
                    return ToolResult.error("不支持的类型: " + type);
            }
            
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("current", formattedDateTime);
            resultMap.put("format", format);
            resultMap.put("type", type);
            
            return ToolResult.success("获取当前日期时间完成", resultMap);
        } catch (Exception e) {
            return ToolResult.error("获取当前日期时间失败: " + e.getMessage());
        }
    }
    
    private ToolResult parseDate(Map<String, Object> parameters) {
        String dateStr = (String) parameters.get("date");
        String format = (String) parameters.get("format");
        
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return ToolResult.error("日期字符串不能为空");
        }
        
        if (format == null || format.trim().isEmpty()) {
            format = "yyyy-MM-dd";
        }
        
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
            LocalDate date = LocalDate.parse(dateStr, formatter);
            
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("year", date.getYear());
            resultMap.put("month", date.getMonthValue());
            resultMap.put("day", date.getDayOfMonth());
            resultMap.put("dayOfWeek", date.getDayOfWeek().toString());
            resultMap.put("dayOfYear", date.getDayOfYear());
            resultMap.put("isLeapYear", date.isLeapYear());
            resultMap.put("originalDate", dateStr);
            resultMap.put("format", format);
            
            return ToolResult.success("日期解析完成", resultMap);
        } catch (Exception e) {
            return ToolResult.error("日期解析失败: " + e.getMessage());
        }
    }
}