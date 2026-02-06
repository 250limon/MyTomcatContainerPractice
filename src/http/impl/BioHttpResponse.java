package http.impl;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Map;

public class BioHttpResponse extends AbstractHttpResponse {
    private OutputStream outputStream;
    private PrintWriter writer;
    public BioHttpResponse(String responseData) throws IOException {
        super(responseData);
    }

    @Override
    public void write(byte[] data) {
        try {
            if (finished) {
                throw new IllegalStateException("Response has already been finished");
            }
            outputStream.write(data);
        } catch (IOException e) {
            e.printStackTrace();
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
}
