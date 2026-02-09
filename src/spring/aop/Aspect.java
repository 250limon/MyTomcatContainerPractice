package spring.aop;

/**
 * 切面注解，用于标记切面类
 */
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Aspect {
    /**
     * 切点表达式
     * @return 切点表达式
     */
    String value();
}