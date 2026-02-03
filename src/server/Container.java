package server;

import http.HttpRequest;
import http.HttpResponse;
import servlet.Servlet;
import servlet.ServletConfig;

import java.util.Map;

/**
 * 容器接口，管理Servlet和请求映射
 */
public interface Container {
    /**
     * 注册Servlet
     * @param servletName Servlet名称
     * @param servletClass Servlet类名
     * @param initParams 初始化参数
     * @return Servlet配置
     */
    ServletConfig registerServlet(String servletName, String servletClass, Map<String, String> initParams);
    
    /**
     * 映射URL到Servlet
     * @param urlPattern URL模式
     * @param servletName Servlet名称
     */
    void mapServlet(String urlPattern, String servletName);
    
    /**
     * 处理HTTP请求
     * @param request HTTP请求
     * @param response HTTP响应
     */
    void processRequest(HttpRequest request, HttpResponse response);
    
    /**
     * 获取Servlet
     * @param servletName Servlet名称
     * @return Servlet对象
     */
    Servlet getServlet(String servletName);
    
    /**
     * 获取URL对应的Servlet名称
     * @param url URL路径
     * @return Servlet名称
     */
    String getServletNameByUrl(String url);
    
    /**
     * 初始化容器
     */
    void init();
    
    /**
     * 销毁容器
     */
    void destroy();
}