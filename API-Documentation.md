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

**功能**: 发票管理，支持创建、查询和列表

**参数**:
- `action`: 操作类型 (create/query/list)
- `invoice_id`: 发票ID
- `customer_name`: 客户名称
- `amount`: 金额
- `items`: 商品列表

**示例**:
```json
{
  "action": "create",
  "customer_name": "张三",
  "amount": 100.00,
  "items": [
    {
      "name": "商品A",
      "quantity": 2,
      "price": 50.00
    }
  ]
}
```

### 2. 计算器工具 (calculator)

**功能**: 数学计算，支持基本运算

**参数**:
- `action`: 操作类型 (add/subtract/multiply/divide)
- `a`: 第一个数字
- `b`: 第二个数字

**示例**:
```json
{
  "action": "add",
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

### 4. 订单查询工具 (order)

**功能**: 订单查询和状态跟踪，支持订单状态查询和物流信息跟踪

**参数**:
- `action`: 操作类型 (query)
- `order_id`: 订单ID

**示例**:
```json
{
  "action": "query",
  "order_id": "ORD202311001"
}
```

**响应示例**:
```json
{
  "success": true,
  "message": "订单查询成功",
  "data": {
    "order_id": "ORD202311001",
    "status": "已发货",
    "status_description": "您的订单已发货，正在配送中",
    "customer_name": "张三",
    "order_date": "2023-11-10",
    "total_amount": 299.00,
    "logistics_info": {
      "current_location": "北京转运中心",
      "estimated_delivery": "2023-11-17",
      "tracking_steps": [
        {
          "time": "2023-11-10 14:30:00",
          "location": "上海仓库",
          "status": "已揽收"
        },
        {
          "time": "2023-11-11 09:15:00",
          "location": "上海转运中心",
          "status": "已发出"
        },
        {
          "time": "2023-11-12 16:45:00",
          "location": "北京转运中心",
          "status": "到达目的地"
        }
      ]
    }
  }
}
```

### 5. 退款申请工具 (refund)

**功能**: 退款申请和状态查询，支持提交退款申请、查询退款状态和获取退款原因列表

**参数**:
- `action`: 操作类型 (submit/query/get_reasons)
- `refund_id`: 退款ID (查询时需要)
- `order_id`: 订单ID (提交申请时需要)
- `reason`: 退款原因 (提交申请时需要)
- `amount`: 退款金额 (提交申请时需要)

**示例（提交退款申请）**:
```json
{
  "action": "submit",
  "order_id": "ORD202311001",
  "reason": "不想要了",
  "amount": 299.00
}
```

**响应示例（提交退款申请）**:
```json
{
  "success": true,
  "message": "退款申请提交成功",
  "data": {
    "refund_id": "REF20231115001",
    "order_id": "ORD202311001",
    "status": "已提交",
    "status_description": "退款申请已提交，等待审核",
    "reason": "不想要了",
    "amount": 299.00,
    "submit_time": "2023-11-15 10:30:00"
  }
}
```

**示例（查询退款状态）**:
```json
{
  "action": "query",
  "refund_id": "REF20231115001"
}
```

**响应示例（查询退款状态）**:
```json
{
  "success": true,
  "message": "退款状态查询成功",
  "data": {
    "refund_id": "REF20231115001",
    "order_id": "ORD202311001",
    "status": "已审核",
    "status_description": "退款申请已通过审核，将在3-5个工作日内到账",
    "reason": "不想要了",
    "amount": 299.00,
    "submit_time": "2023-11-15 10:30:00",
    "review_time": "2023-11-15 14:20:00",
    "estimated_refund_time": "2023-11-20"
  }
}
```

**示例（获取退款原因列表）**:
```json
{
  "action": "get_reasons"
}
```

**响应示例（获取退款原因列表）**:
```json
{
  "success": true,
  "message": "退款原因列表获取成功",
  "data": {
    "reasons": [
      {
        "code": "no_longer_wanted",
        "description": "不想要了"
      },
      {
        "code": "wrong_item",
        "description": "商品错发/漏发"
      },
      {
        "code": "quality_issue",
        "description": "质量问题"
      },
      {
        "code": "not_as_described",
        "description": "与描述不符"
      },
      {
        "code": "damaged",
        "description": "商品损坏"
      },
      {
        "code": "other",
        "description": "其他原因"
      }
    ]
  }
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