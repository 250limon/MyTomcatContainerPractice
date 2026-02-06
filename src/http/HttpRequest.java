package http;

import java.io.InputStream;
import java.util.Map;

/**
 * HTTP请求接口
 */
public interface HttpRequest {
    /**
     * 获取请求方法
     * @return 请求方法，如GET、POST等
     */
    String getMethod();
    void setMethod(String method);
    
    /**
     * 获取请求URL
     * @return 请求URL
     */
    String getUrl();

    void setUrl(String url);
    
    /**
     * 获取请求头
     * @param name 头名称
     * @return 头值
     */
    String getHeader(String name);
    void setHeader(String name, String value);
    
    /**
     * 获取所有请求头
     * @return 请求头映射
     */
    Map<String, String> getHeaders();
    void setHeaders(Map<String, String> headers);
    
    /**
     * 获取请求参数
     * @param name 参数名称
     * @return 参数值
     */
    String getParameter(String name);
    void setParameter(String name, String value);
    
    /**
     * 获取所有请求参数
     * @return 参数映射
     */
    Map<String, String> getParameters();
    void setParameters(Map<String, String> parameters);
    
    /**
     * 获取请求体
     * @return 请求体内容
     */
    String getBody();
    String getRequestData();
    void setBody(String body);


    /**
     * 判断是否保持连接
     * @return true表示保持连接，false表示关闭连接
     */
    boolean isKeepAlive();

    /**
     * 判断是否已经经过职责链的过滤
     * @return true 表示已经处理
     */
    boolean hasHandled();

    void setProtocol(String requestPart);

}