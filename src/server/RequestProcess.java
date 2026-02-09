package server;

import http.HttpRequest;
import http.HttpResponse;
import servlet.Servlet;

public abstract class RequestProcess {

    private Container container;
    protected RequestProcess(Container container) {
        this.container = container;
    }

    public abstract HttpRequest filter(HttpRequest request);
    public abstract HttpResponse createResponse(HttpRequest request);


    public void process(HttpRequest request){
        HttpResponse response = createResponse(request);
        HttpRequest handled_request = filter(request);
        String url = handled_request.getUrl();
        String servletName = container.getServletNameByUrl(url);
        // 获取Servlet
        Servlet servlet = container.getServlet(servletName);
        exceptionHandle(servletName,servlet,response,url);

        // 处理请求
        System.out.println("处理请求: " + request.getMethod() + " " + url + " -> " + servletName);
        try{
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
        }

    }

    private void exceptionHandle(String servletName, Servlet servlet, HttpResponse response,String url)
    {
        if (servletName == null) {
            // 404 处理
            response.setStatusCode(404);
            response.setBody("<html><body><h1>404 Not Found</h1><p>URL: " + url + "</p></body></html>");
            response.finish();
        }
        if (servlet == null) {
            // 500 处理
            response.setStatusCode(500);
            response.setBody("<html><body><h1>500 Internal Server Error</h1><p>Servlet不可用</p></body></html>");
            response.finish();
        }
    }

}