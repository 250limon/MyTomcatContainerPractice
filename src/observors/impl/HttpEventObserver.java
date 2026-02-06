package observors.impl;

import event.Event;
import event.HttpEvent;
import http.impl.HttpRequestImpl;
import observors.Observer;
import server.Container;
import server.RequestProcessTemplate;

import java.nio.channels.SocketChannel;

public class HttpEventObserver implements Observer {
    private RequestProcessTemplate requestProcess;
    public HttpEventObserver(RequestProcessTemplate requestProcess) {
        this.requestProcess = requestProcess;
    }
    @Override
    public void handle(Event event) {
        if (event instanceof HttpEvent)
        {
            requestProcess.process(new HttpRequestImpl(((HttpEvent) event).getRequestData(),event.getSource()));
        }
    }




}