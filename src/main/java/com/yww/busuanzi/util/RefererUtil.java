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
        System.out.println(getSiteUrl("https://www.tongyi.com/index.html/?sessionId=46bd6372c21c4414a9e5a12925f74365", false));
        System.out.println(getSiteUrl("https://www.tongyi.com/about/?sessionId=46bd6372c21c4414a9e5a12925f74365", true));
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
            // 有端口的情况
            if (uri.getPort() != -1) {
                url.append(":").append(uri.getPort());
            }
            if (hasPath) {
                url.append(uri.getPath());
            } else {
                url.append("/");
            }
            return url.toString();
        } catch (Exception e) {
            log.error("获取站点地址出错，【{}】", location, e);
            return null;
        }
    }

}
