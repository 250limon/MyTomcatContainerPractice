package servlet;

import java.util.Enumeration;

/**
 * Servlet配置接口，提供Servlet的配置信息
 */
public interface ServletConfig {
    /**
     * 获取Servlet名称
     * @return Servlet名称
     */
    String getServletName();
    
    /**
     * 获取Servlet上下文
     * @return Servlet上下文
     */
    ServletContext getServletContext();
    
    /**
     * 获取初始化参数
     * @param name 参数名称
     * @return 参数值
     */
    String getInitParameter(String name);
    
    /**
     * 获取所有初始化参数名称
     * @return 参数名称枚举
     */
    Enumeration<String> getInitParameterNames();
}