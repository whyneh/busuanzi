package com.yww.busuanzi.auth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

/**
 * <p>
 *     应用启动监听器
 * </p>
 *
 * @author yww
 * @since 2025/8/13
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ApplicationStartupListener implements ApplicationListener<ContextRefreshedEvent> {

    private final ApiKeyService apiKeyService;

    @Override
    public void onApplicationEvent(@NonNull ContextRefreshedEvent event) {
        apiKeyService.generateAndStoreApiKey();
    }
}