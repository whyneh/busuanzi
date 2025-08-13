# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a self-hosted busuanzi statistics service built with Spring Boot 3.x and Java 17. It provides website traffic statistics tracking including PV (Page Views) and UV (Unique Visitors) for both site-wide and per-page metrics.

The service is designed to be compatible with the original busuanzi service, providing similar APIs and functionality while offering self-hosted capabilities.

## Architecture

### Core Components
1. **ApiController** (`src/main/java/com/yww/busuanzi/api/ApiController.java`) - Main REST endpoints for tracking and retrieving statistics
2. **ApiService** (`src/main/java/com/yww/busuanzi/api/ApiService.java`) - Business logic for statistics tracking and retrieval
3. **Redis Integration** - Uses Redis for fast counter operations and set-based unique visitor tracking
4. **Frontend Integration** - JavaScript clients (`busuanzi.js`, `busuanzi-jsonp.js`) for browser-based tracking

### Key Features
- Tracks site PV/UV and page PV/UV metrics
- Uses Redis sets with expiration for efficient unique visitor counting
- Generates unique visitor IDs using Yitter ID generator (Snowflake algorithm)
- CORS enabled for cross-origin requests
- REST API with JSON responses
- JSONP support for compatibility with original busuanzi
- Sitemap-based initialization for migrating from original busuanzi

## Development Commands

### Build & Run
```bash
# Build the project
./mvnw clean package

# Run the application
./mvnw spring-boot:run

# Run in background
nohup java -jar target/busuanzi-1.0.jar > busuanzi.log 2>&1 &

# Run with Docker
docker-compose up -d
```

### Testing
```bash
# Run tests
./mvnw test

# Run specific test class
./mvnw test -Dtest=BusuanziApplicationTests
```

### Redis Management
Redis is configured on database 7 at busuanzi-redis:6379. Key patterns:
- `pv:{host}` - Site page views counter (String type)
- `pv:page:zset:{host}` - Page-specific views counter (ZSet type for ranking)
- `uv:{host}` - Site unique visitors counter
- `uv:page:{url}` - Page-specific unique visitors counter
- `uvtime:{date}:{host}` - Daily site visitor sets
- `uvtime:page:{date}:{url}` - Daily page visitor sets

### Configuration
- Application port: 10010 (configured in `application.yml`)
- Redis database: 7
- API Key: Configured in `application.yml` (used for administrative endpoints)

## Important Implementation Details

### Statistics Tracking Logic
1. Each visitor gets a unique ID (either from Authorization header or generated)
2. For each request, PV counters are incremented
3. UV tracking uses Redis sets with 7-day expiration to track unique visitors per day
4. Statistics are returned as JSON with Authorization header containing visitor ID

### Key Classes
- `RedisCache` - Core Redis operations wrapper
- `RefererUtil` - URL parsing utilities
- `SitemapUtil` - Sitemap parsing utilities
- `PreAuthorize` - Custom annotation for API key protection

### API Endpoints
1. `GET /busuanzi/api` - Main tracking endpoint that increments counters
2. `GET /busuanzi/api/getStatistics` - Get statistics without incrementing counters
3. `GET /busuanzi/api/jsonp` - JSONP endpoint for compatibility with original busuanzi
4. `POST /busuanzi/api/initBySitemap` - Initialize data from sitemap (requires API key)

## Common Development Tasks

### Adding New Metrics
1. Modify ApiController and ApiService to track additional counters
2. Update RedisCache with new operations if needed
3. Update frontend JavaScript files to display new metrics
4. Ensure proper Redis key naming conventions

### Modifying Redis Configuration
Update `RedisConfig` class for serialization changes or connection settings.

### Adding New API Endpoints
1. Add new methods to ApiController with appropriate mappings
2. Implement business logic in ApiService
3. Add PreAuthorize annotation if administrative access is required
4. Update API documentation in comments

### Working with Docker
The project includes Docker and Docker Compose configurations:
- `Dockerfile` defines the application container
- `docker-compose.yml` defines the multi-container setup (app + Redis)
- Redis data is persisted to `/data/docker/busuanzi/data` on the host