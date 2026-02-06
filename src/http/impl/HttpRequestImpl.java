package http.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * HTTP请求的具体实现类
 */
public class HttpRequestImpl implements http.HttpRequest {
    private String method;
    private String url;
    private String protocol;
    private Map<String, String> headers = new HashMap<>();
    private Map<String, String> parameters = new HashMap<>();
    private String body;
    private String requestData;
    private Object source;

    public HttpRequestImpl(String requestData, Object source)  {
        this.requestData = requestData;
        // 重置请求信息
        this.method = null;
        this.url = null;
        this.protocol = null;
        this.headers.clear();
        this.parameters.clear();
        this.body = null;
        this.source = source;
    }
    public Object getSource() {
        return source;
    }

    public String getRequestData() {
        return requestData;
    }

    @Override
    public String getMethod() {
        return method;
    }

    @Override
    public void setMethod(String method) {
        this.method = method;
    }

    @Override
    public String getUrl() {
        return url;
    }

    @Override
    public void setUrl(String url) {
       this.url = url;
    }

    @Override
    public String getHeader(String name) {
        return headers.get(name);
    }

    @Override
    public void setHeader(String name, String value) {
        this.headers.put(name, value);
    }

    @Override
    public Map<String, String> getHeaders() {
        return new HashMap<>(headers);
    }

    @Override
    public void setHeaders(Map<String, String> headers) {
           this.headers.putAll(headers);
    }

    @Override
    public String getParameter(String name) {
        return parameters.get(name);
    }

    @Override
    public void setParameter(String name, String value) {
     this.parameters.put(name, value);
    }

    @Override
    public Map<String, String> getParameters() {
        return new HashMap<>(parameters);
    }

    @Override
    public void setParameters(Map<String, String> parameters) {
       this.parameters.putAll(parameters);
    }

    @Override
    public String getBody() {
        return body;
    }

    @Override
    public void setBody(String body) {
      this.body = body;
    }

    public String getProtocol() {
        return protocol;
    }

    @Override
    public boolean isKeepAlive() {
        String connectionHeader = headers.get("Connection");
        if (connectionHeader != null) {
            return "keep-alive".equalsIgnoreCase(connectionHeader);
        }
        // HTTP/1.1默认是keep-alive
        return "HTTP/1.1".equals(protocol);
    }

    public void setRequestData(String requestData) {
        this.requestData = requestData;
    }

    @Override
    public boolean hasHandled() {
        return false;
    }

    @Override
    public void setProtocol(String requestPart) {
        this.protocol = requestPart;
    }
}