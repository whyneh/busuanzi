package com.yww.busuanzi.api;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSONObject;
import com.yww.busuanzi.redis.RedisCache;
import com.yww.busuanzi.util.URLUtil;
import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * <p>
 *      服务类
 * </p>
 *
 * @author yww
 * @since 2025/8/6
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ApiService {

    public static final String SITE_PV = "pv:";
    public static final String PAGE_PV = "pv:page:";
    public static final String SITE_UV = "uv:";
    public static final String PAGE_UV = "uv:page:";

    public final RedisCache redisCache;

    public JSONObject api(String referer, String authorization) {
        String today = DateUtil.today();
        log.info("time: 【{}】, referer: 【{}】, authorization: 【{}】", today, referer, authorization);

        // 获取网站host
        String host = URLUtil.getHost(referer);

        // 网站PV自增
        Long sitePv = redisCache.incrementCounter(SITE_PV + host);

        // 网站页面PV自增
        Long pagePv = redisCache.incrementCounter(PAGE_PV + referer);

        // 网站UV
        String siteUvSetKey = "uvtime:" + today + ":" + host;
        Long siteUv;
        if (!redisCache.isSetMember(siteUvSetKey, authorization)) {
            redisCache.addSet(siteUvSetKey, authorization, 7);
            siteUv = redisCache.incrementCounter(SITE_UV + host);
        } else {
            siteUv = redisCache.getStringValue(SITE_UV + host);
        }

        //  页面UV
        String pageUvSetKey = "uvtime:page:" + today + ":" + referer;
        Long pageUv;
        if (!redisCache.isSetMember(pageUvSetKey, authorization)) {
            redisCache.addSet(pageUvSetKey, authorization, 7);
            pageUv = redisCache.incrementCounter(PAGE_UV + referer);
        } else {
            pageUv = redisCache.getStringValue(PAGE_UV + referer);
        }

        // 构建结果
        JSONObject res = new JSONObject();
        res.put("site_pv", sitePv);
        res.put("page_pv", pagePv);
        res.put("site_uv", siteUv);
        res.put("page_uv", pageUv);
        return res;
    }

    public JSONObject getStatistics(String referer) {
        // 获取网站host
        String host = URLUtil.getHost(referer);
        JSONObject res = new JSONObject();
        res.put("site_pv", redisCache.getStringValue(SITE_PV + host));
        res.put("page_pv", redisCache.getStringValue(PAGE_PV + referer));
        res.put("site_uv", redisCache.getStringValue(SITE_UV + host));
        res.put("page_uv", redisCache.getStringValue(PAGE_UV + referer));
        return res;
    }

    /**
     * 将JSONObject的对象转换为JSONP字符串
     */
    public String toJSONP(JSONObject jsonObject, String jsonpCallback) {
        String jsonp =
                """
                    try{
                        {}({
                            "site_uv": {},
                            "page_pv": {},
                            "version": 2.4,
                            "site_pv": {}
                        });
                    }catch(e){}
                """;
        return StrUtil.format(jsonp,
                jsonpCallback,
                jsonObject.getString("site_uv"),
                jsonObject.getString("page_pv"),
                jsonObject.getString("site_pv")
        );
    }

}
