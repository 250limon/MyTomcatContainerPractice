package spring.aop;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 环绕通知注解，在目标方法执行前后都执行，可以控制目标方法的执行
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Around {
    /**
     * 切点表达式
     * @return 切点表达式
     */
    String value();
}