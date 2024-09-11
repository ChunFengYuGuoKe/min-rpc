package com.jpt.example.consumer;

import com.jpt.example.common.model.User;
import com.jpt.example.common.service.UserService;
import com.jpt.minrpc.config.RpcConfig;
import com.jpt.minrpc.proxy.ServiceProxyFactory;
import com.jpt.minrpc.utils.ConfigUtils;

/**
 * 简易服务消费者示例
 *
 */
public class ConsumerExample {

    public static void main(String[] args) {
        RpcConfig rpc = ConfigUtils.loadConfig(RpcConfig.class, "rpc");
        System.out.println(rpc);

        UserService userService = ServiceProxyFactory.getProxy(UserService.class);
        User user = new User();
        user.setName("minmin");
        User user1 = userService.getUser(user);
        if (user1 != null) {
            System.out.println(user1);
        } else {
            System.out.println("user == null");
        }

    }
}
