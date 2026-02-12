package spring.mvc;

import spring.mvc.annotation.PathVariable;
import spring.mvc.annotation.RequestBody;
import spring.mvc.annotation.RequestParam;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 处理器适配器，用于调用处理器方法并处理参数解析
 */
public class HandlerAdapter {
    /**
     * 调用处理器方法
     * @param handlerMethod 处理器方法信息
     * @param requestUrl 请求URL
     * @param requestParameters 请求参数
     * @param requestBody 请求体
     * @return 方法返回值
     * @throws Exception 调用过程中可能抛出的异常
     */
    public Object handle(HandlerMapping.HandlerMethod handlerMethod, String requestUrl, Map<String, String> requestParameters, String requestBody) throws Exception {
        // 获取处理器方法
        Method method = handlerMethod.getMethod();
        
        // 获取方法参数
        Parameter[] parameters = method.getParameters();
        Object[] args = new Object[parameters.length];
        
        // 解析参数
        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            Class<?> parameterType = parameter.getType();
            
            // 处理@PathVariable注解
            PathVariable pathVariable = parameter.getAnnotation(PathVariable.class);
            if (pathVariable != null) {
                args[i] = resolvePathVariable(parameter, parameterType, pathVariable, handlerMethod, requestUrl);
                continue;
            }
            
            // 处理@RequestParam注解
            RequestParam requestParam = parameter.getAnnotation(RequestParam.class);
            if (requestParam != null) {
                args[i] = resolveRequestParam(parameter, parameterType, requestParam, requestParameters);
                continue;
            }
            
            // 处理@RequestBody注解
            RequestBody requestBodyAnnotation = parameter.getAnnotation(RequestBody.class);
            if (requestBodyAnnotation != null) {
                args[i] = resolveRequestBody(parameter, parameterType, requestBodyAnnotation, requestBody);
                continue;
            }
            
            // 如果没有任何注解，尝试根据参数名称从请求参数中获取
            String paramName = parameter.getName();
            String paramValue = requestParameters.get(paramName);
            if (paramValue != null) {
                args[i] = convertParameterValue(parameterType, paramValue);
            } else {
                // 如果参数类型是基本类型，使用默认值；如果是引用类型，使用null
                args[i] = getDefaultValue(parameterType);
            }
        }
        
