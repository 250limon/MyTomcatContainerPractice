package spring.aop;

import java.lang.reflect.Method;

/**
 * 通知接口，代表横切关注点的实现
 */
public interface Advice {
    /**
     * 在目标方法执行前执行
     * @param joinPoint 连接点
     */
    default void before(JoinPoint joinPoint) {
        // 默认实现为空
    }
    
    /**
     * 在目标方法执行后执行，无论方法是否抛出异常
     * @param joinPoint 连接点
     * @param result 方法返回值
     * @param ex 方法抛出的异常（如果有）
     */
    default void after(JoinPoint joinPoint, Object result, Throwable ex) {
        // 默认实现为空
    }
    
    /**
     * 环绕通知，在目标方法执行前后都执行，可以控制目标方法的执行
     * @param adviceChain 通知链
     * @param joinPoint 连接点
     * @return 方法返回值
     * @throws Exception 执行过程中可能抛出的异常
     */
    default Object around(ProxyFactory.AdviceChain adviceChain, JoinPoint joinPoint) throws Exception {
        // 默认实现：执行目标方法
        return adviceChain.proceed(joinPoint);
    }
}