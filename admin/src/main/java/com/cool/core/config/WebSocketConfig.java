package com.cool.core.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import com.cool.modules.kiosk.ws.KioskWebSocketHandler;

import jakarta.annotation.Resource;

/**
 * @author : Created by niulixiong(hystrix0779@yeah.net) on 2025/07/31/15.
 * @annotation :
 */

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
    
    @Resource
    private KioskWebSocketHandler kioskWebSocketHandler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(kioskWebSocketHandler, "/ws/kiosk") 
                .setAllowedOrigins("*"); // 允许跨域
    }

}
