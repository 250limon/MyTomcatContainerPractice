package observors.impl;

import event.HttpEvent;
import event.HttpEventQueue;
import observors.ThreadObserver;
import server.Container;
import server.RequestProcessTemplate;
import server.impl.SocketConvertImpl;

public class ThreadObserverImpl implements ThreadObserver {
    private RequestProcessTemplate requestProcess;
    private Container container;

    public ThreadObserverImpl(Container container, RequestProcessTemplate requestProcess) {
        this.container = container;
        this.requestProcess = requestProcess;
    }

    @Override
    public void update()  {
        System.out.println("线程 " + Thread.currentThread().getId() + " 开始处理事件队列");
        while (true){
            HttpEvent lastEvent=getHttpEventFromQueue();
            //System.out.println("<UNK> " + Thread.currentThread().getId() + " <UNK>");
            if (lastEvent!=null) {
                requestProcess.process(lastEvent.getClientSocket());
            } else {
                // 如果队列返回null，说明线程被中断
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }

            }
        }
        //System.out.println("线程 " + Thread.currentThread().getId() + " 退出事件处理");
    }



    private HttpEvent getHttpEventFromQueue()
    {
        return HttpEventQueue.getInstance().dequeue();
    }


}