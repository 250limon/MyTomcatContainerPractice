package observors.impl;

import event.Event;
import event.HttpEvent;
import http.impl.HttpRequestImpl;
import observors.Observer;
import server.RequestProcess;

public class HttpEventObserver implements Observer {
    private RequestProcess requestProcess;
    public HttpEventObserver(RequestProcess requestProcess) {
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