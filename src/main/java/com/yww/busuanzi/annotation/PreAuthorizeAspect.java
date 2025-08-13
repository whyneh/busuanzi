package com.yww.busuanzi.annotation;

import cn.hutool.core.util.StrUtil;
import com.yww.busuanzi.auth.ApiKeyService;
import com.yww.busuanzi.exception.BusuanziException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;


/**
 * <p>
 *      校验权限注解切面
 * </p>
 *
 * @author yww
 * @since 2025/8/12
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class PreAuthorizeAspect {

    private final ApiKeyService apiKeyService;

    /**
     * 配置切入点
     */
    @Pointcut("@annotation(com.yww.busuanzi.annotation.PreAuthorize)")
    public void pointcut() {
        // 该方法无方法体,主要为了让同类中其他方法使用此切入点
    }

    /**
     * 环绕通知处理权限校验
     */
    @Around("pointcut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        // 获取当前请求
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            throw new BusuanziException(500, "错误的请求");
        }

        HttpServletRequest request = attributes.getRequest();
        String apiKey = request.getHeader("X-ApiKey");
        if (StrUtil.isBlank(apiKey)) {
            throw new BusuanziException(401, "权限不足");
        }

        // 校验apiKey
        if (!apiKeyService.validateApiKey(apiKey)) {
            throw new BusuanziException(401, "密钥校验失败");
        }

        return point.proceed();
    }

}
