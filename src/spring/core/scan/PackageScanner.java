package spring.core.scan;

import spring.annotation.Component;
import spring.beans.BeanDefinition;
import spring.mvc.annotation.Controller;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 包扫描器，用于扫描指定包及其子包中的所有带有@Component注解的类
 */
public class PackageScanner {
    /**
     * 扫描指定包及其子包中的所有带有@Component注解的类
     * @param basePackages 要扫描的基础包列表
     * @return 扫描到的Bean定义映射
     */
    public Map<String, BeanDefinition> scan(String... basePackages) {
        Map<String, BeanDefinition> beanDefinitions = new HashMap<>();
        
        for (String basePackage : basePackages) {
            try {
                scanPackage(basePackage, beanDefinitions);
            } catch (Exception e) {
                throw new RuntimeException("Error scanning package: " + basePackage, e);
            }
        }
        
        return beanDefinitions;
    }
    
    /**
     * 扫描单个包
     * @param basePackage 要扫描的包名
     * @param beanDefinitions Bean定义映射
     * @throws IOException IO异常
     */
    private void scanPackage(String basePackage, Map<String, BeanDefinition> beanDefinitions) throws IOException {
        // 将包名转换为路径
        String packagePath = basePackage.replace('.', '/');
        
        // 获取类加载器
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        
        // 获取包下的所有资源
        Enumeration<URL> resources = classLoader.getResources(packagePath);
        
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            String filePath = resource.getFile();
            
            // 扫描目录下的所有类文件
            scanDirectory(new File(filePath), basePackage, beanDefinitions);
        }
    }
    
    /**
     * 扫描目录下的所有类文件
     * @param directory 目录
     * @param packageName 包名
     * @param beanDefinitions Bean定义映射
     */
    private void scanDirectory(File directory, String packageName, Map<String, BeanDefinition> beanDefinitions) {
        // 检查目录是否存在
        if (!directory.exists() || !directory.isDirectory()) {
            return;
        }
        
        // 获取目录下的所有文件
        File[] files = directory.listFiles();
        if (files == null) {
            return;
        }
        
        for (File file : files) {
            if (file.isDirectory()) {
                // 递归扫描子目录
                scanDirectory(file, packageName + "." + file.getName(), beanDefinitions);
            } else if (file.getName().endsWith(".class")) {
                // 处理类文件
                String className = packageName + "." + file.getName().replace(".class", "");
                processClass(className, beanDefinitions);
            }
        }
    }
    
    /**
     * 处理类文件，检查是否带有@Component注解
     * @param className 类名
     * @param beanDefinitions Bean定义映射
     */
    private void processClass(String className, Map<String, BeanDefinition> beanDefinitions) {
        try {
            // 加载类
            Class<?> clazz = Class.forName(className);
            
            // 检查类是否带有@Component注解
            Component componentAnnotation = clazz.getAnnotation(Component.class);
            Controller controllerAnnotation = clazz.getAnnotation(Controller.class);
            if (componentAnnotation != null || controllerAnnotation != null) {
                // 创建Bean定义
                BeanDefinition beanDefinition = new BeanDefinition();
                beanDefinition.setBeanClassName(className);
                
                // 获取Bean ID
                String beanId = componentAnnotation != null ? componentAnnotation.value() : controllerAnnotation.value();
                if (beanId.isEmpty()) {
                    // 如果没有指定Bean ID，使用类名的首字母小写作为Bean ID
                    beanId = toCamelCase(clazz.getSimpleName());
                }
                
                // 处理构造函数注入（@Autowired注解）
                processConstructorInjection(clazz, beanDefinition);
                
                // 处理设值注入（@Autowired注解）
                processFieldInjection(clazz, beanDefinition);
                
                // 添加到Bean定义映射中
                beanDefinitions.put(beanId, beanDefinition);
                
                System.out.println("Found component: " + beanId + " -> " + className);
            }
            
        } catch (ClassNotFoundException e) {
            System.err.println("Error loading class: " + className);
        }
    }
    
    /**
     * 处理构造函数注入（@Autowired注解）
     * @param clazz 类
     * @param beanDefinition Bean定义
     */
    private void processConstructorInjection(Class<?> clazz, BeanDefinition beanDefinition) {
        // 获取所有构造函数
        java.lang.reflect.Constructor<?>[] constructors = clazz.getDeclaredConstructors();
        
        for (java.lang.reflect.Constructor<?> constructor : constructors) {
            // 检查构造函数是否带有@Autowired注解
            spring.annotation.Autowired autowiredAnnotation = constructor.getAnnotation(spring.annotation.Autowired.class);
            if (autowiredAnnotation != null) {
                // 获取构造函数参数
                java.lang.reflect.Parameter[] parameters = constructor.getParameters();
                List<BeanDefinition.ConstructorArg> constructorArgs = new ArrayList<>();
                
                for (java.lang.reflect.Parameter parameter : parameters) {
                    // 检查参数是否带有@Qualifier注解
                    spring.annotation.Qualifier qualifierAnnotation = parameter.getAnnotation(spring.annotation.Qualifier.class);
                    
                    BeanDefinition.ConstructorArg constructorArg;
                    if (qualifierAnnotation != null) {
                        // 如果带有@Qualifier注解，使用指定的Bean ID
                        constructorArg = new BeanDefinition.ConstructorArg(null, qualifierAnnotation.value(), true);
                    } else {
                        // 如果没有@Qualifier注解，使用参数类型的首字母小写作为Bean ID
                        String paramName = parameter.getName();
                        constructorArg = new BeanDefinition.ConstructorArg(null, paramName, true);
                    }
                    
                    constructorArgs.add(constructorArg);
                }
                
                // 设置构造函数参数
                beanDefinition.setConstructorArgs(constructorArgs);
                
                System.out.println("Found autowired constructor: " + constructor);
                break; // 只处理第一个带有@Autowired注解的构造函数
            }
        }
    }
    
    /**
     * 处理设值注入（@Autowired注解）
     * @param clazz 类
     * @param beanDefinition Bean定义
     */
    private void processFieldInjection(Class<?> clazz, BeanDefinition beanDefinition) {
        // 获取所有字段
        Map<String, BeanDefinition.PropertyValue> propertyValues = new HashMap<>();
        
        // 获取所有字段，包括父类的字段
        Class<?> currentClass = clazz;
        while (currentClass != null && currentClass != Object.class) {
            java.lang.reflect.Field[] fields = currentClass.getDeclaredFields();
            for (java.lang.reflect.Field field : fields) {
                // 检查字段是否带有@Autowired注解
                spring.annotation.Autowired autowiredAnnotation = field.getAnnotation(spring.annotation.Autowired.class);
                if (autowiredAnnotation != null) {
                    // 检查字段是否带有@Qualifier注解
                    spring.annotation.Qualifier qualifierAnnotation = field.getAnnotation(spring.annotation.Qualifier.class);
                    
                    BeanDefinition.PropertyValue propertyValue;
                    if (qualifierAnnotation != null) {
                        // 如果带有@Qualifier注解，使用指定的Bean ID
                        propertyValue = new BeanDefinition.PropertyValue(field.getName(), null, qualifierAnnotation.value(), true);
                    } else {
                        // 如果没有@Qualifier注解，使用字段名作为Bean ID
                        propertyValue = new BeanDefinition.PropertyValue(field.getName(), null, field.getName(), true);
                    }
                    
                    propertyValues.put(field.getName(), propertyValue);
                    
                    System.out.println("Found autowired field: " + field.getName());
                }
            }
            
            // 处理父类的字段
            currentClass = currentClass.getSuperclass();
        }
        
        // 设置属性值
        if (!propertyValues.isEmpty()) {
            beanDefinition.setPropertyValues(propertyValues);
        }
    }
    
    /**
     * 将类名转换为驼峰命名法（首字母小写）
     * @param className 类名
     * @return 驼峰命名法的类名
     */
    private String toCamelCase(String className) {
        if (className == null || className.isEmpty()) {
            return className;
        }
        
        return className.substring(0, 1).toLowerCase() + className.substring(1);
    }
}