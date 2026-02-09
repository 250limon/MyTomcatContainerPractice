package spring.core.io;

import java.io.IOException;
import java.io.InputStream;

/**
 * 资源接口，代表可以从某处读取的资源
 */
public interface Resource {
    /**
     * 获取资源的输入流
     * @return 输入流
     * @throws IOException IO异常
     */
    InputStream getInputStream() throws IOException;
    
    /**
     * 获取资源的描述信息
     * @return 资源描述
     */
    String getDescription();
}