package spring.xml;

import spring.beans.BeanDefinition;
import spring.core.io.Resource;
import spring.core.io.ResourceLoader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * XML Bean定义读取器
 */
public class XmlBeanDefinitionReader {
    private final ResourceLoader resourceLoader;
    
    public XmlBeanDefinitionReader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }
    
    /**
     * 加载Bean定义
     * @param configLocation 配置文件路径
     * @return Bean定义映射
     */
    public Map<String, BeanDefinition> loadBeanDefinitions(String configLocation) {
        Map<String, BeanDefinition> beanDefinitions = new HashMap<>();
        
        try {
            // 获取资源
            Resource resource = resourceLoader.getResource(configLocation);
            
            // 创建文档构建器
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            
            // 解析XML文档
            try (InputStream inputStream = resource.getInputStream()) {
                Document document = builder.parse(inputStream);
                
                // 处理根元素
                Element rootElement = document.getDocumentElement();
                
                // 解析所有bean元素
                NodeList beanNodeList = rootElement.getElementsByTagName("bean");
                
                for (int i = 0; i < beanNodeList.getLength(); i++) {
                    Node beanNode = beanNodeList.item(i);
                    if (beanNode instanceof Element) {
                        Element beanElement = (Element) beanNode;
                        // 解析Bean定义
                        BeanDefinition beanDefinition = parseBeanElement(beanElement);
                        // 将Bean定义添加到映射中（使用beanId作为键）
                        String beanId = beanElement.getAttribute("id");
                        beanDefinitions.put(beanId, beanDefinition);
                    }
                }
            }
            
        } catch (Exception e) {
            throw new RuntimeException("Error loading bean definitions from " + configLocation + ": " + e.getMessage(), e);
        }
        
        return beanDefinitions;
    }
    
    /**
     * 解析bean元素
     * @param beanElement bean元素
     * @return Bean定义
     */
    private BeanDefinition parseBeanElement(Element beanElement) {
        BeanDefinition beanDefinition = new BeanDefinition();
        
        // 解析id属性
        String beanId = beanElement.getAttribute("id");
        beanDefinition.setBeanClassName(beanId);
        
        // 解析class属性
        String className = beanElement.getAttribute("class");
        beanDefinition.setBeanClassName(className);
        
        // 解析scope属性，默认是singleton
        String scope = beanElement.getAttribute("scope");
        if ("prototype".equals(scope)) {
            beanDefinition.setSingleton(false);
        }
        
        // 解析lazy-init属性，默认是false
        String lazyInit = beanElement.getAttribute("lazy-init");
        if ("true".equals(lazyInit)) {
            beanDefinition.setLazyInit(true);
        }
        
        // 解析factory-method属性
        String factoryMethod = beanElement.getAttribute("factory-method");
        if (!factoryMethod.isEmpty()) {
            beanDefinition.setFactoryMethodName(factoryMethod);
        }
        
        // 解析构造函数参数
        List<BeanDefinition.ConstructorArg> constructorArgs = parseConstructorArgs(beanElement);
        if (!constructorArgs.isEmpty()) {
            beanDefinition.setConstructorArgs(constructorArgs);
        }
        
        // 解析属性
        Map<String, BeanDefinition.PropertyValue> propertyValues = parseProperties(beanElement);
        if (!propertyValues.isEmpty()) {
            beanDefinition.setPropertyValues(propertyValues);
        }
        
        return beanDefinition;
    }
    
    /**
     * 解析构造函数参数
     * @param beanElement bean元素
     * @return 构造函数参数列表
     */
    private List<BeanDefinition.ConstructorArg> parseConstructorArgs(Element beanElement) {
        List<BeanDefinition.ConstructorArg> constructorArgs = new ArrayList<>();
        
        NodeList constructorArgNodeList = beanElement.getElementsByTagName("constructor-arg");
        
        for (int i = 0; i < constructorArgNodeList.getLength(); i++) {
            Node argNode = constructorArgNodeList.item(i);
            if (argNode instanceof Element) {
                Element argElement = (Element) argNode;
                
                // 解析value或ref属性
                String value = argElement.getAttribute("value");
                String ref = argElement.getAttribute("ref");
                
                if (!value.isEmpty()) {
                    constructorArgs.add(new BeanDefinition.ConstructorArg(value, null, false));
                } else if (!ref.isEmpty()) {
                    constructorArgs.add(new BeanDefinition.ConstructorArg(null, ref, true));
                }
            }
        }
        
        return constructorArgs;
    }
    
    /**
     * 解析属性
     * @param beanElement bean元素
     * @return 属性值映射
     */
    private Map<String, BeanDefinition.PropertyValue> parseProperties(Element beanElement) {
        Map<String, BeanDefinition.PropertyValue> propertyValues = new HashMap<>();
        
        // 查找property子元素
        NodeList propertyNodeList = beanElement.getElementsByTagName("property");
        
        for (int i = 0; i < propertyNodeList.getLength(); i++) {
            Node propertyNode = propertyNodeList.item(i);
            if (propertyNode instanceof Element) {
                Element propertyElement = (Element) propertyNode;
                
                // 解析name属性
                String name = propertyElement.getAttribute("name");
                
                // 解析value或ref属性
                String value = propertyElement.getAttribute("value");
                String ref = propertyElement.getAttribute("ref");
                
                if (!value.isEmpty()) {
                    propertyValues.put(name, new BeanDefinition.PropertyValue(name, value, null, false));
                } else if (!ref.isEmpty()) {
                    propertyValues.put(name, new BeanDefinition.PropertyValue(name, null, ref, true));
                }
            }
        }
        
        return propertyValues;
    }
}