        // 调用处理器方法
        return method.invoke(handlerMethod.getBeanInstance(), args);
    }
    
    /**
     * 解析路径变量参数
     * @param parameter 参数
     * @param parameterType 参数类型
     * @param pathVariable PathVariable注解
     * @param handlerMethod 处理器方法信息
     * @param requestUrl 请求URL
     * @return 解析后的参数值
     */
    private Object resolvePathVariable(Parameter parameter, Class<?> parameterType, PathVariable pathVariable, HandlerMapping.HandlerMethod handlerMethod, String requestUrl) {
        String paramName = pathVariable.value();
        if (paramName.isEmpty()) {
            paramName = parameter.getName();
        }
        
        // 获取处理器方法的URL模板
        String urlTemplate = handlerMethod.getRequestMapping().value();
        
        // 将URL模板转换为正则表达式
        String regexUrl = convertToRegex(urlTemplate);
        Pattern pattern = Pattern.compile(regexUrl);
        Matcher matcher = pattern.matcher(requestUrl);
        
        if (matcher.matches()) {
            // 查找路径变量的索引
            int paramIndex = findPathVariableIndex(urlTemplate, paramName);
            if (paramIndex >= 0 && paramIndex < matcher.groupCount()) {
                String paramValue = matcher.group(paramIndex + 1); // 正则表达式组从1开始
                return convertParameterValue(parameterType, paramValue);
            }
        }
        
        // 如果没有找到，返回默认值
        return getDefaultValue(parameterType);
    }
    
    /**
     * 解析请求参数
     * @param parameter 参数
     * @param parameterType 参数类型
     * @param requestParam RequestParam注解
     * @param requestParameters 请求参数
     * @return 解析后的参数值
     */
    private Object resolveRequestParam(Parameter parameter, Class<?> parameterType, RequestParam requestParam, Map<String, String> requestParameters) {
        String paramName = requestParam.value();
        if (paramName.isEmpty()) {
            paramName = parameter.getName();
        }
        
        String paramValue = requestParameters.get(paramName);
        if (paramValue != null) {
            return convertParameterValue(parameterType, paramValue);
        } else if (requestParam.required()) {
            // 如果参数是必须的，但没有提供，抛出异常
            throw new RuntimeException("Required parameter '" + paramName + "' is not present");
        } else if (!requestParam.defaultValue().isEmpty()) {
            // 如果提供了默认值，使用默认值
            return convertParameterValue(parameterType, requestParam.defaultValue());
        } else {
            // 返回默认值
            return getDefaultValue(parameterType);
        }
    }
    
    /**
     * 解析请求体
     * @param parameter 参数
     * @param parameterType 参数类型
     * @param requestBodyAnnotation RequestBody注解
     * @param requestBody 请求体
     * @return 解析后的参数值
     */
    private Object resolveRequestBody(Parameter parameter, Class<?> parameterType, RequestBody requestBodyAnnotation, String requestBody) {
        if (requestBody == null || requestBody.isEmpty()) {
            if (requestBodyAnnotation.required()) {
                throw new RuntimeException("Required request body is not present");
            } else {
                return null;
            }
        }
        
        // 简单实现：如果参数类型是String，直接返回；否则尝试转换为JSON对象
        if (parameterType == String.class) {
            return requestBody;
        } else {
            // 这里可以添加JSON解析逻辑，例如使用Jackson或Gson
            System.err.println("Warning: RequestBody conversion for type " + parameterType.getName() + " is not implemented");
            return null;
        }
    }
    
    /**
     * 将URL转换为正则表达式（用于处理路径变量）
     * @param url URL
     * @return 正则表达式
     */
    private String convertToRegex(String url) {
        // 将{paramName}转换为正则表达式组
        return url.replaceAll("\\{([^}]+)\\}", "([^/]+)");
    }
    
    /**
     * 查找路径变量在URL模板中的索引
     * @param urlTemplate URL模板
     * @param paramName 参数名称
     * @return 参数索引
     */
    private int findPathVariableIndex(String urlTemplate, String paramName) {
        String[] parts = urlTemplate.split("/");
        int index = 0;
        for (String part : parts) {
            if (part.startsWith("{") && part.endsWith("}")) {
                String name = part.substring(1, part.length() - 1);
                if (name.equals(paramName)) {
                    return index;
                }
                index++;
            }
        }
        return -1;
    }
    
    /**
     * 转换参数值为指定类型
     * @param parameterType 参数类型
     * @param paramValue 参数值字符串
     * @return 转换后的参数值
     */
    private Object convertParameterValue(Class<?> parameterType, String paramValue) {
        if (parameterType == String.class) {
            return paramValue;
        } else if (parameterType == int.class || parameterType == Integer.class) {
            return Integer.parseInt(paramValue);
        } else if (parameterType == long.class || parameterType == Long.class) {
            return Long.parseLong(paramValue);
        } else if (parameterType == double.class || parameterType == Double.class) {
            return Double.parseDouble(paramValue);
        } else if (parameterType == boolean.class || parameterType == Boolean.class) {
            return Boolean.parseBoolean(paramValue);
        } else if (parameterType == float.class || parameterType == Float.class) {
            return Float.parseFloat(paramValue);
        } else if (parameterType == short.class || parameterType == Short.class) {
            return Short.parseShort(paramValue);
        } else if (parameterType == byte.class || parameterType == Byte.class) {
            return Byte.parseByte(paramValue);
        } else if (parameterType == char.class || parameterType == Character.class) {
            return paramValue.charAt(0);
        } else {
            // 如果是其他类型，尝试通过构造函数转换
            try {
                return parameterType.getConstructor(String.class).newInstance(paramValue);
            } catch (Exception e) {
                System.err.println("Warning: Cannot convert parameter value '" + paramValue + "' to type " + parameterType.getName());
                return null;
            }
        }
    }
    
    /**
     * 获取参数类型的默认值
     * @param parameterType 参数类型
     * @return 默认值
     */
    private Object getDefaultValue(Class<?> parameterType) {
        if (parameterType.isPrimitive()) {
            if (parameterType == boolean.class) {
                return false;
            } else {
                return 0;
            }
        } else {
            return null;
        }
    }
}