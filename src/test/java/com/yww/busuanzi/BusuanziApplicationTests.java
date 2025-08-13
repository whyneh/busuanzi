package com.yww.busuanzi;

import com.yww.busuanzi.redis.RedisCache;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class BusuanziApplicationTests {

    @Autowired
    RedisCache redisCache;

    @Test
    void contextLoads() {

    }

}
