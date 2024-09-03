package com.jpt.example.provider;

import com.jpt.example.common.service.UserService;
import com.jpt.minrpc.registry.LocalRegistry;
import com.jpt.minrpc.server.HttpServer;
import com.jpt.minrpc.server.VertxHttpServer;


public class EasyProviderExample {
    public static void main(String[] args) {
        // 注册服务
        LocalRegistry.register(UserService.class.getName(), UserServiceImpl.class);

        // 启动 web 服务
        HttpServer httpServer = new VertxHttpServer();
        httpServer.doStart(8099);
    }
}
