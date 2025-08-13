package com.yww.busuanzi.auth;

import com.yww.busuanzi.redis.RedisCache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * <p>
 *     API密钥管理服务
 * </p>
 *
 * @author yww
 * @since 2025/8/13
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ApiKeyService {

    private final RedisCache redisCache;
    
    private static final String API_KEY_REDIS_KEY = "busuanzi:api_key";

    /**
     * 生成新的API密钥并存储到Redis中
     */
    public void generateAndStoreApiKey() {
        String apiKey = UUID.randomUUID().toString().replace("-", "");
        redisCache.add(API_KEY_REDIS_KEY, apiKey);
        log.info("生成并存储新的API密钥到Redis: {}", apiKey);
    }

    /**
     * 从Redis中获取当前的API密钥
     *
     * @return 当前的API密钥，如果不存在则返回null
     */
    public String getCurrentApiKey() {
        return redisCache.getStringValueAsString(API_KEY_REDIS_KEY);
    }

    /**
     * 验证提供的API密钥是否有效
     *
     * @param apiKey 提供的API密钥
     * @return 如果有效返回true，否则返回false
     */
    public boolean validateApiKey(String apiKey) {
        String currentApiKey = getCurrentApiKey();
        return currentApiKey != null && currentApiKey.equals(apiKey);
    }

}