package event;

import observors.Observer;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class HttpEventQueue {
    private static HttpEventQueue instance = new HttpEventQueue();
    private final BlockingQueue<HttpEvent> queue = new LinkedBlockingQueue<>();
    //private final Queue<HttpEvent> queue = new LinkedList<>();
    private List<Observer> observers = new LinkedList<>();
    private HttpEventQueue() {}
    public static HttpEventQueue getInstance() {
           return instance;
    }
    public void enqueue(HttpEvent event) {
        queue.offer(event);
        // 使用ConcurrentLinkedQueue，不再需要通知观察者
    }
    public HttpEvent dequeue() {
        try {
            // 使用take()方法，队列为空时会阻塞等待，避免轮询
            return queue.take();
        } catch (InterruptedException e) {
            // 如果线程被中断，返回null
            Thread.currentThread().interrupt();
            return null;
        }
//        if (isEmpty()) {
//            return null;
//        }
//
//        return queue.poll();
    }
     public boolean isEmpty() {
        return queue.isEmpty();
    }
     // 不再需要观察者模式，移除相关方法
    public void addObserver(Observer observer) {
        // 空实现，保持接口兼容性
    }


}