package com.itheima.reggie.filter;

import com.alibaba.fastjson.JSON;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * TODO
 *
 * @author feiyun
 * @date 2023/3/10 12:39
 * @explain 检查用户是否完成登录
 */
@Slf4j
@WebFilter(filterName = "loginCheckFilter",urlPatterns = "/*")
public class LoginCheckFilter implements Filter {

    //路径匹配器，支持通配符
    public static final AntPathMatcher PATH_MATCHER =new AntPathMatcher();
    @Override
    public void doFilter(ServletRequest ServletRequest, ServletResponse ServletResponse, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request=(HttpServletRequest)ServletRequest;
        HttpServletResponse response =(HttpServletResponse) ServletResponse;
        //{}是占位符
        log.info("拦截到请求：{}",request.getRequestURL());



        //1.获取本次请求的uri
        String requestURI = request.getRequestURI();
        //定义不需要处理的资源
        String[] uri=new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**", //静态资源
                "/front/**",
                "/common/**"
        };

        //2.判断本次请求是否需要处理
        boolean check = check(uri, requestURI);
        //3.如果不处理，则直接放行
        if(check){
            chain.doFilter(request,response);
            log.info("本次请求不需要处理：{}"+requestURI);
            return;
        }


        //4.判断登录状态，如果是已登录，则直接放行
        if(request.getSession().getAttribute("employee")!=null){
            log.info("用户已登陆：{} id为："+request.getSession().getAttribute("employee"));
/*            //记录当前线程id
            long id = Thread.currentThread().getId();
            log.info("当前线程Id为：{}",id);*/
            Long empID = (Long) request.getSession().getAttribute("employee");
            BaseContext.setCurrentId(empID);
            chain.doFilter(request,response);
            return;
        }                                                                               
        log.info("用户未登录：{}");

        //5.如果未登录则返回未登录的结果,通过输出流的方式向客户端响应数据
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        return;
    }

    /**
     * 路径匹配，检查本次请求是否需要放行
     * @param urls
     * @param requestURI
     * @return
     */
    public boolean check(String[] urls,String requestURI){
        for (String url : urls) {
            boolean match = PATH_MATCHER.match(url, requestURI);
            if(match){
                return true;
            }
        }
        return false;
    }

}
