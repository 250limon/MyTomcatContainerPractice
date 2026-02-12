package spring.test.controller;

import spring.annotation.Autowired;
import spring.mvc.annotation.Controller;
import spring.mvc.annotation.RequestMapping;
import spring.mvc.annotation.ResponseBody;
import spring.test.service.UserService;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户控制器，处理用户相关的请求
 */
@Controller
@RequestMapping("/user")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    /**
     * 处理欢迎页面请求
     */
    @RequestMapping("/welcome")
    public String welcome(Map<String, Object> model) {
        model.put("message", "欢迎访问用户管理系统");
        return "welcome";
    }
    
    /**
     * 处理用户信息请求，返回JSON数据
     */
    @RequestMapping("/info")
    @ResponseBody
    public Object getUserInfo(String userId) {
        Map<String, Object> result = new HashMap<>();
        String userInfo = userService.getUserInfo(userId);
        result.put("success", true);
        result.put("userInfo", userInfo);
        return result;
    }
    
    /**
     * 处理欢迎消息请求，返回JSON数据
     */
    @RequestMapping("/message")
    @ResponseBody
    public Object getWelcomeMessage(String username) {
        String message = userService.getWelcomeMessage(username);
        return message;
    }
}