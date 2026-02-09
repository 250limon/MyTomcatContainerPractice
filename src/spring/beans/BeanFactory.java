package spring.beans;

import java.util.Map;

/**
 * Bean工厂接口，负责Bean的实例化和管理
 */
public interface BeanFactory {
    /**
     * 根据Bean ID获取Bean实例
     * @param beanId Bean ID
     * @return Bean实例
     */
    Object getBean(String beanId);
    
    /**
     * 获取所有Bean的ID
     * @return Bean ID数组
     */
    String[] getBeanDefinitionNames();
    
    /**
     * 获取指定类型的所有Bean实例
     * @param type Bean类型
     * @param <T> Bean类型
     * @return Bean实例映射
     */
    <T> Map<String, T> getBeansOfType(Class<T> type);
}