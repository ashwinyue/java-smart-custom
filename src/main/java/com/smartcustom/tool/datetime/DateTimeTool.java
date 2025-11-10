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
                    return formatDate(parameters);
                case "calculate":
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
    
    private ToolResult calculateDifference(Map<String, Object> parameters) {
        String startDateStr = (String) parameters.get("startDate");
        String endDateStr = (String) parameters.get("endDate");
        String unit = (String) parameters.get("unit");
        
        if (startDateStr == null || startDateStr.trim().isEmpty()) {
            return ToolResult.error("开始日期不能为空");
        }
        
        if (endDateStr == null || endDateStr.trim().isEmpty()) {
            return ToolResult.error("结束日期不能为空");
        }
        
        if (unit == null || unit.trim().isEmpty()) {
            unit = "days";
        }
        
        try {
            LocalDate startDate = LocalDate.parse(startDateStr);
            LocalDate endDate = LocalDate.parse(endDateStr);
            
            long difference;
            switch (unit.toLowerCase()) {
                case "days":
                    difference = ChronoUnit.DAYS.between(startDate, endDate);
                    break;
                case "weeks":
                    difference = ChronoUnit.WEEKS.between(startDate, endDate);
                    break;
                case "months":
                    difference = ChronoUnit.MONTHS.between(startDate, endDate);
                    break;
                case "years":
                    difference = ChronoUnit.YEARS.between(startDate, endDate);
                    break;
                default:
                    return ToolResult.error("不支持的时间单位: " + unit);
            }
            
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("difference", difference);
            resultMap.put("unit", unit);
            resultMap.put("startDate", startDate);
            resultMap.put("endDate", endDate);
            
            return ToolResult.success("日期差计算完成", resultMap);
        } catch (Exception e) {
            return ToolResult.error("日期差计算失败: " + e.getMessage());
        }
    }
    
    private ToolResult addOrSubtractTime(Map<String, Object> parameters) {
        String baseDateStr = (String) parameters.get("baseDate");
        Integer amount = (Integer) parameters.get("amount");
        String timeUnit = (String) parameters.get("timeUnit");
        String operation = (String) parameters.get("operation");
        
        if (baseDateStr == null || baseDateStr.trim().isEmpty()) {
            return ToolResult.error("基准日期不能为空");
        }
        
        if (amount == null) {
            return ToolResult.error("数量不能为空");
        }
        
        if (timeUnit == null || timeUnit.trim().isEmpty()) {
            timeUnit = "days";
        }
        
        if (operation == null || operation.trim().isEmpty()) {
            operation = "add";
        }
        
        try {
            LocalDate baseDate = LocalDate.parse(baseDateStr);
            LocalDate resultDate;
            
            switch (timeUnit.toLowerCase()) {
                case "days":
                    resultDate = operation.equalsIgnoreCase("add") ? 
                            baseDate.plusDays(amount) : baseDate.minusDays(amount);
                    break;
                case "weeks":
                    resultDate = operation.equalsIgnoreCase("add") ? 
                            baseDate.plusWeeks(amount) : baseDate.minusWeeks(amount);
                    break;
                case "months":
                    resultDate = operation.equalsIgnoreCase("add") ? 
                            baseDate.plusMonths(amount) : baseDate.minusMonths(amount);
                    break;
                case "years":
                    resultDate = operation.equalsIgnoreCase("add") ? 
                            baseDate.plusYears(amount) : baseDate.minusYears(amount);
                    break;
                default:
                    return ToolResult.error("不支持的时间单位: " + timeUnit);
            }
            
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("resultDate", resultDate);
            resultMap.put("baseDate", baseDate);
            resultMap.put("amount", amount);
            resultMap.put("timeUnit", timeUnit);
            resultMap.put("operation", operation);
            
            return ToolResult.success("日期计算完成", resultMap);
        } catch (Exception e) {
            return ToolResult.error("日期计算失败: " + e.getMessage());
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