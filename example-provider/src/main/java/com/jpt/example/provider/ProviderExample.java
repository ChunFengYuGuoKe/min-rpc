package com.jpt.example.provider;

import com.jpt.example.common.service.UserService;
import com.jpt.minrpc.RpcApplication;
import com.jpt.minrpc.registry.LocalRegistry;
import com.jpt.minrpc.server.HttpServer;
import com.jpt.minrpc.server.VertxHttpServer;
import com.jpt.minrpc.utils.ConfigUtils;

public class ProviderExample {
    public static void main(String[] args) {
        RpcApplication.init();

        LocalRegistry.register(UserService.class.getName(), UserServiceImpl.class);
        HttpServer server = new VertxHttpServer();
        server.doStart(RpcApplication.getRpcConfig().getServerPort());
    }
}
