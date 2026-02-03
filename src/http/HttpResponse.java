package http;

import java.util.Map;

/**
 * HTTP响应接口
 */
public interface HttpResponse {
    /**
     * 设置响应状态码
     * @param statusCode 状态码
     */
    void setStatusCode(int statusCode);
    
    /**
     * 获取响应状态码
     * @return 状态码
     */
    int getStatusCode();
    
    /**
     * 设置响应头
     * @param name 头名称
     * @param value 头值
     */
    void setHeader(String name, String value);
    
    /**
     * 获取响应头
     * @param name 头名称
     * @return 头值
     */
    String getHeader(String name);
    
    /**
     * 获取所有响应头
     * @return 响应头映射
     */
    Map<String, String> getHeaders();
    
    /**
     * 设置响应体
     * @param body 响应体内容
     */
    void setBody(String body);
    
    /**
     * 获取响应体
     * @return 响应体内容
     */
    String getBody();
    
    /**
     * 写入响应数据
     * @param data 要写入的数据
     */
    void write(byte[] data);
    
    /**
     * 完成响应
     */
    void finish();
}