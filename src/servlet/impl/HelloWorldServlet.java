package servlet.impl;

import http.HttpRequest;
import http.HttpResponse;
import servlet.HttpServlet;

/**
 * 示例Servlet，用于测试请求处理
 */
public class HelloWorldServlet extends HttpServlet {
    @Override
    public void init() {
        System.out.println("HelloWorldServlet初始化完成");
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        String name = request.getParameter("name");
        if (name == null) {
            name = "World";
        }

        StringBuilder responseBody = new StringBuilder();
        responseBody.append("<html>");
        responseBody.append("<head><title>Hello</title></head>");
        responseBody.append("<body>");
        responseBody.append("<h1>Hello, " + name + "!</h1>");
        responseBody.append("<p>这是一个基于事件驱动的类Tomcat容器</p>");
        responseBody.append("<p>请求方法: " + request.getMethod() + "</p>");
        responseBody.append("<p>请求URL: " + request.getUrl() + "</p>");
        responseBody.append("</body>");
        responseBody.append("</html>");

        response.setBody(responseBody.toString());
        response.finish();
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        String name = request.getParameter("name");
        if (name == null) {
            name = "World";
        }

        StringBuilder responseBody = new StringBuilder();
        responseBody.append("<html>");
        responseBody.append("<head><title>Hello (POST)</title></head>");
        responseBody.append("<body>");
        responseBody.append("<h1>Hello, " + name + "! (POST)</h1>");
        responseBody.append("<p>POST请求已成功处理</p>");
        responseBody.append("</body>");
        responseBody.append("</html>");

        response.setBody(responseBody.toString());
        response.finish();
    }

    @Override
    public String getServletInfo() {
        return "HelloWorldServlet/1.0";
    }
}