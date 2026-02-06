package http.impl;

import http.HttpResponse;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * 基于NIO的HTTP响应实现类
 */
public class NioHttpResponse implements HttpResponse {
    private int statusCode = 200;
    private String statusMessage = "OK";
    private Map<String, String> headers = new HashMap<>();
    private StringBuilder body = new StringBuilder();
    private SocketChannel clientChannel;
    private boolean finished = false;
    private String requestData;

    public NioHttpResponse(SocketChannel clientChannel, String requestData) {
        this.clientChannel = clientChannel;
        this.requestData = requestData;
        // 设置默认响应头
        setHeader("Server", "MyNioTomcat/1.0");
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
    public void write(byte[] data) {
        if (finished) {
            throw new IllegalStateException("Response has already been finished");
        }
        // 对于NIO实现，write方法直接写入到body中
        body.append(new String(data, StandardCharsets.UTF_8));
    }

    @Override
    public void finish() {
        if (finished) {
            return;
        }

        try {
            // 构建完整的HTTP响应
            String response = buildResponse();
            
            // 使用NIO方式发送响应
            sendResponse(response);
            
            finished = true;
            System.out.println("响应发送完成: " + statusCode + " " + statusMessage);
            
        } catch (IOException e) {
            System.err.println("发送响应失败: " + e.getMessage());
            e.printStackTrace();
            // 标记为已完成，避免重复处理
            finished = true;
        }
    }

    /**
     * 构建完整的HTTP响应字符串
     */
    private String buildResponse() {
        // 设置Content-Length
        byte[] bodyBytes = body.toString().getBytes(StandardCharsets.UTF_8);
        setHeader("Content-Length", String.valueOf(bodyBytes.length));

        // 构建响应
        StringBuilder responseBuilder = new StringBuilder();
        // 响应行
        responseBuilder.append("HTTP/1.1 ").append(statusCode).append(" ").append(statusMessage).append("\r\n");
        // 响应头
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            responseBuilder.append(entry.getKey()).append(": ").append(entry.getValue()).append("\r\n");
        }
        // 空行分隔响应头和响应体
        responseBuilder.append("\r\n");
        // 响应体
        responseBuilder.append(body.toString());
        
        return responseBuilder.toString();
    }

    /**
     * 使用NIO方式发送响应
     */
    private void sendResponse(String response) throws IOException {
        if (clientChannel == null || !clientChannel.isOpen()) {
            throw new IOException("客户端通道已关闭");
        }

        byte[] responseBytes = response.getBytes(StandardCharsets.UTF_8);
        ByteBuffer buffer = ByteBuffer.wrap(responseBytes);
        
        // 发送响应数据
        while (buffer.hasRemaining()) {
            int bytesWritten = clientChannel.write(buffer);
            if (bytesWritten == -1) {
                throw new IOException("写入响应时连接已关闭");
            }
        }
    }

    /**
     * 设置是否保持连接
     */
    public void setKeepAlive(boolean keepAlive) {
        if (keepAlive) {
            setHeader("Connection", "keep-alive");
        } else {
            setHeader("Connection", "close");
        }
    }

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

    /**
     * 发送错误响应
     */
    public void sendError(int statusCode, String message) {
        setStatusCode(statusCode);
        setBody("<html><body><h1>" + statusCode + " " + getStatusMessage(statusCode) + "</h1><p>" + message + "</p></body></html>");
        finish();
    }

    /**
     * 发送重定向响应
     */
    public void sendRedirect(String location) {
        setStatusCode(302);
        setHeader("Location", location);
        finish();
    }

    /**
     * 获取客户端通道
     */
    public SocketChannel getClientChannel() {
        return clientChannel;
    }
}