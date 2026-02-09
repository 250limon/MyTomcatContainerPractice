package spring.core.io.impl;

import spring.core.io.Resource;
import spring.core.io.ResourceLoader;

/**
 * 文件系统资源加载器实现类
 */
public class FileSystemResourceLoader implements ResourceLoader {
    @Override
    public Resource getResource(String location) {
        // 处理相对路径，转换为绝对路径
        if (!location.startsWith("/")) {
            location = System.getProperty("user.dir") + "/" + location;
        }
        return new FileSystemResource(location);
    }
}