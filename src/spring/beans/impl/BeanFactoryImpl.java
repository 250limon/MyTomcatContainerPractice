package spring.beans.impl;

import spring.annotation.Autowired;
import spring.annotation.Qualifier;
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
            throws IllegalAccessException, InvocationTargetException, InstantiationException {
        
        Class<?> beanClass = bean.getClass();
        
        // 处理所有属性注入（来自PackageScanner和XmlBeanDefinitionReader）
        if (propertyValues != null && !propertyValues.isEmpty()) {
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
                        System.out.println("Autowired field: " + propertyName + " -> " + refBean.getClass().getName());
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
                            System.out.println("Autowired field: " + propertyName + " -> " + refBean.getClass().getName());
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
    
    /**
     * 处理基于注解的字段注入
     * @param bean Bean实例
     * @param beanClass Bean类
     */
    private void injectAnnotatedFields(Object bean, Class<?> beanClass) throws IllegalAccessException {
        // 获取所有字段，包括父类的字段
        while (beanClass != null && beanClass != Object.class) {
            Field[] fields = beanClass.getDeclaredFields();
            for (Field field : fields) {
                // 检查字段是否带有@Autowired注解
                Autowired autowiredAnnotation = field.getAnnotation(Autowired.class);
                if (autowiredAnnotation != null) {
                    field.setAccessible(true);
                    
                    // 获取要注入的Bean实例
                    Object beanToInject = getBeanForField(field);
                    
                    if (beanToInject != null) {
                        // 注入Bean实例
                        field.set(bean, beanToInject);
                        System.out.println("Autowired field: " + field.getName() + " -> " + beanToInject.getClass().getName());
                    } else if (autowiredAnnotation.required()) {
                        // 如果是必须注入的，但没有找到对应的Bean，抛出异常
                        throw new RuntimeException("Cannot find bean for field: " + field.getName());
                    }
                }
            }
            
            // 处理父类的字段
            beanClass = beanClass.getSuperclass();
        }
    }
    
    /**
     * 获取要注入到字段中的Bean实例
     * @param field 字段
     * @return Bean实例
     */
    @SuppressWarnings("unchecked")
    private Object getBeanForField(Field field) {
        // 检查字段是否带有@Qualifier注解
        Qualifier qualifierAnnotation = field.getAnnotation(Qualifier.class);
        if (qualifierAnnotation != null) {
            // 如果带有@Qualifier注解，使用指定的Bean ID
            String beanId = qualifierAnnotation.value();
            return getBean(beanId);
        } else {
            // 如果没有@Qualifier注解，根据类型查找Bean
            Class<?> fieldType = field.getType();
            Map<String, ?> beansOfType = getBeansOfType(fieldType);
            
            if (beansOfType.size() == 1) {
                // 如果只有一个匹配的Bean，直接返回
                return beansOfType.values().iterator().next();
            } else if (beansOfType.size() > 1) {
                // 如果有多个匹配的Bean，尝试根据字段名查找
                String fieldName = field.getName();
                if (beansOfType.containsKey(fieldName)) {
                    return beansOfType.get(fieldName);
                } else {
                    // 如果没有找到匹配的Bean，抛出异常
                    throw new RuntimeException("Multiple beans found for field: " + fieldName + ", please use @Qualifier to specify which one to inject");
                }
            }
        }
        
        return null;
    }
}