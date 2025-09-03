# Busuanzi 自搭建服务 - 项目文档

## 项目概述

这是一个自搭建的 Busuanzi 网站统计服务，用于替代不蒜子官方统计服务。项目基于 Spring Boot 3.0 开发，使用 Redis 作为数据存储，提供网站访问量统计功能。

### 核心功能
- **网站PV统计**：统计整个网站的总访问量
- **页面PV统计**：统计单个页面的访问量
- **网站UV统计**：统计网站独立访客数
- **页面UV统计**：统计页面独立访客数
- **JSONP支持**：兼容原版不蒜子的JSONP调用方式
- **站点地图初始化**：通过sitemap.xml批量初始化页面数据

### 技术栈
- **后端**：Spring Boot 3.0.2 + Java 17
- **数据存储**：Redis 7.x
- **序列化**：FastJSON2
- **工具库**：Hutool
- **容器化**：Docker + Docker Compose
- **构建工具**：Maven

## 项目结构

```
busuanzi/
├── src/
│   ├── main/
│   │   ├── java/com/yww/busuanzi/
│   │   │   ├── BusuanziApplication.java          # 主启动类
│   │   │   ├── api/
│   │   │   │   ├── ApiController.java           # API接口控制器
│   │   │   │   └── ApiService.java              # 业务逻辑服务
│   │   │   ├── redis/
│   │   │   │   ├── RedisConfig.java             # Redis配置
│   │   │   │   ├── RedisCache.java              # Redis缓存操作
│   │   │   │   └── FastJson2JsonRedisSerializer.java # JSON序列化器
│   │   │   ├── annotation/
│   │   │   │   ├── PreAuthorize.java            # 权限注解
│   │   │   │   └── PreAuthorizeAspect.java      # 权限切面
│   │   │   ├── auth/
│   │   │   │   ├── ApiKeyService.java           # API密钥服务
│   │   │   │   └── ApplicationStartupListener.java # 启动监听器
│   │   │   ├── exception/
│   │   │   │   ├── BusuanziException.java       # 自定义异常
│   │   │   │   └── ControllerAdviceHandler.java # 全局异常处理
│   │   │   └── util/
│   │   │       ├── IdGenerator.java             # ID生成器
│   │   │       ├── RefererUtil.java             # Referer工具类
│   │   │       └── SitemapUtil.java             # 站点地图工具类
│   │   └── resources/
│   │       ├── application.yml                  # 主配置文件
│   │       ├── application-dev.yml             # 开发环境配置
│   │       ├── application-prod.yml            # 生产环境配置
│   │       ├── static/
│   │       │   ├── busuanzi.js                  # 前端统计脚本
│   │       │   ├── busuanzi-jsonp.js           # JSONP兼容脚本
│   │       │   └── index.html                  # 测试页面
│   │       └── sitemap.xml                     # 站点地图示例
├── docker-compose.yml                           # Docker编排配置
├── Dockerfile                                   # Docker镜像构建
├── pom.xml                                     # Maven项目配置
├── redis.conf                                  # Redis配置文件
└── .gitignore                                  # Git忽略规则
```

## 快速开始

### 环境要求
- Java 17+
- Maven 3.6+
- Redis 7.x
- Docker & Docker Compose (可选)

### 本地开发

1. **启动Redis**
   ```bash
   # 使用Docker启动Redis
   docker run -d -p 6379:6379 redis:7-alpine
   
   # 或者使用项目提供的配置
   redis-server redis.conf
   ```

2. **构建项目**
   ```bash
   mvn clean package -DskipTests
   ```

3. **运行项目**
   ```bash
   # 开发环境
   mvn spring-boot:run -Dspring.profiles.active=dev
   
   # 或者直接运行jar
   java -jar target/busuanzi-1.0.jar --spring.profiles.active=dev
   ```

4. **访问测试**
   - 服务地址：http://localhost:10010
   - API文档：http://localhost:10010/busuanzi/api

### Docker部署

1. **使用Docker Compose一键部署**
   ```bash
   docker-compose up -d
   ```

2. **单独构建镜像**
   ```bash
   mvn clean package -DskipTests
   docker build -t busuanzi:latest .
   docker run -d -p 10010:10010 --name busuanzi busuanzi:latest
   ```

## API接口文档

### 1. 获取统计数据（带增长）
- **URL**: `GET /busuanzi/api`
- **Headers**: 
  - `X-Referer`: 当前页面URL
  - `Authorization`: 用户标识（可选，首次访问会返回）
