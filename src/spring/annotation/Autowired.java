package spring.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自动注入注解，用于标记需要自动注入的字段或构造函数
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.CONSTRUCTOR})
public @interface Autowired {
    /**
     * 是否必须注入
     * @return 是否必须注入
     */
    boolean required() default true;
}