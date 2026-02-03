import event.EventListener;
import event.HttpEventQueue;
import event.impl.EventManagerImpl;
import filters.Filter;
import filters.FilterManager;
import filters.impl.FilterImpl;
import filters.impl.FinalFilter;
import filters.impl.RequestParse;
import http.HttpRequestEvent;
import observors.ThreadObserver;
import observors.impl.ThreadObserverImpl;
import server.Container;
import server.RequestProcessTemplate;
import server.Server;
import server.impl.ContainerImpl;
import server.impl.RequestProcessImpl;
import server.impl.ServerImpl;
import server.impl.SocketConvertImpl;

/**
 * 主类，启动基于事件驱动的类Tomcat容器
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("=== 基于事件驱动的类Tomcat容器启动 ===");


        Container container = new ContainerImpl();
        RequestProcessTemplate requestProcess = new RequestProcessImpl(container,new SocketConvertImpl());
        ThreadObserver threadObserver = new ThreadObserverImpl(container,requestProcess);
        HttpEventQueue.getInstance().addObserver(threadObserver);
        // 创建服务器实例
        Server server = new ServerImpl(container,requestProcess,threadObserver);

        Filter finalFilter=new FinalFilter();
        Filter requestParesFilter=new RequestParse(finalFilter);
        Filter firstFilter=new FilterImpl(requestParesFilter);
        FilterManager.getInstance().setFirstFilter(firstFilter);
        // 设置服务器端口
        server.setPort(8080);
        
        // 注册HTTP请求监听器
        registerHttpEventListener();
        
        // 启动服务器
        server.start();
        
        // 等待服务器运行
        waitForServer(server);
    }
    /**
     * 注册HTTP请求监听器
     */
    private static void registerHttpEventListener() {
        //如果事件类型是HTTP类型，
        EventManagerImpl.getInstance().registerListener("HTTP_REQUEST", new EventListener() {
            @Override
            public void onEvent(event.Event event) {
                if (event instanceof HttpRequestEvent) {
                    HttpRequestEvent httpEvent = (HttpRequestEvent) event;
                    System.out.println("\n监听到HTTP请求事件：");
                    System.out.println("- 事件类型: " + httpEvent.getEventType());
                    System.out.println("- 请求方法: " + httpEvent.getRequest().getMethod());
                    System.out.println("- 请求URL: " + httpEvent.getRequest().getUrl());
                    System.out.println("- 客户端IP: " + httpEvent.getSource());
                }
            }
        });
        System.out.println("HTTP请求监听器已注册");
    }
    
    /**
     * 等待服务器运行
     */
    private static void waitForServer(Server server) {
        // 添加关闭钩子，优雅停止服务器
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\n=== 服务器正在停止 ===");
            server.stop();
        }));

        // 保持主线程运行
        System.out.println("\n服务器运行中，按 Ctrl+C 停止...");
        while (server.getState() == Server.ServerState.STARTED) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}