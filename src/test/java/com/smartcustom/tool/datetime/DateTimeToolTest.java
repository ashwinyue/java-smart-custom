package com.smartcustom.tool.datetime;

import com.smartcustom.tool.ToolResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class DateTimeToolTest {
    
    private DateTimeTool dateTimeTool;
    
    @BeforeEach
    void setUp() {
        dateTimeTool = new DateTimeTool();
    }
    
    @Test
    void testGetName() {
        assertEquals("datetime", dateTimeTool.getName());
    }
    
    @Test
    void testGetDescription() {
        assertNotNull(dateTimeTool.getDescription());
        assertTrue(dateTimeTool.getDescription().contains("日期时间"));
    }
    
    @Test
    void testGetCurrentTime() {
        Map<String, Object> params = new HashMap<>();
        params.put("action", "current");
        
        ToolResult result = dateTimeTool.execute(params);
        
        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
        
        @SuppressWarnings("unchecked")
        Map<String, Object> data = (Map<String, Object>) result.getData();
        assertTrue(data.containsKey("datetime"));
        assertTrue(data.containsKey("formatted"));
        
        String datetime = (String) data.get("datetime");
        String formatted = (String) data.get("formatted");
        
        assertNotNull(datetime);
        assertNotNull(formatted);
        assertTrue(datetime.matches("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}"));
    }
    
    @Test
    void testFormatDateTime() {
        Map<String, Object> params = new HashMap<>();
        params.put("action", "format");
        params.put("datetime", "2023-11-15T14:30:00");
        params.put("format", "yyyy年MM月dd日 HH:mm:ss");
        
        ToolResult result = dateTimeTool.execute(params);
        
        assertTrue(result.isSuccess());
        
        @SuppressWarnings("unchecked")
        Map<String, Object> data = (Map<String, Object>) result.getData();
        assertTrue(data.containsKey("formatted"));
        
        String formatted = (String) data.get("formatted");
        assertEquals("2023年11月15日 14:30:00", formatted);
    }
    
    @Test
    void testParseDateTime() {
        Map<String, Object> params = new HashMap<>();
        params.put("action", "parse");
        params.put("datetime", "2023年11月15日 14:30:00");
        params.put("format", "yyyy年MM月dd日 HH:mm:ss");
        
        ToolResult result = dateTimeTool.execute(params);
        
        assertTrue(result.isSuccess());
        
        @SuppressWarnings("unchecked")
        Map<String, Object> data = (Map<String, Object>) result.getData();
        assertTrue(data.containsKey("parsed"));
        
        String parsed = (String) data.get("parsed");
        assertEquals("2023-11-15T14:30:00", parsed);
    }
    
    @Test
    void testAddDays() {
        Map<String, Object> params = new HashMap<>();
        params.put("action", "add");
        params.put("datetime", "2023-11-15T14:30:00");
        params.put("days", 7);
        
        ToolResult result = dateTimeTool.execute(params);
        
        assertTrue(result.isSuccess());
        
        @SuppressWarnings("unchecked")
        Map<String, Object> data = (Map<String, Object>) result.getData();
        assertTrue(data.containsKey("result"));
        
        String resultDatetime = (String) data.get("result");
        assertEquals("2023-11-22T14:30:00", resultDatetime);
    }
    
    @Test
    void testSubtractDays() {
        Map<String, Object> params = new HashMap<>();
        params.put("action", "add");
        params.put("datetime", "2023-11-15T14:30:00");
        params.put("days", -3);
        
        ToolResult result = dateTimeTool.execute(params);
        
        assertTrue(result.isSuccess());
        
        @SuppressWarnings("unchecked")
        Map<String, Object> data = (Map<String, Object>) result.getData();
        assertTrue(data.containsKey("result"));
        
        String resultDatetime = (String) data.get("result");
        assertEquals("2023-11-12T14:30:00", resultDatetime);
    }
    
    @Test
    void testDifference() {
        Map<String, Object> params = new HashMap<>();
        params.put("action", "difference");
        params.put("datetime1", "2023-11-20T14:30:00");
        params.put("datetime2", "2023-11-15T10:30:00");
        
        ToolResult result = dateTimeTool.execute(params);
        
        assertTrue(result.isSuccess());
        
        @SuppressWarnings("unchecked")
        Map<String, Object> data = (Map<String, Object>) result.getData();
        assertTrue(data.containsKey("days"));
        assertTrue(data.containsKey("hours"));
        assertTrue(data.containsKey("minutes"));
        
        Long days = (Long) data.get("days");
        Long hours = (Long) data.get("hours");
        Long minutes = (Long) data.get("minutes");
        
        assertEquals(5L, days);
        assertEquals(4L, hours);
        assertEquals(0L, minutes);
    }
    
    @Test
    void testInvalidAction() {
        Map<String, Object> params = new HashMap<>();
        params.put("action", "invalid");
        
        ToolResult result = dateTimeTool.execute(params);
        
        assertFalse(result.isSuccess());
        assertTrue(result.getError().contains("不支持的操作"));
    }
    
    @Test
    void testMissingAction() {
        Map<String, Object> params = new HashMap<>();
        // 缺少 action 参数
        
        ToolResult result = dateTimeTool.execute(params);
        
        assertFalse(result.isSuccess());
        assertTrue(result.getError().contains("操作类型不能为空"));
    }
    
    @Test
    void testFormatWithMissingDatetime() {
        Map<String, Object> params = new HashMap<>();
        params.put("action", "format");
        params.put("format", "yyyy-MM-dd");
        // 缺少 datetime 参数
        
        ToolResult result = dateTimeTool.execute(params);
        
        assertFalse(result.isSuccess());
        assertTrue(result.getError().contains("日期时间不能为空"));
    }
    
    @Test
    void testFormatWithInvalidDatetime() {
        Map<String, Object> params = new HashMap<>();
        params.put("action", "format");
        params.put("datetime", "invalid-date");
        params.put("format", "yyyy-MM-dd");
        
        ToolResult result = dateTimeTool.execute(params);
        
        assertFalse(result.isSuccess());
        assertTrue(result.getError().contains("日期时间格式错误"));
    }
    
    @Test
    void testAddWithMissingDays() {
        Map<String, Object> params = new HashMap<>();
        params.put("action", "add");
        params.put("datetime", "2023-11-15T14:30:00");
        // 缺少 days 参数
        
        ToolResult result = dateTimeTool.execute(params);
        
        assertFalse(result.isSuccess());
        assertTrue(result.getError().contains("天数不能为空"));
    }
}