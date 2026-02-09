import event.EventManager;
import event.impl.EventManagerImpl;
import filters.Filter;
import filters.FilterManager;
import observors.Observer;
import server.Server;
import spring.SpringContext;
import event.EventType;
/**
 * 主类，启动基于事件驱动的类Tomcat容器
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("=== 基于事件驱动的类Tomcat容器启动 ===");
        System.out.println("=== 使用Spring-like IoC容器 ===");

        // 创建Spring上下文，加载配置文件
        SpringContext springContext = new SpringContext("src/spring/applicationContext.xml");
        
        // 从Spring上下文获取服务器实例
        Server server = springContext.getBean("server");
        
        // 设置过滤器链
        Filter firstFilter = springContext.getBean("firstFilter");
        FilterManager.getInstance().setFirstFilter(firstFilter);
        EventManager eventManager = EventManagerImpl.getInstance();
        Observer observer=springContext.getBean("httpEventObserver");
        eventManager.registerListener(EventType.HTTPEVENT, observer::handle);

        
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
            // 关闭事件管理器的线程池
            EventManagerImpl eventManager = EventManagerImpl.getInstance();
            eventManager.shutdown();
            System.out.println("事件管理器线程池已关闭");
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