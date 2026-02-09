package server.impl;

import filters.FilterManager;
import http.HttpRequest;
import http.HttpResponse;
import http.impl.NioHttpResponse;
import server.Container;
import server.RequestProcess;

import java.nio.channels.SocketChannel;

/**
 * 基于NIO的请求处理模板
 */
public class NioRequestProcess extends RequestProcess {
     public NioRequestProcess(Container container) {
        super(container);
    }

    @Override
    public HttpRequest filter(HttpRequest request) {
        return FilterManager.getInstance().getFirstFilter().process(request);
    }

    @Override
    public HttpResponse createResponse(HttpRequest request) {
        return new NioHttpResponse((SocketChannel)request.getSource(),request.getRequestData());
    }
}