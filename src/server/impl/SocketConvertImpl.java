package server.impl;

import http.HttpRequest;
import http.HttpResponse;
import http.impl.HttpRequestImpl;
import http.impl.HttpResponseImpl;
import server.SocketConvert;

import java.io.IOException;
import java.net.Socket;

public  class SocketConvertImpl implements SocketConvert {
    @Override
    public HttpRequest convertToRequest(Socket socket) {
        try {
            return new HttpRequestImpl(socket);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public HttpResponse convertToResponse(Socket socket) {
        try {
            return new HttpResponseImpl(socket);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
