package server.impl;

import server.RequestDataString;

import java.nio.ByteBuffer;

public class RequestDataFromBuffer implements RequestDataString {
    @Override
    public String getRequestData(Object dataSource) {
        ByteBuffer buffer = (ByteBuffer) dataSource;
        // 检查是否读取到完整请求
        buffer.flip();
        byte[] data = new byte[buffer.remaining()];
        buffer.get(data);
        return new String(data);
    }
}
