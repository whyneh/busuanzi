package com.yww.busuanzi.exception;

import lombok.Getter;

/**
 * <p>
 *      自定义异常类
 * </p>
 *
 * @author yww
 * @since 2025/8/4
 */
@Getter
public class BusuanziException extends RuntimeException {

    /**
     * 错误码
     */
    private final Integer code;

    /**
     * 错误信息
     */
    private final String message;

    public BusuanziException(String message) {
        this.code = 500;
        this.message = message;
    }

    public BusuanziException(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

}
