package com.yww.busuanzi;

import com.alibaba.fastjson2.JSONObject;
import com.yww.busuanzi.redis.RedisCache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *      主要接口控制器
 * </p>
 *
 * @author yww
 * @since 2025/7/22
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ApiController {

    public final RedisCache redisCache;

    @GetMapping
    public ResponseEntity<JSONObject> api(@RequestHeader("Referer") String referer,
                                          @RequestHeader("Authorization") String authorization) {
        log.info("referer={}", referer);
        log.info("authorization={}", authorization);

        return null;
    }

}
