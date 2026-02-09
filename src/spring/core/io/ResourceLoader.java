package spring.core.io;

/**
 * 资源加载器接口
 */
public interface ResourceLoader {
    /**
     * 根据资源路径获取资源
     * @param location 资源路径
     * @return 资源对象
     */
    Resource getResource(String location);
}