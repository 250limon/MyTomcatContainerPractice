package servlet;

import http.HttpRequest;
import http.HttpResponse;

/**
 * Servlet接口，所有Servlet必须实现此接口
 */
public interface Servlet {
    /**
     * 初始化Servlet
     * @param config Servlet配置
     */
    void init(ServletConfig config);
    
    /**
     * 获取Servlet配置
     * @return Servlet配置
     */
    ServletConfig getServletConfig();
    
    /**
     * 处理HTTP请求
     * @param request HTTP请求
     * @param response HTTP响应
     */
    void service(HttpRequest request, HttpResponse response);
    
    /**
     * 获取Servlet信息
     * @return Servlet信息
     */
    String getServletInfo();
    
    /**
     * 销毁Servlet
     */
    void destroy();
}