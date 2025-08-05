# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a self-hosted busuanzi statistics service built with Spring Boot 3.x and Java 17. It provides website traffic statistics tracking including PV (Page Views) and UV (Unique Visitors) for both site-wide and per-page metrics.

## Architecture

### Core Components
1. **ApiController** (`src/main/java/com/yww/busuanzi/ApiController.java`) - Main REST endpoints for tracking and retrieving statistics
2. **Redis Integration** - Uses Redis for fast counter operations and set-based unique visitor tracking
3. **H2 Database** - Embedded database for persistence
4. **Frontend Integration** - JavaScript client (`busuanzi.js`) for browser-based tracking

### Key Features
- Tracks site PV/UV and page PV/UV metrics
- Uses Redis sets with expiration for efficient unique visitor counting
- Generates unique visitor IDs using Yitter ID generator (Snowflake algorithm)
- CORS enabled for cross-origin requests
- REST API with JSON responses

## Development Commands

### Build & Run
```bash
# Build the project
./mvnw clean package

# Run the application
./mvnw spring-boot:run

# Run in background
nohup java -jar target/busuanzi-1.0.jar > busuanzi.log 2>&1 &
```

### Testing
```bash
# Run tests
./mvnw test

# Run specific test class
./mvnw test -Dtest=BusuanziApplicationTests
```

### Redis Management
Redis is configured on database 7 at localhost:6379. Key patterns:
- `pv:{host}` - Site page views counter
- `pv:page:{url}` - Page-specific views counter
- `uv:{host}` - Site unique visitors counter
- `uv:page:{url}` - Page-specific unique visitors counter
- `uvtime:{date}:{host}` - Daily site visitor sets
- `uvtime:page:{date}:{url}` - Daily page visitor sets

### Configuration
- Application port: 10010 (configured in `application.yml`)
- Redis database: 7
- H2 database file: `/data/busuanzi`

## Important Implementation Details

### Statistics Tracking Logic
1. Each visitor gets a unique ID stored in localStorage
2. For each request, PV counters are incremented
3. UV tracking uses Redis sets with 7-day expiration to track unique visitors per day
4. Statistics are returned as JSON with Authorization header containing visitor ID

### Key Classes
- `RedisCache` - Core Redis operations wrapper
- `URLUtil` - URL parsing utilities
- `IdGenerator` - ID generation utilities
- `FastJson2JsonRedisSerializer` - Redis serialization configuration

## Common Development Tasks

### Adding New Metrics
1. Modify ApiController to track additional counters
2. Update RedisCache with new operations if needed
3. Update busuanzi.js to display new metrics
4. Ensure proper Redis key naming conventions

### Modifying Redis Configuration
Update `RedisConfig` class for serialization changes or connection settings.