package spring.aop;

/**
 * 通知器接口，将切入点和通知组合在一起
 */
public interface Advisor {
    /**
     * 获取切入点
     * @return 切入点
     */
    Pointcut getPointcut();
    
    /**
     * 获取通知
     * @return 通知
     */
    Advice getAdvice();
    
    /**
     * 创建简单的通知器
     * @param pointcut 切入点
     * @param advice 通知
     * @return 通知器
     */
    static Advisor of(Pointcut pointcut, Advice advice) {
        return new SimpleAdvisor(pointcut, advice);
    }
    
    /**
     * 简单通知器实现
     */
    class SimpleAdvisor implements Advisor {
        private final Pointcut pointcut;
        private final Advice advice;
        
        public SimpleAdvisor(Pointcut pointcut, Advice advice) {
            this.pointcut = pointcut;
            this.advice = advice;
        }
        
        @Override
        public Pointcut getPointcut() {
            return pointcut;
        }
        
        @Override
        public Advice getAdvice() {
            return advice;
        }
    }
}