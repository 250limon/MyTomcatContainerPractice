package spring.mvc.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 请求参数注解，用于将请求参数绑定到方法参数
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface RequestParam {
    /**
     * 参数名称
     * @return 参数名称
     */
    String value() default "";
    
    /**
     * 是否必须
     * @return 是否必须
     */
    boolean required() default true;
    
    /**
     * 默认值
     * @return 默认值
     */
    String defaultValue() default "";
}