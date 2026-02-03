package event;

public enum EventType {
    HTTPEVENT("Http");
    private String type;
    EventType(String type)
    {
        this.type=type;
    }
}
