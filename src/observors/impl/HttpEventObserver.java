package observors.impl;

import event.Event;
import event.HttpEvent;
import event.HttpEventQueue;
import observors.Observer;
import server.Container;
import server.RequestProcessTemplate;

public class HttpEventObserver implements Observer {
    private RequestProcessTemplate requestProcess;
    private Container container;

    public HttpEventObserver(Container container, RequestProcessTemplate requestProcess) {
        this.container = container;
        this.requestProcess = requestProcess;
    }

    @Override
    public void handle(Event event)  {
        if(event instanceof HttpEvent)
            requestProcess.process(((HttpEvent)event).getClientSocket());
        }



    private HttpEvent getHttpEventFromQueue()
    {
        return HttpEventQueue.getInstance().dequeue();
    }


}