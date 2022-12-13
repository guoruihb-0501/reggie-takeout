package com.future.reggie.filter;

import com.alibaba.fastjson.JSON;
import com.future.reggie.common.BaseContext;
import com.future.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author guorui
 * @create 2022-12-01-9:19
 */
@Slf4j
@WebFilter(filterName = "loginCheckFilter", urlPatterns = "/*")
public class LoginCheckFilter implements Filter {
    //定义路径匹配器
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest)servletRequest;
        HttpServletResponse response = (HttpServletResponse)servletResponse;

        // 1.获取本次请求的url
        String url = request.getRequestURI();

        log.info("拦截到请求" + url);
        // 2.定义不需要拦截的路径
        String[] urls = new String[]{
            "/employee/login",
            "/employee/logout",
            "/backend/**",
            "/front/**",
            "/common/**",
            "/user/sendMsg",
            "/user/login"
        };
        // 3.检查本次访问是否需要被处理
        boolean check = check(urls,url);

        // 4.不需要处理则直接放行
        if (check) {
            log.info("本次请求{}不需要处理",url);
            filterChain.doFilter(request,response);
            return;
        }

        // 5.1.判断后端用户登录状态，已经登录则放行
        if (request.getSession().getAttribute("employee") != null){
            log.info("用户已登录，用户id为{}",request.getSession().getAttribute("employee"));
            //将登录用户存储在共享线程存储空间中
            Long empId = (Long) request.getSession().getAttribute("employee");
            BaseContext.setCurrentId(empId);

            filterChain.doFilter(request,response);
            return;
        }
        // 5.2.判断移动端用户登录状态，已经登录则放行
        if (request.getSession().getAttribute("user") != null){
            log.info("用户已登录，用户id为{}",request.getSession().getAttribute("user"));
            //将登录用户存储在共享线程存储空间中
            Long userId = (Long) request.getSession().getAttribute("user");
            BaseContext.setCurrentId(userId);

            filterChain.doFilter(request,response);
            return;
        }

        // 6.如果没有登录在返回错误结果  见/backend/js/request.js中关于登录判断的内容
        log.info("用户未登录");
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
    }

    public boolean check(String[] urls,String url){
        for (String u:urls){
            boolean match = PATH_MATCHER.match(u,url);
            if (match) {
                return true;
            }
        }
        return  false;
    }
}
