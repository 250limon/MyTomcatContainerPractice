package spring.mvc;

import http.HttpRequest;
import http.HttpResponse;
import servlet.HttpServlet;
import spring.SpringContext;
import spring.mvc.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 前端控制器，用于接收所有请求并分发到相应的处理方法
 */
public class DispatcherServlet extends HttpServlet {
    private SpringContext springContext;
    private HandlerMapping handlerMapping;
    private HandlerAdapter handlerAdapter;
    private ViewResolver viewResolver;
    
    public DispatcherServlet() {
        this.handlerMapping = new HandlerMapping();
        this.handlerAdapter = new HandlerAdapter();
        this.viewResolver = new ViewResolver();
    }
    
    @Override
    protected void init() {
        super.init();
        
        // 创建Spring上下文，扫描控制器和服务类
        String[] basePackages = {"spring.test.controller", "spring.test.service"};
        SpringContext springContext = new SpringContext(basePackages);
        
        // 初始化DispatcherServlet
        init(springContext);
    }
    
    /**
     * 初始化DispatcherServlet
     * @param springContext Spring上下文
     */
    public void init(SpringContext springContext) {
        this.springContext = springContext;
        
        // 初始化视图解析器
        viewResolver.setPrefix("src/webapp/WEB-INF/views/");
        viewResolver.setSuffix(".html");
        
        // 扫描并注册所有控制器
        registerControllers();
        
        System.out.println("DispatcherServlet initialized successfully");
    }
    
    /**
     * 扫描并注册所有控制器
     */
    private void registerControllers() {
        // 获取所有Bean名称
        String[] beanNames = springContext.getBeanDefinitionNames();
        
        for (String beanName : beanNames) {
            // 获取Bean实例
            Object beanInstance = springContext.getBean(beanName);
            
            // 检查Bean是否是控制器
            if (beanInstance.getClass().isAnnotationPresent(spring.mvc.annotation.Controller.class)) {
                // 注册控制器的处理器方法
                handlerMapping.registerHandlerMethod(beanName, beanInstance, beanInstance.getClass());
            }
        }
    }
    
    @Override
    public void service(HttpRequest request, HttpResponse response) {
        try {
            // 获取请求URL
            String requestUrl = request.getUrl();
            
            // 获取请求方法
            String requestMethod = request.getMethod();
            
            // 获取请求参数
            Map<String, String> requestParameters = request.getParameters();
            
            // 获取请求体
            String requestBody = request.getBody();
            
            System.out.println("DispatcherServlet received request: " + requestMethod + " " + requestUrl);
            
            // 根据请求URL查找对应的处理器方法
            HandlerMapping.HandlerMethod handlerMethod = handlerMapping.getHandlerMethod(requestUrl);
            
            if (handlerMethod != null) {
                // 调用处理器方法
                Object result = handlerAdapter.handle(handlerMethod, requestUrl, requestParameters, requestBody);
                
                // 处理响应结果
                handleResponse(handlerMethod, result, request, response);
            } else {
                // 如果没有找到对应的处理器方法，返回404错误
                response.setStatusCode(404);
                response.setBody("<html><body><h1>404 Not Found</h1></body></html>");
                response.finish();
            }
            
        } catch (Exception e) {
            // 处理异常
            System.err.println("Error processing request: " + e.getMessage());
            e.printStackTrace();
            
            try {
                // 返回500错误
                response.setStatusCode(500);
                response.setBody("<html><body><h1>500 Internal Server Error</h1><p>" + e.getMessage() + "</p></body></html>");
                response.finish();
            } catch (Exception ex) {
                System.err.println("Error sending error response: " + ex.getMessage());
            }
        }
    }
    
    /**
     * 处理响应结果
     * @param handlerMethod 处理器方法信息
     * @param result 方法返回值
     * @param request 请求对象
     * @param response 响应对象
     * @throws Exception 处理过程中可能抛出的异常
     */
    private void handleResponse(HandlerMapping.HandlerMethod handlerMethod, Object result, HttpRequest request, HttpResponse response) throws Exception {
        // 检查方法或类是否带有ResponseBody注解
        boolean hasResponseBody = handlerMethod.getMethod().isAnnotationPresent(ResponseBody.class) ||
                                 handlerMethod.getBeanInstance().getClass().isAnnotationPresent(ResponseBody.class);
        
        if (hasResponseBody) {
            // 如果带有ResponseBody注解，直接将结果作为响应体返回
            response.setHeader("Content-Type", "application/json;charset=UTF-8");
            response.setBody(result != null ? result.toString() : "");
            response.finish();
        } else {
            // 如果没有ResponseBody注解，将结果作为视图名称或模型数据处理
            if (result instanceof String) {
                // 如果返回值是字符串，将其作为视图名称
                String viewName = (String) result;
                
                // 解析视图并渲染
                ViewResolver.View view = viewResolver.resolveView(viewName, new HashMap<>());
                String renderedView = view.render();
                
                // 设置响应内容
                response.setHeader("Content-Type", "text/html;charset=UTF-8");
                response.setBody(renderedView);
                response.finish();
            } else if (result instanceof Map) {
                // 如果返回值是Map，将其作为模型数据，默认使用请求URL作为视图名称
                @SuppressWarnings("unchecked")
                Map<String, Object> model = (Map<String, Object>) result;
                String viewName = request.getUrl().substring(1).replace("/", "-");
                
                // 解析视图并渲染
                ViewResolver.View view = viewResolver.resolveView(viewName, model);
                String renderedView = view.render();
                
                // 设置响应内容
                response.setHeader("Content-Type", "text/html;charset=UTF-8");
                response.setBody(renderedView);
                response.finish();
            } else {
                // 如果返回值是其他类型，默认使用请求URL作为视图名称
                String viewName = request.getUrl().substring(1).replace("/", "-");
                
                // 解析视图并渲染
                ViewResolver.View view = viewResolver.resolveView(viewName, new HashMap<>());
                String renderedView = view.render();
                
                // 设置响应内容
                response.setHeader("Content-Type", "text/html;charset=UTF-8");
                response.setBody(renderedView);
                response.finish();
            }
        }
    }
    
    /**
     * 获取HandlerMapping
     * @return HandlerMapping
     */
    public HandlerMapping getHandlerMapping() {
        return handlerMapping;
    }
    
    /**
     * 获取HandlerAdapter
     * @return HandlerAdapter
     */
    public HandlerAdapter getHandlerAdapter() {
        return handlerAdapter;
    }
    
    /**
     * 获取ViewResolver
     * @return ViewResolver
     */
    public ViewResolver getViewResolver() {
        return viewResolver;
    }
}