package com.yww.busuanzi.redis;

import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * <p>
 *      Redis的工具类
 * </p>
 *
 * @author yww
 * @since 2025/7/22
 */
@Slf4j
@Component
public class RedisCache {

    private final StringRedisTemplate stringRedisTemplate;
    private final RedisTemplate<String, Object> redisTemplate;

    @Autowired
    public RedisCache(StringRedisTemplate stringRedisTemplate, RedisTemplate<String, Object> redisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.redisTemplate = redisTemplate;
    }

    /**
     * 普通缓存
     *
     * @param key 键
     */
    public void setStr(String key, String value) {
        stringRedisTemplate.opsForValue().set(key, value);
    }

    /**
     * 普通缓存
     *
     * @param key 键
     */
    public void setStr(String key, String value, long time) {
        stringRedisTemplate.opsForValue().set(key, value, time, TimeUnit.MINUTES);
    }

    /**
     * 普通缓存
     *
     * @param key 键
     */
    public void setStr(String key, String value, long time, TimeUnit timeUnit) {
        stringRedisTemplate.opsForValue().set(key, value, time, timeUnit);
    }

    /**
     * 普通缓存获取
     *
     * @param key 键
     * @return 值
     */
    public String getStr(String key) {
        return key == null ? null : stringRedisTemplate.opsForValue().get(key);
    }

    /**
     * 普通缓存获取
     *
     * @param key 键
     * @return 值
     */
    public <T> T getStr(String key, Class<T> clazz) {
        return key == null ? null : JSON.parseObject(stringRedisTemplate.opsForValue().get(key), clazz);
    }

    /**
     * 普通缓存删除
     *
     * @param key 键
     */
    public void deleteStr(String key) {
        stringRedisTemplate.delete(key);
    }

}
