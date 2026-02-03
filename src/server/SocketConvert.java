package server;

import http.HttpRequest;
import http.HttpResponse;

import java.net.Socket;

public interface SocketConvert {
    /**
     * 将Socket转换为HttpRequest
     * @param socket 客户端Socket
     * @return HttpRequest对象
     */
    HttpRequest convertToRequest(Socket socket);
    HttpResponse convertToResponse(Socket socket);
}
