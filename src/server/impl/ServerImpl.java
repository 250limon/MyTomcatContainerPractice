package server.impl;

import event.HttpEvent;
import event.HttpEventQueue;
import observors.ThreadObserver;
import observors.impl.ThreadObserverImpl;
import server.Container;
import server.RequestProcessTemplate;
import server.Server;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 服务器的具体实现类，负责监听端口并处理客户端连接
 */
public class ServerImpl implements Server {
    private int port = 8080;
    private String serverName = "MyTomcat";
    private ServerState state = ServerState.STOPPED;
    private ServerSocket serverSocket;
    private RequestProcessTemplate requestProcess;
    private ExecutorService threadPool;
    private Container container;
    private ThreadObserver threadObserver;

    public ServerImpl(Container container,RequestProcessTemplate requestProcess,ThreadObserver threadObserver) {
        this.container = container;
        this.requestProcess = requestProcess;
        this.threadObserver = threadObserver;
    }

    @Override
    public void start() {
        if (state == ServerState.STARTED) {
            System.out.println("服务器已启动");
            return;
        }

        System.out.println("=== " + serverName + " 启动中 ===");

        try {
            // 初始化容器
            container.init();

            // 创建线程池，支持3个并发请求
            threadPool = Executors.newFixedThreadPool(2);

            // 启动服务器Socket
            serverSocket = new ServerSocket(port);
            state = ServerState.STARTED;

            System.out.println("服务器已启动，监听端口: " + port);
            System.out.println("访问地址: http://localhost:" + port);

            // 开始接受客户端连接
            acceptConnections();
        } catch (IOException e) {
            System.err.println("服务器启动失败: " + e.getMessage());
            stop();
        }
    }

    /**
     * 接受客户端连接
     */
    private void acceptConnections() {
        new Thread(() -> {
            // 提交3个事件处理器到线程池，每个线程处理事件队列
            for (int i = 0; i < 2; i++) {
                final int threadId = i;
                threadPool.submit(() -> {
                    System.out.println("事件处理器线程 " + threadId + " 启动");
                    threadObserver.update();
                });
            }
            
            while (state == ServerState.STARTED) {
                try {
                    Socket clientSocket = serverSocket.accept();//每次有新的客户端连接都会返回一个新的实例
                    // 创建HttpEvent对象
                    HttpEvent event = new HttpEvent( clientSocket);
                    // 将事件加入队列
                    HttpEventQueue.getInstance().enqueue(event);
                } catch (IOException e) {
                    if (state == ServerState.STARTED) {
                        System.err.println("接受客户端连接失败: " + e.getMessage());
                    }
                }
            }
        }, "ConnectionAcceptor").start();
    }

    @Override
    public void stop() {
        if (state == ServerState.STOPPED) {
            return;
        }

        System.out.println("服务器停止中...");
        state = ServerState.STOPPED;

        try {
            // 关闭服务器Socket
            if (serverSocket != null) {
                serverSocket.close();
            }

            // 关闭线程池
            if (threadPool != null) {
                threadPool.shutdown();
            }

            // 销毁容器
            container.destroy();

            System.out.println("服务器已停止");
        } catch (IOException e) {
            System.err.println("服务器停止异常: " + e.getMessage());
        }
    }

    @Override
    public void pause() {
        if (state == ServerState.PAUSED) {
            return;
        }

        state = ServerState.PAUSED;
        System.out.println("服务器已暂停");
    }

    @Override
    public void resume() {
        if (state == ServerState.STARTED) {
            return;
        }

        state = ServerState.STARTED;
        System.out.println("服务器已恢复");
    }

    @Override
    public ServerState getState() {
        return state;
    }

    @Override
    public int getPort() {
        return port;
    }

    @Override
    public void setPort(int port) {
        if (state == ServerState.STARTED) {
            throw new IllegalStateException("服务器已启动，无法修改端口");
        }
        this.port = port;
    }

    @Override
    public String getServerName() {
        return serverName;
    }

    @Override
    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

}