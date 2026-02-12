package spring.mvc;

import spring.mvc.annotation.RequestMapping;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 请求映射处理器，用于将请求URL映射到对应的处理方法
 */
public class HandlerMapping {
    private final Map<String, HandlerMethod> handlerMethods;
    private final Map<String, List<Pattern>> pathPatterns;
    
    public HandlerMapping() {
        this.handlerMethods = new HashMap<>();
        this.pathPatterns = new HashMap<>();
    }
    
    /**
     * 注册处理器方法
     * @param beanName Bean名称
     * @param beanInstance Bean实例
     * @param controllerClass 控制器类
     */
    public void registerHandlerMethod(String beanName, Object beanInstance, Class<?> controllerClass) {
        // 检查类是否带有RequestMapping注解
        RequestMapping classMapping = controllerClass.getAnnotation(RequestMapping.class);
        String classUrl = "";
        if (classMapping != null) {
            classUrl = classMapping.value();
            // 确保URL以/开头
            if (!classUrl.isEmpty() && !classUrl.startsWith("/")) {
                classUrl = "/" + classUrl;
            }
        }
        
        // 获取所有方法
        Method[] methods = controllerClass.getDeclaredMethods();
        for (Method method : methods) {
            // 检查方法是否带有RequestMapping注解
            RequestMapping methodMapping = method.getAnnotation(RequestMapping.class);
            if (methodMapping != null) {
                String methodUrl = methodMapping.value();
                // 构建完整的URL
                String fullUrl = classUrl;
                if (!methodUrl.isEmpty()) {
                    if (!methodUrl.startsWith("/")) {
                        methodUrl = "/" + methodUrl;
                    }
                    fullUrl += methodUrl;
                }
                
                // 确保URL以/开头
                if (fullUrl.isEmpty()) {
                    fullUrl = "/";
                } else if (!fullUrl.startsWith("/")) {
                    fullUrl = "/" + fullUrl;
                }
                
                // 创建HandlerMethod
                HandlerMethod handlerMethod = new HandlerMethod(beanName, beanInstance, method, methodMapping);
                
                // 添加到映射中
                handlerMethods.put(fullUrl, handlerMethod);
                
                // 编译URL为正则表达式（用于处理路径变量）
                String regexUrl = convertToRegex(fullUrl);
                Pattern pattern = Pattern.compile(regexUrl);
                
                // 添加到路径模式映射中
                pathPatterns.computeIfAbsent(fullUrl, k -> new ArrayList<>()).add(pattern);
                
                System.out.println("Registered handler method: " + fullUrl + " -> " + controllerClass.getName() + "." + method.getName());
            }
        }
    }
    
    /**
     * 根据请求URL查找对应的处理方法
     * @param requestUrl 请求URL
     * @return 处理方法信息
     */
    public HandlerMethod getHandlerMethod(String requestUrl) {
        // 首先尝试精确匹配
        HandlerMethod handlerMethod = handlerMethods.get(requestUrl);
        if (handlerMethod != null) {
            return handlerMethod;
        }
        
        // 如果没有精确匹配，尝试正则表达式匹配（处理路径变量）
        for (Map.Entry<String, List<Pattern>> entry : pathPatterns.entrySet()) {
            for (Pattern pattern : entry.getValue()) {
                Matcher matcher = pattern.matcher(requestUrl);
                if (matcher.matches()) {
                    return handlerMethods.get(entry.getKey());
                }
            }
        }
        
        return null;
    }
    
    /**
     * 将URL转换为正则表达式（用于处理路径变量）
     * @param url URL
     * @return 正则表达式
     */
    private String convertToRegex(String url) {
        // 将{paramName}转换为正则表达式组
        return url.replaceAll("\\{([^}]+)\\}", "([^/]+)");
    }
    
    /**
     * 处理器方法信息
     */
    public static class HandlerMethod {
        private final String beanName;
        private final Object beanInstance;
        private final Method method;
        private final RequestMapping requestMapping;
        
        public HandlerMethod(String beanName, Object beanInstance, Method method, RequestMapping requestMapping) {
            this.beanName = beanName;
            this.beanInstance = beanInstance;
            this.method = method;
            this.requestMapping = requestMapping;
        }
        
        public String getBeanName() {
            return beanName;
        }
        
        public Object getBeanInstance() {
            return beanInstance;
        }
        
        public Method getMethod() {
            return method;
        }
        
        public RequestMapping getRequestMapping() {
            return requestMapping;
        }
        
        @Override
        public String toString() {
            return "HandlerMethod{beanName='" + beanName + "', method=" + method + "}";
        }
    }
}