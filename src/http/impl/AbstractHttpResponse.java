package http.impl;

import http.HttpResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * HTTP响应的具体实现类
 */
public abstract class AbstractHttpResponse implements HttpResponse {
    protected int statusCode = 200;
    protected String statusMessage = "OK";
    protected Map<String, String> headers = new HashMap<>();
    protected StringBuilder body = new StringBuilder();
    protected boolean finished = false;

    public AbstractHttpResponse(String responseData) throws IOException {
        //this.outputStream = new ByteArrayOutputStream();
        //this.writer = new PrintWriter(outputStream, true);
        // 设置默认响应头
        setHeader("Server", "MyTomcat/1.0");
        setHeader("Content-Type", "text/html;charset=UTF-8");
    }

    @Override
    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
        this.statusMessage = getStatusMessage(statusCode);
    }

    @Override
    public int getStatusCode() {
        return statusCode;
    }

    @Override
    public void setHeader(String name, String value) {
        headers.put(name, value);
    }

    @Override
    public String getHeader(String name) {
        return headers.get(name);
    }

    @Override
    public Map<String, String> getHeaders() {
        return new HashMap<>(headers);
    }

    @Override
    public void setBody(String body) {
        this.body.setLength(0);
        this.body.append(body);
    }

    @Override
    public String getBody() {
        return body.toString();
    }

    @Override
    public abstract void write(byte[] data);
    @Override
    public abstract void finish();
    /**
     * 根据状态码获取状态消息
     */
    private String getStatusMessage(int statusCode) {
        switch (statusCode) {
            case 200: return "OK";
            case 400: return "Bad Request";
            case 404: return "Not Found";
            case 500: return "Internal Server Error";
            case 405: return "Method Not Allowed";
            default: return "Unknown Status";
        }
    }

}