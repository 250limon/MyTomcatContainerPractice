package server.impl;

import event.EventManager;
import event.impl.EventManagerImpl;
import server.Container;
import servlet.Servlet;
import servlet.ServletConfig;
import servlet.impl.ServletConfigImpl;
import servlet.impl.ServletContextImpl;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 * 容器的具体实现类，负责管理Servlet和请求映射
 */
public class ContainerImpl implements Container {
    private final Map<String, Servlet> servlets = new HashMap<>();
    private final Map<String, String> urlMappings = new HashMap<>();
    private final Map<String, ServletConfig> servletConfigs = new HashMap<>();
    private final ServletContextImpl servletContext = new ServletContextImpl();
    private final EventManager eventManager = EventManagerImpl.getInstance();
    private boolean initialized = false;

    @Override
    public void init() {
        if (initialized) {
            return;
        }

        System.out.println("容器初始化开始");
        
        // 注册默认Servlet
        try {
            registerServlet("helloWorld", "servlet.impl.HelloWorldServlet", new HashMap<>());
            mapServlet("/hello", "helloWorld");
        } catch (Exception e) {
            System.err.println("注册默认Servlet失败: " + e.getMessage());
        }
        
        // 注册DispatcherServlet（Spring MVC）
        try {
            registerServlet("dispatcherServlet", "spring.mvc.DispatcherServlet", new HashMap<>());
            mapServlet("/*", "dispatcherServlet");
        } catch (Exception e) {
            System.err.println("注册DispatcherServlet失败: " + e.getMessage());
        }

        initialized = true;
        System.out.println("容器初始化完成");
    }

    //创建Servlet实例，并进行初始化
    @Override
    public ServletConfig registerServlet(String servletName, String servletClass, Map<String, String> initParams) {
        try {
            // 加载Servlet类
            Class<?> clazz = Class.forName(servletClass);
            Servlet servlet = (Servlet) clazz.getDeclaredConstructor().newInstance();

            // 创建Servlet配置
            ServletConfigImpl config = new ServletConfigImpl(servletName, servletContext, initParams);

            // 初始化Servlet
            servlet.init(config);

            // 存储Servlet和配置
            servlets.put(servletName, servlet);
            servletConfigs.put(servletName, config);

            System.out.println("Servlet注册成功: " + servletName + " -> " + servletClass);
            return config;
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException |
                NoSuchMethodException | InvocationTargetException e) {
            throw new RuntimeException("注册Servlet失败: " + servletClass, e);
        }
    }

    @Override
    public void mapServlet(String urlPattern, String servletName) {
        if (!servlets.containsKey(servletName)) {
            throw new IllegalArgumentException("Servlet不存在: " + servletName);
        }

        urlMappings.put(urlPattern, servletName);
        System.out.println("URL映射成功: " + urlPattern + " -> " + servletName);
    }

    @Override
    public Servlet getServlet(String servletName) {
        return servlets.get(servletName);
    }

    @Override
    public String getServletNameByUrl(String url) {

        // 精确匹配
        if (urlMappings.containsKey(url)) {
            return urlMappings.get(url);
        }

        // 前缀匹配（简化实现，只支持/*形式）
        for (Map.Entry<String, String> entry : urlMappings.entrySet()) {
            String pattern = entry.getKey();
            if (pattern.endsWith("/*")) {
                String prefix = pattern.substring(0, pattern.length() - 2);
                if (url.startsWith(prefix)) {
                    return entry.getValue();
                }
            }
            else if (pattern.equals("/*")) {
                return entry.getValue();
            }
        }

        // 默认匹配
        if (urlMappings.containsKey("/")) {
            return urlMappings.get("/");
        }

        return null;
    }

    @Override
    public void destroy() {
        System.out.println("容器销毁开始");

        // 销毁所有Servlet
        for (Map.Entry<String, Servlet> entry : servlets.entrySet()) {
            try {
                entry.getValue().destroy();
                System.out.println("Servlet销毁成功: " + entry.getKey());
            } catch (Exception e) {
                System.err.println("销毁Servlet失败: " + entry.getKey() + " - " + e.getMessage());
            }
        }

        servlets.clear();
        urlMappings.clear();
        servletConfigs.clear();

        initialized = false;
        System.out.println("容器销毁完成");
    }

}