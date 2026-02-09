package spring.aop;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

/**
 * 代理工厂，用于创建AOP代理对象
 */
public class ProxyFactory {
    private Object target;
    private final List<Advisor> advisors = new ArrayList<>();
    
    public ProxyFactory() {
    }
    
    public ProxyFactory(Object target) {
        this.target = target;
    }
    
    /**
     * 设置目标对象
     * @param target 目标对象
     */
    public void setTarget(Object target) {
        this.target = target;
    }
    
    /**
     * 添加通知器
     * @param advisor 通知器
     */
    public void addAdvisor(Advisor advisor) {
        advisors.add(advisor);
    }
    
    /**
     * 创建代理对象
     * @param <T> 代理对象类型
     * @return 代理对象
     */
    @SuppressWarnings("unchecked")
    public <T> T getProxy() {
        if (target == null) {
            throw new IllegalStateException("Target object must be set before creating proxy");
        }
        
        // 如果没有通知器，直接返回目标对象
        if (advisors.isEmpty()) {
            return (T) target;
        }
        
        // 获取目标对象的所有接口
        Class<?>[] interfaces = target.getClass().getInterfaces();
        
        // 如果目标对象没有实现任何接口，直接返回目标对象
        if (interfaces.length == 0) {
            return (T) target;
        }
        
        // 创建代理对象
        return (T) Proxy.newProxyInstance(
                target.getClass().getClassLoader(),
                interfaces,
                new AopInvocationHandler(target, advisors)
        );
    }
    
    /**
     * AOP调用处理器
     */
    private static class AopInvocationHandler implements InvocationHandler {
        private final Object target;
        private final List<Advisor> advisors;
        
        public AopInvocationHandler(Object target, List<Advisor> advisors) {
            this.target = target;
            this.advisors = advisors;
        }
        
        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            // 创建连接点
            JoinPoint joinPoint = new JoinPoint(target, method, args);
            
            // 匹配的通知器
            List<Advisor> matchedAdvisors = new ArrayList<>();
            for (Advisor advisor : advisors) {
                if (advisor.getPointcut().matches(method, target.getClass())) {
                    matchedAdvisors.add(advisor);
                }
            }
            
            // 如果没有匹配的通知器，直接执行目标方法
            if (matchedAdvisors.isEmpty()) {
                return method.invoke(target, args);
            }
            
            // 创建通知链
            AdviceChain adviceChain = new AdviceChain(target, method, args, matchedAdvisors);
            
            // 执行通知链
            return adviceChain.proceed(joinPoint);
        }
    }
    
    /**
     * 通知链
     */
    static class AdviceChain {
        private final Object target;
        private final Method method;
        private final Object[] args;
        private final List<Advisor> advisors;
        private int currentIndex = 0;
        
        public AdviceChain(Object target, Method method, Object[] args, List<Advisor> advisors) {
            this.target = target;
            this.method = method;
            this.args = args;
            this.advisors = advisors;
        }
        
        public Object proceed(JoinPoint joinPoint) throws Exception {
            // 如果所有通知都执行完了，执行目标方法
            if (currentIndex == advisors.size()) {
                return joinPoint.proceed();
            }
            
            // 获取当前通知器
            Advisor advisor = advisors.get(currentIndex++);
            Advice advice = advisor.getAdvice();
            
            // 执行前置通知
            advice.before(joinPoint);
            
            Object result = null;
            Throwable ex = null;
            
            try {
                // 执行环绕通知或继续执行通知链
                result = advice.around(this, joinPoint);
            } catch (Throwable e) {
                ex = e;
                throw e;
            } finally {
                // 执行后置通知
                advice.after(joinPoint, result, ex);
            }
            
            return result;
        }
    }
}