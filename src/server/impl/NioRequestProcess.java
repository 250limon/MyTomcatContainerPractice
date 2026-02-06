package server.impl;

import http.HttpRequest;
import http.HttpResponse;
import server.Container;
import server.RequestProcessTemplate;

/**
 * 基于NIO的请求处理模板
 */
public class NioRequestProcess extends RequestProcessTemplate {
     NioRequestProcess(Container container, Convert socketConvert) {
        super(container, socketConvert);
    }

    @Override
    public HttpRequest filter(HttpRequest request) {
        return null;
    }

    @Override
    public HttpResponse createResponse(HttpRequest request) {
        return null;
    }
}