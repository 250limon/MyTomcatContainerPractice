package event;

/**
 * 抽象事件类，提供Event接口的默认实现
 */
public abstract class AbstractEvent implements Event {
    private final Object source;
    private final EventType eventType;
    
    public AbstractEvent(Object source, EventType eventType) {
        this.source = source;
        this.eventType = eventType;
    }
    
    @Override
    public Object getSource() {
        return source;
    }
    
    @Override
    public EventType getEventType() {
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