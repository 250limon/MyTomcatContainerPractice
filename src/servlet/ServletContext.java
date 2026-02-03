package servlet;

import java.util.Enumeration;
import java.io.InputStream;

/**
 * Servlet上下文接口，提供Servlet运行的环境
 */
public interface ServletContext {
    /**
     * 获取上下文路径
     * @return 上下文路径
     */
    String getContextPath();
    
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
    
    /**
     * 获取资源的真实路径
     * @param path 资源路径
     * @return 真实路径
     */
    String getRealPath(String path);
    
    /**
     * 获取资源的输入流
     * @param path 资源路径
     * @return 输入流
     */
    InputStream getResourceAsStream(String path);
    
    /**
     * 设置属性
     * @param name 属性名称
     * @param object 属性值
     */
    void setAttribute(String name, Object object);
    
    /**
     * 获取属性
     * @param name 属性名称
     * @return 属性值
     */
    Object getAttribute(String name);
    
    /**
     * 移除属性
     * @param name 属性名称
     */
    void removeAttribute(String name);
    
    /**
     * 获取所有属性名称
     * @return 属性名称枚举
     */
    Enumeration<String> getAttributeNames();
    
    /**
     * 获取Servlet容器信息
     * @return 容器信息
     */
    String getServerInfo();
}