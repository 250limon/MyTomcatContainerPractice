package spring.mvc.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 路径变量注解，用于将URL路径参数绑定到方法参数
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface PathVariable {
    /**
     * 路径变量名称
     * @return 路径变量名称
     */
    String value() default "";
}