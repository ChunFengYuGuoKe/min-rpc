package com.jpt.minrpc.proxy;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.jpt.minrpc.RpcApplication;
import com.jpt.minrpc.config.RpcConfig;
import com.jpt.minrpc.constant.RpcConstant;
import com.jpt.minrpc.model.RpcRequest;
import com.jpt.minrpc.model.RpcResponse;
import com.jpt.minrpc.model.ServiceMetaInfo;
import com.jpt.minrpc.registry.Registry;
import com.jpt.minrpc.registry.RegistryFactory;
import com.jpt.minrpc.serializer.JDKSerializer;
import com.jpt.minrpc.serializer.Serializer;
import com.jpt.minrpc.serializer.SerializerFactory;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;

/**
 * 服务代理（JDK 动态代理）
 */
public class ServiceProxy implements InvocationHandler {

    /**
     * 调用代理
     *
     * @return
     * @throws Throwable
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 指定序列化器
        final Serializer serializer = SerializerFactory.getInstance(RpcApplication.getRpcConfig().getSerializer());
//        final Serializer serializer = SerializerFactory.getInstance("a");


        // 构造请求
        String serviceName = method.getDeclaringClass().getName();
        RpcRequest rpcRequest = RpcRequest.builder()
                .serviceName(serviceName)
                .methodName(method.getName())
                .parameterTypes(method.getParameterTypes())
                .args(args)
                .build();
        try {
            // 序列化
            byte[] bodyBytes = serializer.serialize(rpcRequest);
            // 发送请求
            // todo 注意，这里地址被硬编码了（需要使用注册中心和服务发现机制解决）

            // 从注册中心获取服务提供者请求地址
            // 1. 读取配置，从注册中心工厂获取注册中心实例
            RpcConfig rpcConfig = RpcApplication.getRpcConfig();
            Registry registry = RegistryFactory.getInstance(rpcConfig.getRegistryConfig().getRegistry());

            // 2. 构造 ServiceMetaInfo 对象，便于使用 serviceMetaInfo.getServiceKey() 获取查询前缀
            ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
            serviceMetaInfo.setServiceName(serviceName);
            serviceMetaInfo.setServiceVersion(RpcConstant.DEFAULT_SERVICE_VERSION);

            // 3. 前缀查询服务
            String serviceKey = serviceMetaInfo.getServiceKey();
            List<ServiceMetaInfo> serviceMetaInfoList = registry.serviceDiscovery(serviceKey);

            if (CollUtil.isEmpty(serviceMetaInfoList)) {
                throw new RuntimeException("暂无服务地址");
            }

            // 5. 暂时选取第一个
            ServiceMetaInfo selectedServiceMetaInfo = serviceMetaInfoList.get(0);

            // 6. 获取地址，发送请求
            try (HttpResponse httpResponse = HttpRequest.post(selectedServiceMetaInfo.getServiceAddress())
                    .body(bodyBytes)
                    .execute()) {
                byte[] result = httpResponse.bodyBytes();
                // 反序列化
                RpcResponse rpcResponse = serializer.deserialize(result, RpcResponse.class);
                return rpcResponse.getData();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}

