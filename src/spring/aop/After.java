package spring.aop;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 后置通知注解，在目标方法执行后执行，无论方法是否抛出异常
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface After {
    /**
     * 切点表达式
     * @return 切点表达式
     */
    String value();
}