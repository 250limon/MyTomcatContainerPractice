package servlet.impl;

import servlet.ServletContext;

import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

/**
 * ServletContext的具体实现类
 */
public class ServletContextImpl implements ServletContext {
    private String contextPath = "/";
    private Map<String, String> initParams = new HashMap<>();
    private Map<String, Object> attributes = new HashMap<>();
    private final String serverInfo = "MyTomcat/1.0";

    public ServletContextImpl() {
        // 初始化默认参数
        initParams.put("server.version", serverInfo);
    }

    @Override
    public String getContextPath() {
        return contextPath;
    }

    @Override
    public String getInitParameter(String name) {
        return initParams.get(name);
    }

    @Override
    public Enumeration<String> getInitParameterNames() {
        return new Vector<>(initParams.keySet()).elements();
    }

    @Override
    public String getRealPath(String path) {
        // 简化实现，实际应该返回文件系统的真实路径
        return System.getProperty("user.dir") + "/webapps" + path;
    }

    @Override
    public InputStream getResourceAsStream(String path) {
        // 简化实现，实际应该从文件系统或资源中读取
        return getClass().getResourceAsStream("/" + path);
    }

    @Override
    public void setAttribute(String name, Object object) {
        if (name == null) {
            throw new IllegalArgumentException("Attribute name cannot be null");
        }
        attributes.put(name, object);
    }

    @Override
    public Object getAttribute(String name) {
        return attributes.get(name);
    }

    @Override
    public void removeAttribute(String name) {
        attributes.remove(name);
    }

    @Override
    public Enumeration<String> getAttributeNames() {
        return new Vector<>(attributes.keySet()).elements();
    }

    @Override
    public String getServerInfo() {
        return serverInfo;
    }

    public void setContextPath(String contextPath) {
        this.contextPath = contextPath;
    }

    public void addInitParameter(String name, String value) {
        initParams.put(name, value);
    }
}