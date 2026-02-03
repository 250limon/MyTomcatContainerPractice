package http;

import event.Event;
import event.EventType;

/**
 * HTTP请求事件
 */
public class HttpRequestEvent implements Event {
    private static final EventType EVENT_TYPE = EventType.HTTPEVENT;
    private final Object source;
    private final HttpRequest request;
    private final HttpResponse response;
    
    public HttpRequestEvent(Object source, HttpRequest request, HttpResponse response) {
        this.source = source;
        this.request = request;
        this.response = response;
    }
    
    @Override
    public Object getSource() {
        return source;
    }
    
    @Override
    public EventType getEventType() {
        return EVENT_TYPE;
    }
    
    /**
     * 获取HTTP请求
     * @return HTTP请求对象
     */
    public HttpRequest getRequest() {
        return request;
    }
    
    /**
     * 获取HTTP响应
     * @return HTTP响应对象
     */
    public HttpResponse getResponse() {
        return response;
    }
}