- **返回**: JSON格式的统计数据

### 2. 获取统计数据（只读）
- **URL**: `GET /busuanzi/api/getStatistics`
- **Headers**: 
  - `X-Referer`: 当前页面URL
- **返回**: JSON格式的统计数据

### 3. JSONP兼容接口
- **URL**: `GET /busuanzi/api/jsonp`
- **参数**: 
  - `jsonpCallback`: JSONP回调函数名
- **Headers**: 
  - `Referer`: 当前页面URL
- **返回**: JSONP格式的统计数据

### 4. 站点地图初始化
- **URL**: `POST /busuanzi/api/initBySitemap`
- **Headers**: 
  - 需要管理员权限（通过PreAuthorize注解验证）
- **Body**: multipart/form-data格式的sitemap.xml文件

## 前端集成

### 方式1：使用提供的JS脚本

在HTML页面中添加以下代码：

```html
<!-- 在需要显示统计数据的元素中添加id -->
<span id="busuanzi_container_site_pv">本站总访问量 <span id="busuanzi_value_site_pv"></span> 次</span>
<span id="busuanzi_container_page_pv">本文总阅读量 <span id="busuanzi_value_page_pv"></span> 次</span>

<!-- 引入统计脚本 -->
<script src="http://your-domain:10010/busuanzi.js"></script>
```

### 方式2：手动调用API

```javascript
fetch('http://your-domain:10010/busuanzi/api', {
    method: 'GET',
    headers: {
        'X-Referer': window.location.href
    }
})
.then(response => response.json())
.then(data => {
    console.log('统计数据:', data);
    // 处理统计数据
});
```

## 配置说明

### Redis配置

#### 开发环境 (application-dev.yml)
```yaml
spring:
  data:
    redis:
      database: 0
      host: 127.0.0.1
      port: 6379
      timeout: 5000
      lettuce:
        pool:
          max-active: 8
          max-wait: 3000
          max-idle: 10
          min-idle: 2
```

#### 生产环境 (application-prod.yml)
需要根据实际Redis配置进行修改。

### 服务器配置

#### application.yml
```yaml
server:
  port: 10010
spring:
  application:
    name: BUSUANZI
  jackson:
    date-format: yyyy-MM-dd HH:mm:ss
    time-zone: GMT+8
```

## 数据存储结构

### Redis键命名规范
- **网站PV**: `pv:{site_url}`
- **页面PV**: `pv:page:{page_url}`
- **网站UV**: `uv:{site_url}`
- **页面UV**: `uv:page:{page_url}`
- **UV去重集合**: `uvtime:{date}:{site_url}` 和 `uvtime:page:{date}:{page_url}`

### 数据过期策略
- UV去重集合：7天过期
- 统计数据：永久保存（除非手动清理）

## 开发规范

### 代码风格
- 使用Lombok简化POJO
- 使用Slf4j进行日志记录
- 异常统一处理
- RESTful API设计

### 安全考虑
- 使用自定义注解进行权限控制
- Referer验证防止CSRF
- 输入参数校验
- 异常信息脱敏

## 部署建议

### 生产环境配置
1. **Redis配置**
   - 启用Redis密码认证
   - 配置Redis持久化
   - 设置合理的内存限制

2. **应用配置**
   - 使用生产环境配置文件
   - 配置JVM参数优化
   - 设置合理的连接池参数

3. **反向代理**
   - 使用Nginx进行反向代理
   - 配置HTTPS
   - 设置合理的缓存策略

### 监控建议
- 监控Redis内存使用情况
- 监控API响应时间
- 设置异常告警
- 定期备份Redis数据

## 故障排查

### 常见问题
1. **Redis连接失败**
   - 检查Redis服务状态
   - 验证网络连通性
   - 检查配置文件

2. **统计数据不增长**
   - 检查Referer是否正确
   - 验证Redis写入权限
   - 查看应用日志

3. **跨域问题**
   - 检查CORS配置
   - 验证请求头设置

### 日志查看
```bash
# 查看应用日志
tail -f logs/busuanzi.log

# 查看Redis日志
docker logs busuanzi-redis
```

## 扩展功能

### 自定义统计维度
可以通过修改`ApiService.java`来添加新的统计维度，如：
- 按时间段统计
- 按用户类型统计
- 按页面分类统计

### 数据导出
可以添加API接口导出统计数据：
- CSV格式导出
- JSON格式导出
- 按时间段筛选

### 管理后台
可以开发管理后台功能：
- 网站管理
- 数据查看
- 配置管理
- 数据清理