package com.itheima.reggie.common;

/**
 * TODO
 *基于ThreadLoal封装工具类，保存用户和获取用户当前登录用户ID
 * @author feiyun
 * @date 2023/3/14 12:13
 * @explain
 */
public class BaseContext {
    private static ThreadLocal<Long> threadLocal =new ThreadLocal<>();

    /**
     * 设置值
     * @param id
     */
    public static void setCurrentId(Long id){
        threadLocal.set(id);
    }

    /**
     * 获取值
     * @return
     */
    public static Long getCurrentId(){
        return  threadLocal.get();
    }
}
