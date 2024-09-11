package com.jpt.example.provider;

import com.jpt.example.common.model.User;
import com.jpt.example.common.service.UserService;
import com.jpt.minrpc.RpcApplication;
import com.jpt.minrpc.config.RpcConfig;
import com.jpt.minrpc.model.ServiceMetaInfo;
import com.jpt.minrpc.registry.LocalRegistry;
import com.jpt.minrpc.registry.Registry;
import com.jpt.minrpc.registry.RegistryFactory;
import com.jpt.minrpc.server.HttpServer;
import com.jpt.minrpc.server.VertxHttpServer;
import com.jpt.minrpc.utils.ConfigUtils;

public class ProviderExample {
    public static void main(String[] args) {
        // rpc 框架初始化
        RpcApplication.init();

        // 注册服务到本地
        String serviceName = UserService.class.getName();
        LocalRegistry.register(serviceName, UserServiceImpl.class);

        // 注册服务到注册中心
        RpcConfig rpcConfig = RpcApplication.getRpcConfig();
        Registry registry = RegistryFactory.getInstance(rpcConfig.getRegistryConfig().getRegistry());
        ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
        serviceMetaInfo.setServiceName(serviceName);
        serviceMetaInfo.setServiceHost(rpcConfig.getServerHost());
        serviceMetaInfo.setServicePort(rpcConfig.getServerPort());

        try {
            registry.register(serviceMetaInfo);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // 启动 web 服务
        HttpServer server = new VertxHttpServer();
        server.doStart(RpcApplication.getRpcConfig().getServerPort());
    }
}
