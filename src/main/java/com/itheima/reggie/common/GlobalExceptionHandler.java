package com.itheima.reggie.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * TODO
 *全局异常处理类，使用aop全局拦截
 * @author feiyun
 * @date 2023/3/13 14:52
 * @explain
 */
@Slf4j
@ResponseBody
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 异常处理方法
     * @param ex
     * @return
     */
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public R<String> exceptionHander(SQLIntegrityConstraintViolationException ex){

        log.error(ex.getMessage());
        if (ex.getMessage().contains("Duplicate entry")){
            String[] split = ex.getMessage().split(" ");
            String msg = split[2] + "已存在！";
            return R.error(msg);
        }
        return R.error("未知错误");
    }

    /**
     * 自定义异常处理方法
     * @param ex
     * @return
     */
    @ExceptionHandler(CustomException.class)
    public R<String> exceptionHander(CustomException ex){
        log.error(ex.getMessage());
        return R.error(ex.getMessage());
    }

}
