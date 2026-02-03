package event;

/**
 * 事件接口，所有事件的基类
 */
public interface Event {
    /**
     * 获取事件源
     * @return 事件源对象
     */
    Object getSource();
    
    /**
     * 获取事件类型
     * @return 事件类型
     */
    String getEventType();
}