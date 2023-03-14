package com.itheima.reggie.common;

/**
 * TODO
 *自定义业务异常类
 * @author feiyun
 * @date 2023/3/14 14:05
 * @explain
 */
public class CustomException extends RuntimeException{
    public CustomException(String message) {
        super(message);
    }
}
