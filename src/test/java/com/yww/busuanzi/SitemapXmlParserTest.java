package com.yww.busuanzi;

import com.yww.busuanzi.redis.RedisCache;
import com.yww.busuanzi.util.SitemapUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class SitemapXmlParserTest {

    @Autowired
    RedisCache redisCache;

    @Test
    void initRedisBySitemapXml() throws Exception {
        String sitemapXml = "src/main/resources/sitemap.xml";
        System.out.println(SitemapUtil.parseSitemapXml(sitemapXml));


    }


}