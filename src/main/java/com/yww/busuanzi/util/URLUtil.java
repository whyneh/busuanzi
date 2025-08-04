package com.yww.busuanzi.util;

import cn.hutool.core.exceptions.UtilException;
import cn.hutool.core.util.StrUtil;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * <p>
 *      URL处理工具
 * </p>
 *
 * @author yww
 * @since 2025/8/4
 */
public class URLUtil {

    /**
     * 获取主机地址
     * @param location  网络地址
     * @return          主机地址
     */
    public static String getHost(String location) {
        return toURL(location).getHost();
    }

    /**
     * 获取资源路径
     * @param location  网络地址
     * @return          资源路径
     */
    public static String getPath(String location) {
        return toURL(location).getPath();
    }

    /**
     * 获取URL对象
     * @param location          网络地址
     * @return                  URL对象
     * @throws UtilException    转换异常
     */
    public static URI toURL(String location) throws UtilException {
        try {
            return new URI(StrUtil.trim(location));
        } catch (URISyntaxException e) {
            throw new UtilException(e);
        }
    }

}
