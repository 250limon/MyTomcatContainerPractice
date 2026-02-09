package spring.beans;

import java.util.List;
import java.util.Map;

/**
 * Bean定义类，存储Bean的元信息
 */
public class BeanDefinition {
    private String beanClassName;
    private boolean singleton = true;
    private boolean lazyInit = false;
    private String factoryBeanName;
    private String factoryMethodName;
    private List<ConstructorArg> constructorArgs;
    private Map<String, PropertyValue> propertyValues;
    private Object beanInstance;
    
    public String getBeanClassName() {
        return beanClassName;
    }
    
    public void setBeanClassName(String beanClassName) {
        this.beanClassName = beanClassName;
    }
    
    public boolean isSingleton() {
        return singleton;
    }
    
    public void setSingleton(boolean singleton) {
        this.singleton = singleton;
    }
    
    public boolean isLazyInit() {
        return lazyInit;
    }
    
    public void setLazyInit(boolean lazyInit) {
        this.lazyInit = lazyInit;
    }
    
    public String getFactoryBeanName() {
        return factoryBeanName;
    }
    
    public void setFactoryBeanName(String factoryBeanName) {
        this.factoryBeanName = factoryBeanName;
    }
    
    public String getFactoryMethodName() {
        return factoryMethodName;
    }
    
    public void setFactoryMethodName(String factoryMethodName) {
        this.factoryMethodName = factoryMethodName;
    }
    
    public List<ConstructorArg> getConstructorArgs() {
        return constructorArgs;
    }
    
    public void setConstructorArgs(List<ConstructorArg> constructorArgs) {
        this.constructorArgs = constructorArgs;
    }
    
    public Map<String, PropertyValue> getPropertyValues() {
        return propertyValues;
    }
    
    public void setPropertyValues(Map<String, PropertyValue> propertyValues) {
        this.propertyValues = propertyValues;
    }
    
    public Object getBeanInstance() {
        return beanInstance;
    }
    
    public void setBeanInstance(Object beanInstance) {
        this.beanInstance = beanInstance;
    }
    
    /**
     * 构造函数参数类
     */
    public static class ConstructorArg {
        private Object value;
        private String ref;
        private boolean isRef;
        
        public ConstructorArg(Object value, String ref, boolean isRef) {
            this.value = value;
            this.ref = ref;
            this.isRef = isRef;
        }
        
        public Object getValue() {
            return value;
        }
        
        public String getRef() {
            return ref;
        }
        
        public boolean isRef() {
            return isRef;
        }
    }
    
    /**
     * 属性值类
     */
    public static class PropertyValue {
        private String name;
        private Object value;
        private String ref;
        private boolean isRef;
        
        public PropertyValue(String name, Object value, String ref, boolean isRef) {
            this.name = name;
            this.value = value;
            this.ref = ref;
            this.isRef = isRef;
        }
        
        public String getName() {
            return name;
        }
        
        public Object getValue() {
            return value;
        }
        
        public String getRef() {
            return ref;
        }
        
        public boolean isRef() {
            return isRef;
        }
    }
}