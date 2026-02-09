package event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 抽象事件管理器类，提供EventManager接口的默认实现
 */
public abstract class AbstractEventManager implements EventManager {
    private final Map<EventType, List<EventListener>> listeners = new HashMap<>();
    private final ExecutorService threadPool;
    
    /**
     * 默认构造函数，创建具有合理默认配置的线程池
     */
    public AbstractEventManager() {
        // 创建线程池：核心线程数为CPU核心数，最大线程数为CPU核心数*2，工作队列大小为100
        int corePoolSize = Runtime.getRuntime().availableProcessors();
        this.threadPool = Executors.newFixedThreadPool(corePoolSize);
    }
    
    /**
     * 构造函数，允许自定义线程池
     * @param threadPool 自定义的线程池
     */
    public AbstractEventManager(ExecutorService threadPool) {
        this.threadPool = threadPool;
    }
    
    @Override
    public void registerListener(EventType eventType, EventListener listener) {
        if (eventType == null || listener == null) {
            throw new IllegalArgumentException("Event type and listener cannot be null");
        }
        listeners.computeIfAbsent(eventType, k -> new ArrayList<>()).add(listener);

    }
    
    @Override
    public void removeListener(EventType eventType, EventListener listener) {
        if (eventType == null || listener == null) {
            return;
        }
        
        List<EventListener> eventListeners = listeners.get(eventType);
        if (eventListeners != null) {
            eventListeners.remove(listener);
            if (eventListeners.isEmpty()) {
                listeners.remove(eventType);
            }
        }
    }
    
    @Override
    public void fireEvent(Event event) {
        if (event == null) {
            throw new IllegalArgumentException("Event cannot be null");
        }
        List<EventListener> eventListeners = listeners.get(event.getEventType());
        if (eventListeners != null) {
            for (EventListener listener : new ArrayList<>(eventListeners)) {
                // 将事件处理任务提交给线程池
                threadPool.execute(() -> {
                    try {
                        listener.onEvent(event);
                    } catch (Exception e) {
                        handleListenerException(e, listener, event);
                    }
                });
            }
        }
    }
    
    /**
     * 处理监听器异常的方法，子类可以重写以提供自定义处理
     * @param e 异常
     * @param listener 监听器
     * @param event 事件
     */
    protected void handleListenerException(Exception e, EventListener listener, Event event) {
        System.err.println("Error in event listener: " + e.getMessage());
        e.printStackTrace();
    }
    
    /**
     * 获取指定事件类型的监听器数量
     * @param eventType 事件类型
     * @return 监听器数量
     */
    protected int getListenerCount(String eventType) {
        List<EventListener> eventListeners = listeners.get(eventType);
        return eventListeners != null ? eventListeners.size() : 0;
    }
    
    /**
     * 获取所有事件类型
     * @return 事件类型列表
     */
    protected List<EventType> getEventTypes() {
        return new ArrayList<>(listeners.keySet());
    }
    
    /**
     * 关闭线程池，释放资源
     */
    public void shutdown() {
        if (threadPool != null && !threadPool.isShutdown()) {
            threadPool.shutdown();
        }
    }
    
    /**
     * 立即关闭线程池，停止所有正在执行的任务
     */
    public void shutdownNow() {
        if (threadPool != null && !threadPool.isShutdown()) {
            threadPool.shutdownNow();
        }
    }
    
    /**
     * 获取线程池信息
     * @return 线程池信息字符串
     */
    public String getThreadPoolInfo() {
        if (threadPool instanceof ThreadPoolExecutor) {
            ThreadPoolExecutor tpe = (ThreadPoolExecutor) threadPool;
            return String.format("线程池信息: 核心线程数=%d, 最大线程数=%d, 当前线程数=%d, 任务队列大小=%d, 已完成任务数=%d",
                    tpe.getCorePoolSize(),
                    tpe.getMaximumPoolSize(),
                    tpe.getPoolSize(),
                    tpe.getQueue().size(),
                    tpe.getCompletedTaskCount());
        }
        return "线程池信息: " + threadPool.toString();
    }
}