package spring.test.service;

import spring.annotation.Component;

/**
 * 业务服务类，提供业务逻辑处理
 */
@Component
public class UserService {
    public String getWelcomeMessage(String username) {
        return "欢迎, " + username + "! 这是Spring-like MVC示例。";
    }
    
    public String getUserInfo(String userId) {
        return "用户ID: " + userId + "，姓名: 测试用户，邮箱: test@example.com";
    }
}