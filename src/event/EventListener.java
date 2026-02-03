package event;

/**
 * 事件监听器接口
 */
public interface EventListener {
    /**
     * 处理事件
     * @param event 要处理的事件
     */
    void onEvent(Event event);
}