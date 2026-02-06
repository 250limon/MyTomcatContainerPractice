package event;

import java.net.Socket;
import java.nio.channels.SocketChannel;

public class HttpEvent extends AbstractEvent {
    private String requestData;
    //private SocketChannel clientChannel;

    public HttpEvent(String requestData,Object source) {
        super(source,EventType.HTTPEVENT);
        this.requestData = requestData;
    }

    public String getRequestData() {
        return requestData;
    }

}