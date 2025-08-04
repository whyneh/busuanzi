package com.yww.busuanzi;

import cn.hutool.core.date.DateUtil;
import com.yww.busuanzi.util.URLUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class BusuanziApplicationTests {

    @Test
    void contextLoads() {
    }

    public static void main(String[] args) {
        String url = "https://localhost:8080/busuanzi/";
        System.out.println(URLUtil.getHost(url));
        System.out.println(URLUtil.getPath(url));

        System.out.println(DateUtil.today());
    }
}
