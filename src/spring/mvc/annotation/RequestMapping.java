package spring.mvc.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 请求映射注解，用于将请求URL映射到控制器方法
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface RequestMapping {
    /**
     * 请求URL
     * @return 请求URL
     */
    String value() default "";
    
    /**
     * 请求方法 (GET, POST, PUT, DELETE等)
     * @return 请求方法数组
     */
    String[] method() default {};
    
    /**
     * 请求参数
     * @return 请求参数数组
     */
    String[] params() default {};
    
    /**
     * 请求头
     * @return 请求头数组
     */
    String[] headers() default {};
    
    /**
     * 请求内容类型
     * @return 请求内容类型数组
     */
    String[] consumes() default {};
    
    /**
     * 响应内容类型
     * @return 响应内容类型数组
     */
    String[] produces() default {};
}