package com.yww.busuanzi.api;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.yww.busuanzi.redis.RedisCache;
import com.yww.busuanzi.util.RefererUtil;
import com.yww.busuanzi.util.SitemapUtil;
import lombok.Cleanup;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

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

    public JSONObject api(String siteUrl, String sitePathUrl, String authorization) {
        String today = DateUtil.today();
        log.info("time: 【{}】, referer: 【{}】, authorization: 【{}】", today, sitePathUrl, authorization);

        // 网站PV自增（保持使用String类型）
        Long sitePv = redisCache.incrementCounter(SITE_PV + siteUrl);

        // 网站页面PV自增（使用ZSet类型）
        Long pagePv = redisCache.incrementCounter(PAGE_PV + sitePathUrl);

        // 网站UV
        String siteUvSetKey = "uvtime:" + today + ":" + siteUrl;
        Long siteUv;
        if (!redisCache.isSetMember(siteUvSetKey, authorization)) {
            redisCache.addSet(siteUvSetKey, authorization, 7);
            siteUv = redisCache.incrementCounter(SITE_UV + siteUrl);
        } else {
            siteUv = redisCache.getStringValue(SITE_UV + siteUrl);
        }

        //  页面UV
        String pageUvSetKey = "uvtime:page:" + today + ":" + sitePathUrl;
        Long pageUv;
        if (!redisCache.isSetMember(pageUvSetKey, authorization)) {
            redisCache.addSet(pageUvSetKey, authorization, 7);
            pageUv = redisCache.incrementCounter(PAGE_UV + sitePathUrl);
        } else {
            pageUv = redisCache.getStringValue(PAGE_UV + sitePathUrl);
        }

        // 构建结果
        JSONObject res = new JSONObject();
        res.put("site_pv", sitePv);
        res.put("page_pv", pagePv);
        res.put("site_uv", siteUv);
        res.put("page_uv", pageUv);
        return res;
    }

    public JSONObject getStatistics(String siteUrl, String sitePathUrl) {
        JSONObject res = new JSONObject();
        res.put("site_pv", redisCache.getStringValue(SITE_PV + siteUrl));
        res.put("page_pv", redisCache.getStringValue(PAGE_PV + sitePathUrl));
        res.put("site_uv", redisCache.getStringValue(SITE_UV + siteUrl));
        res.put("page_uv", redisCache.getStringValue(PAGE_UV + sitePathUrl));
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

    public void initBySitemap(MultipartFile file) {
        String filePath = System.getProperty("user.dir") + "/temp/" + UUID.fastUUID() + ".xml";
        try {
            FileUtil.writeBytes(file.getBytes(), filePath);
            List<String> links = SitemapUtil.parseSitemapXml(filePath);

            links.forEach(link -> {
                String api = "https://busuanzi.ibruce.info/busuanzi";
                @Cleanup
                HttpResponse response = HttpUtil.createGet(api)
                        .header("Referer", link)
                        .form("jsonpCallback", "BusuanziCallback_921338913213")
                        .execute();
                String body = response.body();
                // 截取script脚本中的数据
                JSONObject busuanzi = JSON.parseObject(body.substring(body.indexOf('{', 4), body.indexOf('}') + 1));

                // 获取网站地址和网站资源地址
                String siteUrl = RefererUtil.getSiteUrl(link, false);
                String sitePathUrl = RefererUtil.getSiteUrl(link, true);
                if (StrUtil.isBlank(siteUrl) || StrUtil.isBlank(sitePathUrl)) {
                    return;
                }
                redisCache.add(SITE_PV + siteUrl, busuanzi.getString("site_pv"));
                // 对于页面PV，我们使用ZSet存储
                redisCache.add(PAGE_PV + sitePathUrl, busuanzi.getString("page_pv"));
                redisCache.add(SITE_UV + siteUrl, busuanzi.getString("site_uv"));
                // 避免出现限流等情况，缓一缓
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                log.info("成功初始化url【{}】的数据", link);
            });
        } catch (Exception e) {
            log.error("初始化失败：", e);
        } finally {
            FileUtil.del(filePath);
        }
    }

}
