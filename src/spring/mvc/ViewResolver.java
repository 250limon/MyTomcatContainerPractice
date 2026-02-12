package spring.mvc;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 视图解析器，用于解析视图名称并生成相应的视图
 */
public class ViewResolver {
    private String prefix = "/WEB-INF/views/";
    private String suffix = ".html";
    
    /**
     * 设置视图前缀
     * @param prefix 视图前缀
     */
    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }
    
    /**
     * 设置视图后缀
     * @param suffix 视图后缀
     */
    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }
    
    /**
     * 解析视图名称并生成视图
     * @param viewName 视图名称
     * @param model 模型数据
     * @return 视图
     */
    public View resolveView(String viewName, Map<String, Object> model) {
        // 构建视图文件路径
        String viewPath = prefix + viewName + suffix;
        
        // 创建视图对象
        View view = new View(viewPath);
        view.setModel(model);
        
        return view;
    }
    
    /**
     * 视图类，用于渲染视图
     */
    public static class View {
        private final String viewPath;
        private Map<String, Object> model;
        
        public View(String viewPath) {
            this.viewPath = viewPath;
            this.model = new HashMap<>();
        }
        
        /**
         * 设置模型数据
         * @param model 模型数据
         */
        public void setModel(Map<String, Object> model) {
            if (model != null) {
                this.model.putAll(model);
            }
        }
        
        /**
         * 获取模型数据
         * @return 模型数据
         */
        public Map<String, Object> getModel() {
            return model;
        }
        
        /**
         * 渲染视图
         * @return 渲染后的视图内容
         * @throws IOException IO异常
         */
        public String render() throws IOException {
            // 读取视图文件内容
            StringBuilder content = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new FileReader(viewPath))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    content.append(line).append("\n");
                }
            }
            
            // 替换模型数据
            String renderedContent = content.toString();
            for (Map.Entry<String, Object> entry : model.entrySet()) {
                String placeholder = "${" + entry.getKey() + "}";
                renderedContent = renderedContent.replace(placeholder, entry.getValue() != null ? entry.getValue().toString() : "");
            }
            
            return renderedContent;
        }
        
        /**
         * 获取视图路径
         * @return 视图路径
         */
        public String getViewPath() {
            return viewPath;
        }
    }
}