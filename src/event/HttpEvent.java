package event;

import java.net.Socket;

public class HttpEvent {
    private Socket clientSocket;
    public HttpEvent(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }
    public Socket getClientSocket() {
        return clientSocket;
    }
}
