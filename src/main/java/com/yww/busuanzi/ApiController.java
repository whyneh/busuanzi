package com.yww.busuanzi;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSONObject;
import com.github.yitter.idgen.YitIdHelper;
import com.yww.busuanzi.redis.RedisCache;
import com.yww.busuanzi.util.URLUtil;
import jakarta.servlet.http.HttpServletRequest;
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
    public ResponseEntity<?> api(HttpServletRequest request,
                                 @RequestHeader("Referer") String referer,
                                 @RequestHeader("Authorization") String authorization) {
        // 如果没有网站信息
        if (StrUtil.isBlank(referer)) {
            return ResponseEntity.badRequest().body("缺少Refer请求头信息");
        }
        // 如果没有权限ID，则重新生成
        if (StrUtil.isBlank(authorization)) {
            authorization = Convert.toStr(YitIdHelper.nextId());
        }
        String today = DateUtil.today();
        log.info("time: 【{}】, referer: 【{}】, authorization: 【{}】", today, referer, authorization);

        // 获取网站host
        String host = URLUtil.getHost(referer);

        // 网站PV自增
        Long sitePv = redisCache.incrementCounter("pv:" + host);

        // 网站页面PV自增
        Long pagePv = redisCache.incrementCounter("pv:page:" + referer);

        // 网站UV
        String siteUvSetKey = "uvtime:" + today + ":" + host;
        Long siteUv;
        if (!redisCache.isSetMember(siteUvSetKey, authorization)) {
            redisCache.addSet(siteUvSetKey, authorization, 7);
            siteUv = redisCache.incrementCounter("uv:" + host);
        } else {
            siteUv = redisCache.getStringValue("uv:" + host);
        }

        //  页面UV
        String pageUvSetKey = "uvtime:page:" + today + ":" + referer;
        Long pageUv;
        if (!redisCache.isSetMember(pageUvSetKey, authorization)) {
            redisCache.addSet(pageUvSetKey, authorization, 7);
            pageUv = redisCache.incrementCounter("uv:page:" + referer);
        } else {
            pageUv = redisCache.getStringValue("uv:page:" + referer);
        }

        // 构建结果
        JSONObject res = new JSONObject();
        res.put("site_pv", sitePv);
        res.put("page_pv", pagePv);
        res.put("site_uv", siteUv);
        res.put("page_uv", pageUv);

        return ResponseEntity.ok().header("Authorization", authorization).body(res);
    }

}
