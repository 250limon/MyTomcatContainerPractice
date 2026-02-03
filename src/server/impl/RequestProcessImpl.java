package server.impl;

import filters.Filter;
import filters.FilterManager;
import http.HttpRequest;
import http.HttpResponse;
import server.Container;
import server.RequestProcessTemplate;
import server.SocketConvert;
import servlet.Servlet;

import java.net.Socket;

public class RequestProcessImpl extends RequestProcessTemplate {
    private Container container;
    private SocketConvert socketConvert;
    public RequestProcessImpl(Container container, SocketConvert socketConvert) {
        super(container,socketConvert);
    }

    @Override
    public HttpRequest filter(HttpRequest request) {
        return FilterManager.getInstance().getFirstFilter().process(request);
    }


}
