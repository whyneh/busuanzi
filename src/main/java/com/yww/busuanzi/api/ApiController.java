package com.yww.busuanzi.api;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSONObject;
import com.github.yitter.idgen.YitIdHelper;
import com.yww.busuanzi.redis.RedisCache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
@RequestMapping("/busuanzi/api")
@CrossOrigin(exposedHeaders = {"Authorization, X-Referer"})
public class ApiController {

    @Value("${busuanzi.api-key}")
    private String API_KEY;

    private final ApiService service;

    @GetMapping("hello")
    public String hello() {
        return "hello busuanzi";
    }

    /**
     *  访问页面，获取页面数据，并且进行数据增长
     */
    @GetMapping
    public ResponseEntity<?> api(@RequestHeader(value = "X-Referer", required = false) String referer,
                                 @RequestHeader(value = "Authorization", required = false) String authorization) {
        // 如果没有网站信息
        if (StrUtil.isBlank(referer)) {
            return ResponseEntity.badRequest().body("缺少Refer请求头信息");
        }
        // 如果没有权限ID，则重新生成
        if (StrUtil.isBlank(authorization)) {
            authorization = Convert.toStr(YitIdHelper.nextId());
        }
        return ResponseEntity.ok()
                .header("Authorization", authorization)
                .header("X-Referer", referer)
                .body(service.api(referer, authorization));
    }

    /**
     * 获取页面数据，不进行数据更改
     */
    @GetMapping("/getStatistics")
    public ResponseEntity<?> getStatistics(@RequestHeader(value = "X-Referer", required = false) String referer) {
        // 如果没有网站信息
        if (StrUtil.isBlank(referer)) {
            return ResponseEntity.badRequest().body("缺少Refer请求头信息");
        }
        return ResponseEntity.ok()
                .header("X-Referer", referer)
                .body(service.getStatistics(referer));
    }

    /**
     *  适配原版busuanzi的原版请求
     */
    @GetMapping("/jsonp")
    public ResponseEntity<?> busuanzi(@CookieValue(name = "busuanziId", required = false) String busuanziId,
                                      @RequestHeader(value = "Referer", required = false) String referer,
                                      @RequestParam(value = "jsonpCallback", required = false) String jsonpCallback) {
        // 如果没有网站信息
        if (StrUtil.isBlank(referer)) {
            return ResponseEntity.badRequest().body("缺少Refer请求头信息");
        }
        // 原版使用cookie来存储用户信息
        String authorization = busuanziId;
        if (StrUtil.isBlank(busuanziId)) {
            authorization = Convert.toStr(YitIdHelper.nextId());
        }

        JSONObject data = service.api(referer, authorization);

        // 设置cookie
        String cookie = "busuanziId={}; Path=/; httponly; secure; SameSite=None; Secure";

        // 获取cookie中的数据，用来代替
        return ResponseEntity.ok()
                .header("Set-Cookie", StrUtil.format(cookie, authorization))
                .body(service.toJSONP(data, jsonpCallback));
    }

    /**
     *  适配原版busuanzi的原版请求
     */
    @PostMapping("/initBySitemap")
    public ResponseEntity<?> initBySitemap(@RequestHeader(value = "X-ApiKey", required = false) String apiKey,
                                      @RequestPart MultipartFile file) {
        if (!API_KEY.equals(apiKey)) {
            return ResponseEntity.badRequest().body("密钥不能为空");
        }
        if (file == null || file.isEmpty()) {
            return ResponseEntity.badRequest().body("文件不能为空");
        }
        String extName = FileUtil.extName(file.getOriginalFilename());
        if (!"xml".equals(extName)) {
            return ResponseEntity.badRequest().body("文件格式出错");
        }

        service.initBySitemap(file);
        return ResponseEntity.ok().build();
    }

}
