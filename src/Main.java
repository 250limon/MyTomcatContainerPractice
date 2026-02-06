import event.EventManager;
import event.EventType;
import event.impl.EventManagerImpl;
import filters.Filter;
import filters.FilterManager;
import filters.impl.FilterImpl;
import filters.impl.FinalFilter;
import filters.impl.RequestParse;
import observors.Observer;
import observors.impl.HttpEventObserver;
import server.Container;
import server.RequestDataString;
import server.RequestProcessTemplate;
import server.Server;
import server.impl.*;

/**
 * 主类，启动基于事件驱动的类Tomcat容器
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("=== 基于事件驱动的类Tomcat容器启动 ===");

        Container container = new ContainerImpl();
        RequestProcessTemplate requestProcess = new RequestProcessImpl(container);

         //注册事件处理
        EventManager eventManager=EventManagerImpl.getInstance();
        Observer httpEventObserver = new HttpEventObserver(requestProcess);
        eventManager.registerListener(EventType.HTTPEVENT, httpEventObserver::handle);

        // 创建服务器实例
        RequestDataString requestDataString=new RequestDataFromBuffer();
        Server server = new NioServerImpl(container,eventManager,requestDataString);
        Filter finalFilter=new FinalFilter();
        Filter requestParesFilter=new RequestParse(finalFilter);
        Filter firstFilter=new FilterImpl(requestParesFilter);
        FilterManager.getInstance().setFirstFilter(firstFilter);
        // 设置服务器端口
        server.setPort(8080);

        // 启动服务器
        server.start();
        
        // 等待服务器运行
        waitForServer(server);
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