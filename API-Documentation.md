# SmartCustom API 文档

## 概述

SmartCustom 是一个基于 Spring AI 的智能定制平台，提供聊天服务、工具管理和发票处理等功能。

## 基础信息

- **基础URL**: `http://localhost:8080`
- **内容类型**: `application/json`
- **字符编码**: `UTF-8`

## 聊天服务 API

### 1. 发送聊天消息

**请求**:
```
POST /api/chat
Content-Type: application/json

{
  "message": "你好",
  "sessionId": "会话ID（可选）",
  "userId": "用户ID",
  "stream": false,
  "model": "gpt-3.5-turbo",
  "temperature": 0.7,
  "maxTokens": 1000
}
```

**响应**:
```json
{
  "sessionId": "会话ID",
  "response": "AI响应内容",
  "messageId": "消息ID",
  "success": true,
  "timestamp": "2023-10-01T12:00:00",
  "usage": {
    "promptTokens": 10,
    "completionTokens": 20,
    "totalTokens": 30
  },
  "error": null
}
```

### 2. 异步聊天

**请求**:
```
POST /api/chat/async
Content-Type: application/json

{
  "message": "你好",
  "sessionId": "会话ID（可选）",
  "userId": "用户ID",
  "stream": true,
  "model": "gpt-3.5-turbo",
  "temperature": 0.7,
  "maxTokens": 1000
}
```

**响应**: 与同步聊天相同格式

### 3. 获取会话消息

**请求**:
```
GET /api/chat/session/{sessionId}
```

**响应**:
```json
[
  {
    "id": "消息ID",
    "sessionId": "会话ID",
    "type": "USER|ASSISTANT|SYSTEM",
    "content": "消息内容",
    "timestamp": "2023-10-01T12:00:00",
    "read": true,
    "usage": {
      "promptTokens": 10,
      "completionTokens": 20,
      "totalTokens": 30
    }
  }
]
```

### 4. 创建新会话

**请求**:
```
POST /api/chat/session?userId=用户ID
```

**响应**: 新创建的会话ID

### 5. 清空会话

**请求**:
```
DELETE /api/chat/session/{sessionId}
```

**响应**: `true` 表示成功，`false` 表示失败

## 工具管理 API

### 1. 获取所有工具

**请求**:
```
GET /api/tools
```

**响应**:
```json
{
  "工具名称": {
    "name": "工具名称",
    "description": "工具描述",
    "parameters": {
      "参数名": "参数描述"
    }
  }
}
```

### 2. 获取已启用工具

**请求**:
```
GET /api/tools/enabled
```

**响应**: 工具名称列表

### 3. 获取指定工具

**请求**:
```
GET /api/tools/{toolName}
```

**响应**:
```json
{
  "name": "工具名称",
  "description": "工具描述",
  "parameters": {
    "参数名": "参数描述"
  }
}
```

### 4. 执行工具

**请求**:
```
POST /api/tools/{toolName}/execute
Content-Type: application/json

{
  "参数名": "参数值"
}
```

**响应**:
```json
{
  "success": true,
  "message": "执行成功消息",
  "data": {
    "结果数据"
  },
  "error": null,
  "metadata": {
    "元数据"
  },
  "timestamp": "2023-10-01T12:00:00"
}
```

### 5. 启用/禁用工具

**请求**:
```
POST /api/tools/{toolName}/enable
DELETE /api/tools/{toolName}/disable
```

**响应**: `true` 表示成功，`false` 表示失败

## 内置工具

### 1. 发票工具 (invoice)

**功能**: 发票管理，支持创建、查询和列出发票

**参数**:
- `action`: 操作类型 (create/query/list)
- `id`: 发票ID (查询时需要)
- `customer`: 客户名称 (创建时需要)
- `amount`: 金额 (创建时需要)
- `date`: 日期 (创建时需要)

**示例**:
```json
{
  "action": "create",
  "customer": "测试客户",
  "amount": 1000.0,
  "date": "2023-10-01"
}
```

### 2. 计算器工具 (calculator)

**功能**: 数学计算，支持基本运算和表达式求值

**参数**:
- `operation`: 操作类型 (add/subtract/multiply/divide/power/sqrt/percent/evaluate)
- `a`: 第一个操作数
- `b`: 第二个操作数 (部分操作需要)
- `expression`: 表达式 (evaluate操作需要)

**示例**:
```json
{
  "operation": "add",
  "a": 10,
  "b": 5
}
```

### 3. 日期时间工具 (datetime)

**功能**: 日期时间处理，支持格式化、计算和解析

**参数**:
- `action`: 操作类型 (format/diff/add/subtract/now/parse)
- `date`: 日期字符串
- `format`: 日期格式
- `amount`: 时间数量
- `unit`: 时间单位 (days/months/years/hours/minutes/seconds)

**示例**:
```json
{
  "action": "format",
  "date": "2023-10-01T12:00:00",
  "format": "yyyy-MM-dd HH:mm:ss"
}
```

## 错误处理

所有API在出错时会返回适当的HTTP状态码和错误信息：

- `400 Bad Request`: 请求参数错误
- `404 Not Found`: 资源不存在
- `500 Internal Server Error`: 服务器内部错误

错误响应格式:
```json
{
  "success": false,
  "error": "错误描述",
  "timestamp": "2023-10-01T12:00:00"
}
```

## 示例代码

### JavaScript (使用 fetch)

```javascript
// 发送聊天消息
const chatResponse = await fetch('/api/chat', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json',
  },
  body: JSON.stringify({
    message: '你好',
    userId: 'user123'
  })
});

const chatData = await chatResponse.json();
console.log(chatData.response);

// 执行工具
const toolResponse = await fetch('/api/tools/calculator/execute', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json',
  },
  body: JSON.stringify({
    operation: 'add',
    a: 10,
    b: 5
  })
});

const toolData = await toolResponse.json();
console.log(toolData.data.result); // 15
```

### Python (使用 requests)

```python
import requests
import json

# 发送聊天消息
chat_response = requests.post(
    'http://localhost:8080/api/chat',
    headers={'Content-Type': 'application/json'},
    data=json.dumps({
        'message': '你好',
        'userId': 'user123'
    })
)

chat_data = chat_response.json()
print(chat_data['response'])

# 执行工具
tool_response = requests.post(
    'http://localhost:8080/api/tools/calculator/execute',
    headers={'Content-Type': 'application/json'},
    data=json.dumps({
        'operation': 'add',
        'a': 10,
        'b': 5
    })
)

tool_data = tool_response.json()
print(tool_data['data']['result'])  # 15
```