package event;

/**
 * 事件管理器接口，负责事件的注册、移除和触发
 */
public interface EventManager {
    /**
     * 注册事件监听器
     * @param eventType 事件类型
     * @param listener 事件监听器
     */
    void registerListener(String eventType, EventListener listener);
    
    /**
     * 移除事件监听器
     * @param eventType 事件类型
     * @param listener 事件监听器
     */
    void removeListener(String eventType, EventListener listener);
    
    /**
     * 触发事件
     * @param event 要触发的事件
     */
    void fireEvent(Event event);
}