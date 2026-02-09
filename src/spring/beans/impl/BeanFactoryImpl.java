package spring.beans.impl;

import spring.beans.BeanDefinition;
import spring.beans.BeanFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Bean工厂的具体实现类
 */
public class BeanFactoryImpl implements BeanFactory {
    private final Map<String, BeanDefinition> beanDefinitions;
    private final Map<String, Object> singletonBeans;
    
    public BeanFactoryImpl(Map<String, BeanDefinition> beanDefinitions) {
        this.beanDefinitions = beanDefinitions;
        this.singletonBeans = new ConcurrentHashMap<>();
        
        // 预初始化非延迟加载的单例Bean
        preInstantiateSingletons();
    }
    
    @Override
    public Object getBean(String beanId) {
        // 检查Bean ID是否存在
        if (!beanDefinitions.containsKey(beanId)) {
            throw new RuntimeException("No bean named '" + beanId + "' is defined");
        }
        
        // 获取Bean定义
        BeanDefinition beanDefinition = beanDefinitions.get(beanId);
        
        // 如果是单例且已实例化，直接返回
        if (beanDefinition.isSingleton() && beanDefinition.getBeanInstance() != null) {
            return beanDefinition.getBeanInstance();
        }
        
        // 创建Bean实例
        Object bean = createBean(beanDefinition);
        
        // 如果是单例，缓存实例
        if (beanDefinition.isSingleton()) {
            beanDefinition.setBeanInstance(bean);
            singletonBeans.put(beanId, bean);
        }
        
        return bean;
    }
    
    @Override
    public String[] getBeanDefinitionNames() {
        return beanDefinitions.keySet().toArray(new String[0]);
    }
    
    @Override
    public <T> Map<String, T> getBeansOfType(Class<T> type) {
        Map<String, T> result = new HashMap<>();
        
        for (String beanId : beanDefinitions.keySet()) {
            Object bean = getBean(beanId);
            if (type.isInstance(bean)) {
                @SuppressWarnings("unchecked")
                T typedBean = (T) bean;
                result.put(beanId, typedBean);
            }
        }
        
        return result;
    }
    
    /**
     * 预初始化非延迟加载的单例Bean
     */
    private void preInstantiateSingletons() {
        for (String beanId : beanDefinitions.keySet()) {
            BeanDefinition beanDefinition = beanDefinitions.get(beanId);
            if (beanDefinition.isSingleton() && !beanDefinition.isLazyInit()) {
                getBean(beanId);
            }
        }
    }
    
    /**
     * 创建Bean实例
     * @param beanDefinition Bean定义
     * @return Bean实例
     */
    private Object createBean(BeanDefinition beanDefinition) {
        try {
            // 加载Bean类
            Class<?> beanClass = Class.forName(beanDefinition.getBeanClassName());
            
            // 处理工厂方法
            if (beanDefinition.getFactoryMethodName() != null) {
                return createBeanByFactoryMethod(beanClass, beanDefinition);
            }
            
            // 获取构造函数参数
            List<BeanDefinition.ConstructorArg> constructorArgs = beanDefinition.getConstructorArgs();
            Object[] args = null;
            
            if (constructorArgs != null && !constructorArgs.isEmpty()) {
                args = resolveConstructorArgs(constructorArgs);
            }
            
            // 实例化Bean
            Object bean;
            if (args == null || args.length == 0) {
                bean = beanClass.newInstance();
            } else {
                // 寻找匹配的构造函数
                Constructor<?> constructor = findMatchingConstructor(beanClass, args);
                bean = constructor.newInstance(args);
            }
            
            // 属性注入
            injectProperties(bean, beanDefinition.getPropertyValues());
            
            return bean;
            
        } catch (Exception e) {
            throw new RuntimeException("Error creating bean: " + e.getMessage(), e);
        }
    }
    
    /**
     * 通过工厂方法创建Bean
     * @param beanClass Bean类
     * @param beanDefinition Bean定义
     * @return Bean实例
     */
    private Object createBeanByFactoryMethod(Class<?> beanClass, BeanDefinition beanDefinition) 
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        
        // 处理静态工厂方法
        return beanClass.getMethod(beanDefinition.getFactoryMethodName()).invoke(null);
    }
    
    /**
     * 解析构造函数参数
     * @param constructorArgs 构造函数参数列表
     * @return 解析后的参数值数组
     */
    private Object[] resolveConstructorArgs(List<BeanDefinition.ConstructorArg> constructorArgs) {
        Object[] args = new Object[constructorArgs.size()];
        
        for (int i = 0; i < constructorArgs.size(); i++) {
            BeanDefinition.ConstructorArg arg = constructorArgs.get(i);
            if (arg.isRef()) {
                // 处理引用类型参数
                args[i] = getBean(arg.getRef());
            } else {
                // 处理值类型参数
                args[i] = arg.getValue();
            }
        }
        
        return args;
    }
    
    /**
     * 寻找匹配的构造函数
     * @param beanClass Bean类
     * @param args 构造函数参数
     * @return 匹配的构造函数
     */
    private Constructor<?> findMatchingConstructor(Class<?> beanClass, Object[] args) 
            throws NoSuchMethodException {
        
        Constructor<?>[] constructors = beanClass.getConstructors();
        
        for (Constructor<?> constructor : constructors) {
            Class<?>[] parameterTypes = constructor.getParameterTypes();
            if (parameterTypes.length == args.length) {
                boolean match = true;
                for (int i = 0; i < parameterTypes.length; i++) {
                    if (args[i] != null && !parameterTypes[i].isAssignableFrom(args[i].getClass())) {
                        match = false;
                        break;
                    }
                }
                if (match) {
                    return constructor;
                }
            }
        }
        
        throw new NoSuchMethodException("No matching constructor found for arguments: " + args.length);
    }
    
    /**
     * 属性注入
     * @param bean Bean实例
     * @param propertyValues 属性值映射
     */
    private void injectProperties(Object bean, Map<String, BeanDefinition.PropertyValue> propertyValues) 
            throws IllegalAccessException {
        
        if (propertyValues == null || propertyValues.isEmpty()) {
            return;
        }
        
        Class<?> beanClass = bean.getClass();
        
        for (Map.Entry<String, BeanDefinition.PropertyValue> entry : propertyValues.entrySet()) {
            String propertyName = entry.getKey();
            BeanDefinition.PropertyValue propertyValue = entry.getValue();
            
            try {
                // 获取字段
                Field field = beanClass.getDeclaredField(propertyName);
                field.setAccessible(true);
                
                // 注入属性值
                if (propertyValue.isRef()) {
                    // 处理引用类型属性
                    Object refBean = getBean(propertyValue.getRef());
                    field.set(bean, refBean);
                } else {
                    // 处理值类型属性
                    field.set(bean, propertyValue.getValue());
                }
                
            } catch (NoSuchFieldException e) {
                // 尝试通过setter方法注入
                try {
                    String setterName = "set" + Character.toUpperCase(propertyName.charAt(0)) + propertyName.substring(1);
                    
                    if (propertyValue.isRef()) {
                        Object refBean = getBean(propertyValue.getRef());
                        beanClass.getMethod(setterName, refBean.getClass()).invoke(bean, refBean);
                    } else {
                        beanClass.getMethod(setterName, propertyValue.getValue().getClass()).invoke(bean, propertyValue.getValue());
                    }
                    
                } catch (Exception ex) {
                    // 如果既没有字段也没有setter方法，忽略该属性
                    System.err.println("Warning: Cannot inject property '" + propertyName + "' into bean '" + bean.getClass().getName() + "'");
                }
            }
        }
    }
}