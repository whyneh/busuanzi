package com.yww.busuanzi.redis;

import cn.hutool.core.util.StrUtil;
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
     * 对指定 key 的值进行自增操作
     * 如果 key 不存在，Redis 会自动创建并初始化为 0，然后自增为 1
     *
     * @param key   键
     * @return      自增之后的值
     */
    public Long incrementCounter(String key) {
        return stringRedisTemplate.opsForValue().increment(key);
    }

    /**
     * 根据Key设置值
     *
     * @param   key     键
     * @param   value   值
     */
    public void add(String key, String value) {
        stringRedisTemplate.opsForValue().set(key, value);
    }

    /**
     * 根据Key获取值
     *
     * @param key   键
     * @return      存储的值
     */
    public Long getStringValue(String key) {
        String value = stringRedisTemplate.opsForValue().get(key);
        return StrUtil.isNotBlank(value) ? Long.parseLong(value) : 0L;
    }
    
    /**
     * 根据Key获取字符串值
     *
     * @param key   键
     * @return      存储的字符串值
     */
    public String getStringValueAsString(String key) {
        return stringRedisTemplate.opsForValue().get(key);
    }

    /**
     * 添加一个值进Set集合当中
     *
     * @param key       集合键
     * @param value     添加的值
     * @param days      过期时间（单位天）
     */
    public void addSet(String key, String value, long days) {
        // 如果不存在这个集合，初始化过期时间
        if (!stringRedisTemplate.hasKey(key)) {
            stringRedisTemplate.expire(key, days, TimeUnit.DAYS);
        }
        stringRedisTemplate.opsForSet().add(key, value);
    }

    /**
     * 判断一个Set集合当中是否存在该值
     *
     * @param key       集合键
     * @param value     判断的值
     * @return          存在返回True
     */
    public Boolean isSetMember(String key, String value) {
        return stringRedisTemplate.opsForSet().isMember(key, value);
    }

}
