package spring;

import spring.beans.BeanDefinition;
import spring.beans.BeanFactory;
import spring.beans.impl.BeanFactoryImpl;
import spring.core.io.ResourceLoader;
import spring.core.io.impl.FileSystemResourceLoader;
import spring.core.scan.PackageScanner;
import spring.xml.XmlBeanDefinitionReader;

import java.util.HashMap;
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
     * 根据包名创建Spring上下文（基于注解）
     * @param basePackages 要扫描的包列表
     */
    public SpringContext(String... basePackages) {
        // 创建包扫描器
        PackageScanner packageScanner = new PackageScanner();
        
        // 扫描包，获取Bean定义
        Map<String, BeanDefinition> beanDefinitions = packageScanner.scan(basePackages);
        
        // 创建Bean工厂并初始化
        beanFactory = new BeanFactoryImpl(beanDefinitions);
    }
    
    /**
     * 根据配置文件路径和包名创建Spring上下文（同时支持配置文件和注解）
     * @param configLocation 配置文件路径
     * @param basePackages 要扫描的包列表
     */
    public SpringContext(String configLocation, String... basePackages) {
        // 创建资源加载器
        ResourceLoader resourceLoader = new FileSystemResourceLoader();
        
        // 创建Bean定义读取器
        XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(resourceLoader);
        
        // 读取配置文件，解析Bean定义
        Map<String, BeanDefinition> beanDefinitions = reader.loadBeanDefinitions(configLocation);
        
        // 如果有包扫描需求，合并注解扫描的Bean定义
        if (basePackages != null && basePackages.length > 0) {
            PackageScanner packageScanner = new PackageScanner();
            Map<String, BeanDefinition> annotationBeanDefinitions = packageScanner.scan(basePackages);
            
            // 合并Bean定义，注解定义的Bean会覆盖配置文件中同名的Bean
            beanDefinitions.putAll(annotationBeanDefinitions);
        }
        
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