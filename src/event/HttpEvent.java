package event;

import java.net.Socket;

public class HttpEvent extends AbstractEvent {
    private Socket clientSocket;
    public HttpEvent(Socket clientSocket) {
        super(clientSocket,EventType.HTTPEVENT);
        this.clientSocket = clientSocket;
    }
    public Socket getClientSocket() {
        return clientSocket;
    }
}
