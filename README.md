# SmartCustom - 智能定制平台

[![Java](https://img.shields.io/badge/Java-17+-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.1.0-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Spring AI](https://img.shields.io/badge/Spring%20AI-0.8.0-blue.svg)](https://spring.io/projects/spring-ai)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

SmartCustom 是一个基于 Spring AI 的智能定制平台，提供聊天服务、工具管理和发票处理等功能。

## 功能特性

- 🤖 **智能聊天**: 基于 Spring AI 的智能对话服务，支持会话管理和历史记录
- 🔧 **工具系统**: 可扩展的工具插件系统，支持自定义工具开发
- 📦 **订单管理**: 内置订单查询和状态跟踪功能
- 💰 **退款处理**: 支持退款申请提交和状态查询
- 📊 **业务工具**: 内置发票管理、计算器、日期时间等实用工具
- 🚀 **高性能**: 异步处理和缓存机制，提供高性能服务
- 📚 **完整文档**: 详细的API文档和开发指南

## 快速开始

### 环境要求

- Java 17+
- Maven 3.6+
- Spring Boot 3.1.0+

### 安装与运行

1. 克隆项目
```bash
git clone https://github.com/your-username/java-smart-custom.git
cd java-smart-custom
```

2. 配置应用
```bash
cp src/main/resources/application.yml.example src/main/resources/application.yml
# 编辑 application.yml 文件，配置必要的参数
```

3. 构建项目
```bash
mvn clean install
```

4. 运行应用
```bash
mvn spring-boot:run
```

应用将在 `http://localhost:8080` 启动。

### Docker 部署

1. 构建镜像
```bash
docker build -t smart-custom .
```

2. 运行容器
```bash
docker run -p 8080:8080 smart-custom
```

## 使用指南

### 聊天服务

发送聊天消息：
```bash
curl -X POST http://localhost:8080/api/chat \
  -H "Content-Type: application/json" \
  -d '{
    "message": "你好",
    "userId": "user123"
  }'
```

### 工具系统

获取所有可用工具：
```bash
curl http://localhost:8080/api/tools
```

执行计算器工具：
```bash
curl -X POST http://localhost:8080/api/tools/calculator/execute \
  -H "Content-Type: application/json" \
  -d '{
    "operation": "add",
    "a": 10,
    "b": 5
  }'
```

查询订单状态：
```bash
curl -X POST http://localhost:8080/api/tools/order/execute \
  -H "Content-Type: application/json" \
  -d '{
    "action": "query",
    "order_id": "ORD202311001"
  }'
```

申请退款：
```bash
curl -X POST http://localhost:8080/api/tools/refund/execute \
  -H "Content-Type: application/json" \
  -d '{
    "action": "submit",
    "order_id": "ORD202311001",
    "reason": "不想要了",
    "amount": 299.00
  }'
```

### 发票管理

创建新发票：
```bash
curl -X POST http://localhost:8080/api/tools/invoice/execute \
  -H "Content-Type: application/json" \
  -d '{
    "action": "create",
    "customer": "测试客户",
    "amount": 1000.0,
    "date": "2023-10-01"
  }'
```

## 开发指南

### 项目结构

```
src/main/java/com/smartcustom/
├── config/           # 配置类
├── controller/       # REST API 控制器
├── model/           # 数据模型
│   └── dto/         # 数据传输对象
├── service/         # 业务逻辑层
│   └── impl/        # 业务逻辑实现
└── tool/            # 工具插件
    ├── invoice/     # 发票工具
    ├── calculator/  # 计算器工具
    ├── datetime/    # 日期时间工具
    ├── order/       # 订单查询工具
    └── refund/      # 退款申请工具
```

### 自定义工具开发

1. 继承 `AbstractTool` 类
2. 实现 `doExecute` 方法
3. 添加 `@Component` 注解

示例：
```java
@Component
public class MyCustomTool extends AbstractTool {
    
    public MyCustomTool() {
        super("my-tool", "我的自定义工具", Map.of("input", "输入参数"));
    }
    
    @Override
    protected ToolResult doExecute(Map<String, Object> parameters) {
        String input = (String) parameters.get("input");
        // 实现工具逻辑
        return ToolResult.success("执行成功", Map.of("result", input));
    }
}
```

### 配置说明

主要配置项：

```yaml
smartcustom:
  ai:
    chat:
      model: gpt-3.5-turbo
      temperature: 0.7
      max-tokens: 1000
  tool:
    cache:
      enabled: true
      ttl: 3600
  session:
    max-age: 3600
    max-messages: 50
```

## API 文档

详细的 API 文档请参考 [API-Documentation.md](API-Documentation.md)

## 测试

运行单元测试：
```bash
mvn test
```

运行集成测试：
```bash
mvn test -P integration-test
```

## 贡献指南

1. Fork 项目
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 开启 Pull Request

## 许可证

本项目采用 MIT 许可证 - 详见 [LICENSE](LICENSE) 文件

## 联系方式

- 项目主页: https://github.com/your-username/java-smart-custom
- 问题反馈: https://github.com/your-username/java-smart-custom/issues
- 邮箱: your-email@example.com

## 致谢

感谢以下开源项目：

- [Spring Boot](https://spring.io/projects/spring-boot)
- [Spring AI](https://spring.io/projects/spring-ai)
- [OpenAI API](https://openai.com/api/)