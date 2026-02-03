package event.impl;

import event.AbstractEventManager;

/**
 * 事件管理器的具体实现类
 */
public class EventManagerImpl extends AbstractEventManager {
    private static EventManagerImpl instance;
    
    private EventManagerImpl() {
        // 私有构造函数，单例模式
    }
    
    /**
     * 获取事件管理器实例
     * @return 事件管理器实例
     */
    public static synchronized EventManagerImpl getInstance() {
        if (instance == null) {
            instance = new EventManagerImpl();
        }
        return instance;
    }
}