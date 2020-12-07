package cn.las.filter;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class HeaderFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse resp, FilterChain chain)
            throws IOException, ServletException {
        HttpServletResponse response = (HttpServletResponse) resp;
        // 解决跨域访问的报错问题
        response.setHeader("Access-Control-Allow-Origin", "*");
        // 设置允许访问的restful方法
        response.setHeader("Access-Control-Allow-Methods", "POST, PUT, GET, OPTIONS, DELETE");
        // 设置前端缓存过期的时间 一个小时
        response.setHeader("Access-Control-Max-Age", "3600");
        // 设置允许访问的请求头的格式
        response.setHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept, client_id, uuid, Authorization");
        // 支持Http1.1
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        // 设置无缓存
        response.setHeader("Pragma", "no-cache"); // 支持HTTP 1.0. response.setHeader("Expires", "0");

        // 进行页面的过滤
        chain.doFilter(request, resp);
    }

    @Override
    public void destroy() {

    }
}
