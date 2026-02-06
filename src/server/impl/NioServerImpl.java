package server.impl;

import event.Event;
import event.HttpEvent;
import event.EventManager;
import server.Container;
import server.RequestDataString;
import server.Server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 基于NIO机制的服务器实现
 */
public class NioServerImpl implements Server {
    private int port = 8080;
    private String serverName = "MyNioTomcat";
    private ServerState state = ServerState.STOPPED;
    private ServerSocketChannel serverSocketChannel;
    private Selector selector;
    private ExecutorService threadPool;
    private Container container;
    private EventManager eventManager;
    private RequestDataString requestDataString;

    public NioServerImpl(Container container, EventManager eventManager, RequestDataString requestDataString) {
        this.container = container;
        this.eventManager = eventManager;
        this.requestDataString = requestDataString;
        // 注册NIO事件观察者
        registerNioObserver();
    }
    
    /**
     * 注册NIO事件观察者
     */
    private void registerNioObserver() {
        // 创建NIO事件观察者
        observors.impl.NioHttpEventObserver nioObserver = new observors.impl.NioHttpEventObserver(container);
        // 注册到事件管理器
        //eventManager.registerObserver(nioObserver);
    }

    @Override
    public void start() {
        if (state == ServerState.STARTED) {
            System.out.println("服务器已启动");
            return;
        }

        System.out.println("=== " + serverName + " (NIO) 启动中 ===");

        try {
            // 初始化容器
            container.init();

            // 创建Selector
            selector = Selector.open();

            // 创建ServerSocketChannel
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.configureBlocking(false); // 设置为非阻塞模式
            serverSocketChannel.bind(new InetSocketAddress(port));

            // 注册到Selector，监听连接事件
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

            state = ServerState.STARTED;

            System.out.println("服务器已启动，监听端口: " + port);
            System.out.println("访问地址: http://localhost:" + port);

            // 启动NIO事件循环
            startNioEventLoop();

        } catch (IOException e) {
            System.err.println("服务器启动失败: " + e.getMessage());
            e.printStackTrace();
            stop();
        }
    }

    /**
     * 启动NIO事件循环
     */
    private void startNioEventLoop() {
        new Thread(() -> {
            System.out.println("NIO事件循环线程启动");
            try {
                while (state == ServerState.STARTED) {
                    // 阻塞等待事件发生，设置超时时间避免无限阻塞
                    int readyChannels = selector.select(1000);
                    if (readyChannels == 0) {
                        continue;
                    }

                    // 获取所有就绪的SelectionKey
                    Set<SelectionKey> selectionKeys = selector.selectedKeys();
                    Iterator<SelectionKey> iterator = selectionKeys.iterator();

                    while (iterator.hasNext()) {
                        SelectionKey key = iterator.next();
                        iterator.remove();

                        try {
                            // 处理连接事件
                            if (key.isAcceptable()) {
                                handleAccept(key);
                            }
                            // 处理读事件
                            else if (key.isReadable()) {
                                handleRead(key);
                            }
                        } catch (IOException e) {
                            System.err.println("处理NIO事件时发生异常: " + e.getMessage());
                            e.printStackTrace();
                            // 关闭通道和键
                            key.cancel();
                            key.channel().close();
                        }
                    }
                }
            } catch (IOException e) {
                if (state == ServerState.STARTED) {
                    System.err.println("NIO事件循环发生异常: " + e.getMessage());
                    e.printStackTrace();
                    stop();
                }
            }
        }, "NioEventLoop").start();
    }

    /**
     * 处理连接事件
     */
    private void handleAccept(SelectionKey key) throws IOException {
        ServerSocketChannel serverChannel = (ServerSocketChannel) key.channel();
        SocketChannel clientChannel = serverChannel.accept();
        if (clientChannel == null) {
            return;
        }
        System.out.println("接受新连接: " + clientChannel.getRemoteAddress());
        clientChannel.configureBlocking(false); // 设置为非阻塞模式

        // 为客户端连接创建Buffer
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        // 注册读事件
        clientChannel.register(selector, SelectionKey.OP_READ, buffer);
    }

    /**
     * 处理读事件
     */
    private void handleRead(SelectionKey key) throws IOException {
        SocketChannel clientChannel = (SocketChannel) key.channel();
        ByteBuffer buffer = (ByteBuffer) key.attachment();

        // 读取数据
        int bytesRead = clientChannel.read(buffer);
        if (bytesRead == -1) {
            // 客户端关闭连接
            System.out.println("客户端关闭连接: " + clientChannel.getRemoteAddress());
            key.cancel();
            clientChannel.close();
            return;
        }
        // 转换为字符串
        String requestData = requestDataString.getRequestData(buffer);

        // 检查是否包含完整的HTTP请求（简单检查是否包含\r\n\r\n）
        if (requestData.contains("\r\n\r\n")) {
            // 创建HttpEvent并加入队列，同时传递客户端通道信息
            HttpEvent event = new HttpEvent(requestData, clientChannel);
            eventManager.fireEvent(event);

            // 重置Buffer并重新注册读事件
            buffer.clear();
        } else {
            // 没有完整请求，继续读取
            buffer.compact();
        }
    }

    @Override
    public void stop() {
        if (state == ServerState.STOPPED) {
            return;
        }

        System.out.println("服务器停止中...");
        state = ServerState.STOPPED;

        try {
            // 关闭选择器
            if (selector != null) {
                selector.close();
            }

            // 关闭服务器通道
            if (serverSocketChannel != null) {
                serverSocketChannel.close();
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
            e.printStackTrace();
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