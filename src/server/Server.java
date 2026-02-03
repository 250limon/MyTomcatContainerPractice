package server;

/**
 * 服务器接口，定义服务器的核心功能
 */
public interface Server {
    /**
     * 启动服务器
     */
    void start();
    
    /**
     * 停止服务器
     */
    void stop();
    
    /**
     * 暂停服务器
     */
    void pause();
    
    /**
     * 恢复服务器
     */
    void resume();
    
    /**
     * 获取服务器状态
     * @return 服务器状态
     */
    ServerState getState();
    
    /**
     * 获取服务器端口
     * @return 服务器端口
     */
    int getPort();
    
    /**
     * 设置服务器端口
     * @param port 服务器端口
     */
    void setPort(int port);
    
    /**
     * 获取服务器名称
     * @return 服务器名称
     */
    String getServerName();
    
    /**
     * 设置服务器名称
     * @param serverName 服务器名称
     */
    void setServerName(String serverName);
    
    /**
     * 服务器状态枚举
     */
    enum ServerState {
        STARTED, STOPPED, PAUSED
    }
}