package com.yww.busuanzi.exception;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSONObject;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * <p>
 *      全局异常处理
 * </p>
 *
 * @author yww
 * @since 2025/8/4
 */
@Slf4j
@RestControllerAdvice
public class ControllerAdviceHandler {

    /**
     * 处理自定义的服务异常信息
     * 统一处理GlobalException异常，异常处理顺序是从小异常到大异常。
     *
     * @param e 服务异常
     * @return 异常信息
     */
    @ExceptionHandler(value = BusuanziException.class)
    public JSONObject busuanziExceptionHandler(BusuanziException e, HttpServletRequest request) {
        log.error(">> busuanzi exception: {}, {}, {}", request.getRequestURI(), e.getCode(), e.getMessage());
        String errMessage = e.getMessage();
        // 防止空的错误信息
        if (StrUtil.isBlank(errMessage)) {
            errMessage = "服务出现未知错误！";
        }
        JSONObject res = new JSONObject();
        res.put("code", e.getCode());
        res.put("message", errMessage);
        return res;
    }

    /**
     * 异常信息
     *
     * @param e 服务异常
     * @return 异常信息
     */
    @ExceptionHandler(value = Exception.class)
    public JSONObject defaultErrorHandler(Exception e, HttpServletRequest request) {
        log.error(">> 服务器内部错误 {}", request.getRequestURI(), e);
        JSONObject res = new JSONObject();
        res.put("code", 500);
        res.put("message", "服务器内部错误");
        return res;
    }


}
