package event;

/**
 * 抽象事件类，提供Event接口的默认实现
 */
public abstract class AbstractEvent implements Event {
    private final Object source;
    private final String eventType;
    
    public AbstractEvent(Object source, String eventType) {
        this.source = source;
        this.eventType = eventType;
    }
    
    @Override
    public Object getSource() {
        return source;
    }
    
    @Override
    public String getEventType() {
        return eventType;
    }
    
    @Override
    public String toString() {
        return "Event{" +
                "source=" + source +
                ", eventType='" + eventType + '\'' +
                '}';
    }
}