package spring.aop;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * 连接点接口，用于表示程序执行过程中的连接点
 */
public class JoinPoint {
    private final Object target;
    private final Method method;
    private final Object[] args;
    private final Class<?> targetClass;
    
    public JoinPoint(Object target, Method method, Object[] args) {
        this.target = target;
        this.method = method;
        this.args = args;
        this.targetClass = target.getClass();
    }
    
    /**
     * 获取目标对象
     * @return 目标对象
     */
    public Object getTarget() {
        return target;
    }
    
    /**
     * 获取目标方法
     * @return 目标方法
     */
    public Method getMethod() {
        return method;
    }
    
    /**
     * 获取方法参数
     * @return 方法参数
     */
    public Object[] getArgs() {
        return args;
    }
    
    /**
     * 获取目标类
     * @return 目标类
     */
    public Class<?> getTargetClass() {
        return targetClass;
    }
    
    /**
     * 执行目标方法
     * @return 方法返回值
     * @throws Exception 执行过程中可能抛出的异常
     */
    public Object proceed() throws Exception {
        method.setAccessible(true);
        return method.invoke(target, args);
    }
    
    @Override
    public String toString() {
        return "JoinPoint{target=" + target + ", method=" + method + ", args=" + Arrays.toString(args) + "}";
    }
}