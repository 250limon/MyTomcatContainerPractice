package servlet;

import http.HttpRequest;
import http.HttpResponse;

/**
 * HTTP Servlet抽象类，提供GET和POST方法的默认实现
 */
public abstract class HttpServlet implements Servlet {
    private ServletConfig config;

    @Override
    public void init(ServletConfig config) {
        this.config = config;
        init();
    }

    /**
     * 初始化方法，子类可以重写
     */
    protected void init() {
        // 默认实现为空
    }

    @Override
    public ServletConfig getServletConfig() {
        return config;
    }

    @Override
    public void service(HttpRequest request, HttpResponse response) {
        String method = request.getMethod();
        
        switch (method) {
            case "GET":
                doGet(request, response);
                break;
            case "POST":
                doPost(request, response);
                break;
            case "PUT":
                doPut(request, response);
                break;
            case "DELETE":
                doDelete(request, response);
                break;
            default:
                response.setStatusCode(405);
                response.setBody("<html><body><h1>405 Method Not Allowed</h1></body></html>");
                response.finish();
        }
    }

    /**
     * 处理GET请求
     */
    protected void doGet(HttpRequest request, HttpResponse response) {
        response.setStatusCode(405);
        response.setBody("<html><body><h1>405 Method Not Allowed</h1></body></html>");
        response.finish();
    }

    /**
     * 处理POST请求
     */
    protected void doPost(HttpRequest request, HttpResponse response) {
        response.setStatusCode(405);
        response.setBody("<html><body><h1>405 Method Not Allowed</h1></body></html>");
        response.finish();
    }

    /**
     * 处理PUT请求
     */
    protected void doPut(HttpRequest request, HttpResponse response) {
        response.setStatusCode(405);
        response.setBody("<html><body><h1>405 Method Not Allowed</h1></body></html>");
        response.finish();
    }

    /**
     * 处理DELETE请求
     */
    protected void doDelete(HttpRequest request, HttpResponse response) {
        response.setStatusCode(405);
        response.setBody("<html><body><h1>405 Method Not Allowed</h1></body></html>");
        response.finish();
    }

    @Override
    public String getServletInfo() {
        return "HttpServlet/1.0";
    }

    @Override
    public void destroy() {
        // 默认实现为空
    }
}