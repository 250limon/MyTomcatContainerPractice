package event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 抽象事件管理器类，提供EventManager接口的默认实现
 */
public abstract class AbstractEventManager implements EventManager {
    private final Map<String, List<EventListener>> listeners = new HashMap<>();
    
    @Override
    public void registerListener(String eventType, EventListener listener) {
        if (eventType == null || listener == null) {
            throw new IllegalArgumentException("Event type and listener cannot be null");
        }
        listeners.computeIfAbsent(eventType, k -> new ArrayList<>()).add(listener);

    }
    
    @Override
    public void removeListener(String eventType, EventListener listener) {
        if (eventType == null || listener == null) {
            return;
        }
        
        List<EventListener> eventListeners = listeners.get(eventType);
        if (eventListeners != null) {
            eventListeners.remove(listener);
            if (eventListeners.isEmpty()) {
                listeners.remove(eventType);
            }
        }
    }
    
    @Override
    public void fireEvent(Event event) {
        if (event == null) {
            throw new IllegalArgumentException("Event cannot be null");
        }
        
        List<EventListener> eventListeners = listeners.get(event.getEventType());
        if (eventListeners != null) {
            for (EventListener listener : new ArrayList<>(eventListeners)) {
                try {
                    listener.onEvent(event);
                } catch (Exception e) {
                    handleListenerException(e, listener, event);
                }
            }
        }
    }
    
    /**
     * 处理监听器异常的方法，子类可以重写以提供自定义处理
     * @param e 异常
     * @param listener 监听器
     * @param event 事件
     */
    protected void handleListenerException(Exception e, EventListener listener, Event event) {
        System.err.println("Error in event listener: " + e.getMessage());
        e.printStackTrace();
    }
    
    /**
     * 获取指定事件类型的监听器数量
     * @param eventType 事件类型
     * @return 监听器数量
     */
    protected int getListenerCount(String eventType) {
        List<EventListener> eventListeners = listeners.get(eventType);
        return eventListeners != null ? eventListeners.size() : 0;
    }
    
    /**
     * 获取所有事件类型
     * @return 事件类型列表
     */
    protected List<String> getEventTypes() {
        return new ArrayList<>(listeners.keySet());
    }
}