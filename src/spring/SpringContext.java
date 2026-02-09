package spring;

import spring.beans.BeanDefinition;
import spring.beans.BeanFactory;
import spring.beans.impl.BeanFactoryImpl;
import spring.core.io.ResourceLoader;
import spring.core.io.impl.FileSystemResourceLoader;
import spring.xml.XmlBeanDefinitionReader;

import java.util.Map;

/**
 * Spring应用上下文，提供IoC容器的高级接口
 */
public class SpringContext {
    private BeanFactory beanFactory;
    
    /**
     * 根据配置文件路径创建Spring上下文
     * @param configLocation 配置文件路径
     */
    public SpringContext(String configLocation) {
        // 创建资源加载器
        ResourceLoader resourceLoader = new FileSystemResourceLoader();
        
        // 创建Bean定义读取器
        XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(resourceLoader);
        
        // 读取配置文件，解析Bean定义
        Map<String, BeanDefinition> beanDefinitions = reader.loadBeanDefinitions(configLocation);
        
        // 创建Bean工厂并初始化
        beanFactory = new BeanFactoryImpl(beanDefinitions);
    }
    
    /**
     * 根据Bean ID获取Bean实例
     * @param beanId Bean ID
     * @param <T> Bean类型
     * @return Bean实例
     */
    @SuppressWarnings("unchecked")
    public <T> T getBean(String beanId) {
        return (T) beanFactory.getBean(beanId);
    }
    
    /**
     * 获取所有Bean的ID
     * @return Bean ID列表
     */
    public String[] getBeanDefinitionNames() {
        return beanFactory.getBeanDefinitionNames();
    }
    
    /**
     * 获取指定类型的所有Bean实例
     * @param type Bean类型
     * @param <T> Bean类型
     * @return Bean实例映射
     */
    public <T> Map<String, T> getBeansOfType(Class<T> type) {
        return beanFactory.getBeansOfType(type);
    }
}