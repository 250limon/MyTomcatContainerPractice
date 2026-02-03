package server;

import http.HttpRequest;
import http.HttpResponse;
import servlet.Servlet;

import java.io.IOException;
import java.net.Socket;

public abstract class RequestProcessTemplate {

    private Container container;
    private SocketConvert socketConvert;
    protected RequestProcessTemplate(Container container, SocketConvert socketConvert) {
        this.container = container;
        this.socketConvert = socketConvert;
    }

    public abstract HttpRequest filter(HttpRequest request);


    public void process(Socket clientSocket){
        HttpRequest request = convertToRequest(clientSocket);
        HttpResponse response = convertToResponse(clientSocket);
        HttpRequest handled_request = filter(request);
        String url = handled_request.getUrl();
        String servletName = container.getServletNameByUrl(url);
        // 获取Servlet
        Servlet servlet = container.getServlet(servletName);
        exceptionHandle(servletName,servlet,response,url);
        // 处理请求
        System.out.println("处理请求: " + request.getMethod() + " " + url + " -> " + servletName);
        try{
            // 设置响应的Connection头与请求一致
            boolean keepAlive = handled_request.isKeepAlive();
            ((http.impl.HttpResponseImpl)response).setKeepAlive(keepAlive);
            servlet.service(handled_request, response);
        }catch (Exception e) {
            System.err.println("处理请求时发生异常: " + e.getMessage());
            e.printStackTrace();
            // 异常情况下发送500错误响应
            if (response != null) {
                try {
                    response.setStatusCode(500);
                    response.setBody("<html><body><h1>500 Internal Server Error</h1><p>" + e.getMessage() + "</p></body></html>");
                    response.finish();
                } catch (Exception ex) {
                    System.err.println("发送错误响应失败: " + ex.getMessage());
                }
            }
        } finally {
            // 检查是否需要保持连接
            boolean keepAlive = handled_request.isKeepAlive();
            if (!keepAlive && clientSocket != null && !clientSocket.isClosed()) {
                try {
                    clientSocket.close();
                    System.out.println("关闭连接: " + clientSocket.getInetAddress().getHostAddress());
                } catch (IOException e) {
                    System.err.println("关闭Socket时发生异常: " + e.getMessage());
                }
            } else if (keepAlive) {
                System.out.println("保持连接: " + clientSocket.getInetAddress().getHostAddress());
            }
        }
    }

    private HttpRequest convertToRequest(Socket clientSocket){
        HttpRequest request = socketConvert.convertToRequest(clientSocket);
        return request;
    }
    private HttpResponse convertToResponse(Socket clientSocket){
        HttpResponse response = socketConvert.convertToResponse(clientSocket);
        return response;
    }
    private void exceptionHandle(String servletName, Servlet servlet, HttpResponse response,String url)
    {
        if (servletName == null) {
            // 404 处理
            response.setStatusCode(404);
            response.setBody("<html><body><h1>404 Not Found</h1><p>URL: " + url + "</p></body></html>");
            response.finish();
            return;
        }
        if (servlet == null) {
            // 500 处理
            response.setStatusCode(500);
            response.setBody("<html><body><h1>500 Internal Server Error</h1><p>Servlet不可用</p></body></html>");
            response.finish();
            return;
        }
    }

}