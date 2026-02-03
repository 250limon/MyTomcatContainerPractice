package http.impl;

import http.HttpResponse;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

/**
 * HTTP响应的具体实现类
 */
public class HttpResponseImpl implements HttpResponse {
    private int statusCode = 200;
    private String statusMessage = "OK";
    private Map<String, String> headers = new HashMap<>();
    private StringBuilder body = new StringBuilder();
    private OutputStream outputStream;
    private PrintWriter writer;
    private boolean finished = false;

    public HttpResponseImpl(Socket socket) throws IOException {
        this.outputStream = socket.getOutputStream();
        this.writer = new PrintWriter(outputStream, true);
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
    public void write(byte[] data) {
        try {
            if (finished) {
                throw new IllegalStateException("Response has already been finished");
            }
            outputStream.write(data);
        } catch (IOException e) {
            throw new RuntimeException("Error writing response data", e);
        }
    }

    @Override
    public void finish() {
        if (finished) {
            return;
        }

        try {
            // 设置Content-Length
            byte[] bodyBytes = body.toString().getBytes("UTF-8");
            setHeader("Content-Length", String.valueOf(bodyBytes.length));

            // 使用StringBuilder构建完整响应，减少IO操作次数
            StringBuilder responseBuilder = new StringBuilder();
            // 响应行
            responseBuilder.append("HTTP/1.1 ").append(statusCode).append(" ").append(statusMessage).append("\r\n");
            // 响应头
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                responseBuilder.append(entry.getKey()).append(": ").append(entry.getValue()).append("\r\n");
            }
            // 空行分隔响应头和响应体
            responseBuilder.append("\r\n");
            
            // 发送响应头和空行
            byte[] headersBytes = responseBuilder.toString().getBytes("UTF-8");
            outputStream.write(headersBytes);
            // 发送响应体
            outputStream.write(bodyBytes);
            outputStream.flush();

            finished = true;
        } catch (java.net.SocketException e) {
            // 处理客户端连接关闭的情况，这是正常现象
            if (e.getMessage().contains("Software caused connection abort") || 
                e.getMessage().contains("你的主机中的软件中止了一个已建立的连接")) {
                System.out.println("客户端已关闭连接，响应发送中断");
                finished = true; // 标记为已完成，避免重复处理
            } else {
                throw new RuntimeException("Error finishing response", e);
            }
        } catch (IOException e) {
            throw new RuntimeException("Error finishing response", e);
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
}