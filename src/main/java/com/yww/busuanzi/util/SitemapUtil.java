package com.yww.busuanzi.util;

import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import lombok.Cleanup;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;
import java.io.FileInputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 *      sitemap解析工具类
 * </p>
 *
 * @author yww
 * @since 2025/8/5
 */
public class SitemapUtil {

    public static void main(String[] args) {
        String url = "https://yww52.com/";
        String api = "https://busuanzi.ibruce.info/busuanzi";

        @Cleanup
        HttpResponse response = HttpUtil.createGet(api)
                .header("Referer", url)
                .form("jsonpCallback", "BusuanziCallback_921338913213")
                .execute();
        System.out.println(response.body());
    }

    /**
     * 解析sitemap.xml文件获取页面链接
     *
     * @param filePath  文件路径
     * @return          链接数组
     */
    public static List<String> parseSitemapXml(String filePath) throws Exception {
        // 获取资源文件路径
        Path path = Paths.get(filePath);
        // 创建XML输入工厂
        XMLInputFactory factory = XMLInputFactory.newInstance();
        // 创建文件输入流
        FileInputStream fis = new FileInputStream(path.toFile());
        // 创建XML流读取器
        XMLStreamReader reader = factory.createXMLStreamReader(fis);
        // 存储链接的列表
        List<String> links = new ArrayList<>();
        // 解析XML
        while (reader.hasNext()) {
            int event = reader.next();

            // 处理开始标签
            if (event == XMLStreamConstants.START_ELEMENT) {
                String elementName = reader.getLocalName();

                // 如果是loc标签
                if ("loc".equals(elementName)) {
                    // 读取下一个事件（文本内容）
                    if (reader.hasNext()) {
                        event = reader.next();
                        if (event == XMLStreamConstants.CHARACTERS) {
                            String url = reader.getText();
                            links.add(url);
                            System.out.println(url);
                        }
                    }
                }
            }
        }
        // 关闭资源
        reader.close();
        fis.close();
        return links;
    }

}
