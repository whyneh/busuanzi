package com.yww.busuanzi.util;

import cn.hutool.core.util.URLUtil;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;

/**
 * <p>
 *      请求来源工具类
 * </p>
 *
 * @author yww
 * @since 2025/8/4
 */
@Slf4j
public class RefererUtil {

    public static void main(String[] args) {
        System.out.println(getSiteUrl("https://yww52.com", false));
        System.out.println(getSiteUrl("https://yww52.com", true));
        System.out.println(getSiteUrl("https://comment.yww52.com/path/page.html", false));
        System.out.println(getSiteUrl("https://comment.yww52.com/path/page.html", true));
        System.out.println(getSiteUrl("http://localhost:8080/test/index.html", false));
        System.out.println(getSiteUrl("http://localhost:8080/test/index.html", true));
    }

    /**
     * 获取站点地址
     * 1. 协议
     * 2. 域名
     * 3. 端口
     * @param location  资源地址
     * @param hasPath   是否携带资源路径
     */
    public static String getSiteUrl(String location, boolean hasPath) {
        // 格式化字符串
        try {
            String normalize = URLUtil.normalize(location);
            URI uri = URLUtil.toURI(normalize);
            StringBuilder url = new StringBuilder();
            url.append(uri.getScheme()).append("://").append(uri.getHost());
            // 有端口的情况（非默认端口）
            if (uri.getPort() != -1) {
                url.append(":").append(uri.getPort());
            }
            if (hasPath) {
                // 如果有路径则添加路径，如果没有路径则添加根路径
                String path = uri.getPath();
                if (path != null && !path.isEmpty()) {
                    url.append(path);
                } else {
                    url.append("/");
                }
            } else {
                // 如果不带路径，则只添加根路径
                url.append("/");
            }
            return url.toString();
        } catch (Exception e) {
            log.error("获取站点地址出错，【{}】", location, e);
            return null;
        }
    }

}
