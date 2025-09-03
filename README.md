# 🚀 Busuanzi 自搭建网站统计服务

[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.0.2-green.svg)](https://spring.io/projects/spring-boot)
[![Redis](https://img.shields.io/badge/Redis-7.x-red.svg)](https://redis.io/)
[![Docker](https://img.shields.io/badge/Docker-支持-blue.svg)](https://www.docker.com/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

## 📋 项目简介

这是一个开源的网站访问统计服务，用于替代不蒜子官方统计服务。基于 Spring Boot 3.0 和 Redis 构建，提供高性能、可扩展的网站流量统计功能。

### ✨ 核心特性

- **📊 多维度统计**：网站PV、页面PV、网站UV、页面UV
- **🔄 JSONP兼容**：完美兼容原版不蒜子调用方式
- **🐳 容器化部署**：支持Docker和Docker Compose一键部署
- **⚡ 高性能**：基于Redis内存存储

## 🎯 快速开始

### 🔧 开始

#### 环境要求
- Java 17 或更高版本
- Maven 3.6+
- Redis 7.x

#### 步骤

1. **构建jar包**
   使用IDEA打开项目，配置好maven，选择prod环境，进行打包，生成jar包
2. **docker-compose**
   将生成的jar包，Dockerfile，docker-compose.yml，redis.conf上传到服务器，直接运行
   ``` bash
      docker-compose up -d
   ```

## 📖 API文档

### 1. 获取统计数据（带增长）

```http
GET /busuanzi/api
X-Referer: https://example.com/page
Authorization: 可选的用户标识
```

**响应示例：**
```json
{
  "site_pv": 1234,
  "page_pv": 56,
  "site_uv": 789,
  "page_uv": 12
}
```

### 2. 获取统计数据（只读）

```http
GET /busuanzi/api/getStatistics
X-Referer: https://example.com/page
```

### 3. JSONP兼容接口

```http
GET /busuanzi/api/jsonp?jsonpCallback=callback
Referer: https://example.com/page
```

## 🌐 前端集成

### 方式一：使用提供的JS脚本

在HTML页面中添加：

```html
<!-- 统计展示元素 -->
<span id="busuanzi_container_site_pv">
    本站总访问量 <span id="busuanzi_value_site_pv"></span> 次
</span>

<span id="busuanzi_container_page_pv">
    本文总阅读量 <span id="busuanzi_value_page_pv"></span> 次
</span>

<!-- 引入统计脚本 -->
<script src="http://your-domain:10010/busuanzi.js"></script>
```

### 方式二：手动调用API

```javascript
fetch('http://your-domain:10010/busuanzi/api', {
    method: 'GET',
    headers: {
        'X-Referer': window.location.href
    }
})
.then(response => response.json())
.then(data => {
    console.log('访问统计:', data);
    // 更新页面显示
    document.getElementById('pv').textContent = data.site_pv;
});
```

## 📊 数据存储

### Redis键结构
- `pv:{site_url}` - 网站总访问量
- `pv:page:{page_url}` - 页面访问量
- `uv:{site_url}` - 网站独立访客
- `uv:page:{page_url}` - 页面独立访客

### 数据过期策略
- UV去重数据：7天过期
- 统计数据：永久保存

## 🔧 开发指南

### 项目结构
```
src/main/java/com/yww/busuanzi/
├── BusuanziApplication.java    # 启动类
├── api/
│   ├── ApiController.java     # API接口
│   └── ApiService.java        # 业务逻辑
├── redis/                     # Redis配置
├── annotation/                # 权限注解
├── auth/                      # 认证授权
├── exception/                 # 异常处理
└── util/                      # 工具类
```

## 🐛 常见问题

### Q: 统计数据不更新？
**A:** 检查以下几点：
- 确认Redis连接正常
- 检查Referer请求头是否正确
- 查看应用日志是否有错误

### Q: 跨域问题如何处理？
**A:** 项目已配置CORS支持，确保请求头包含正确的Referer。

### Q: 如何重置统计数据？
**A:** 直接删除Redis中对应的key即可

## 🙏 致谢

- [Spring Boot](https://spring.io/projects/spring-boot) - 优秀的Java框架
- [Redis](https://redis.io/) - 高性能内存数据库
- [不蒜子](https://busuanzi.ibruce.info/) - 原版统计服务
