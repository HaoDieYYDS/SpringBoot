package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

//Socket配置

@Configuration
public class WebSocketConfig {
    //@Bean注解表示在Spring容器中创建一个名为"serverEndpointExporter"的Bean。
    //serverEndpointExporter()方法返回一个ServerEndpointExporter对象，该对象用于自动注册WebSocket端点。
    @Bean
    public ServerEndpointExporter serverEndpointExporter(){return new ServerEndpointExporter();}
}
