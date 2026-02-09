package spring.core.io.impl;

import spring.core.io.Resource;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 文件系统资源实现类
 */
public class FileSystemResource implements Resource {
    private final File file;
    private final String path;
    
    public FileSystemResource(String path) {
        this.path = path;
        this.file = new File(path);
    }
    
    @Override
    public InputStream getInputStream() throws IOException {
        return new FileInputStream(file);
    }
    
    @Override
    public String getDescription() {
        return "file [" + file.getAbsolutePath() + "]";
    }
    
    public File getFile() {
        return file;
    }
    
    public String getPath() {
        return path;
    }
}