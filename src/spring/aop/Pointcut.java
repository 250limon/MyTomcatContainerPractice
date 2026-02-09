package spring.aop;

import java.lang.reflect.Method;

/**
 * 切入点接口，用于匹配连接点
 */
public interface Pointcut {
    /**
     * 检查方法是否匹配切入点
     * @param method 方法
     * @param targetClass 目标类
     * @return 是否匹配
     */
    boolean matches(Method method, Class<?> targetClass);
    
    /**
     * 从表达式创建切入点
     * @param expression 切入点表达式
     * @return 切入点
     */
    static Pointcut fromExpression(String expression) {
        return new SimplePointcut(expression);
    }
    
    /**
     * 简单切入点实现
     */
    class SimplePointcut implements Pointcut {
        private final String expression;
        
        public SimplePointcut(String expression) {
            this.expression = expression;
        }
        
        @Override
        public boolean matches(Method method, Class<?> targetClass) {
            // 简单的匹配逻辑：检查类名或方法名是否包含表达式中的关键字
            // 实际应用中应该使用正则表达式或更复杂的匹配逻辑
            String className = targetClass.getName();
            String methodName = method.getName();
            
            return className.contains(expression) || methodName.contains(expression);
        }
    }
}