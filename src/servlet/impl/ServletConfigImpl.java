package servlet.impl;

import servlet.ServletConfig;
import servlet.ServletContext;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

/**
 * ServletConfig的具体实现类
 */
public class ServletConfigImpl implements ServletConfig {
    private String servletName;
    private ServletContext servletContext;
    private Map<String, String> initParams = new HashMap<>();

    public ServletConfigImpl(String servletName, ServletContext servletContext, Map<String, String> initParams) {
        this.servletName = servletName;
        this.servletContext = servletContext;
        if (initParams != null) {
            this.initParams.putAll(initParams);
        }
    }

    @Override
    public String getServletName() {
        return servletName;
    }

    @Override
    public ServletContext getServletContext() {
        return servletContext;
    }

    @Override
    public String getInitParameter(String name) {
        return initParams.get(name);
    }

    @Override
    public Enumeration<String> getInitParameterNames() {
        return new Vector<>(initParams.keySet()).elements();
    }

    public void addInitParameter(String name, String value) {
        initParams.put(name, value);
    }
}