package com.nguyenthimynguyen.example10.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Broker để server gửi message tới client
        config.enableSimpleBroker("/topic", "/queue"); 

        // Prefix để client gửi message đến server
        config.setApplicationDestinationPrefixes("/app"); 

        // Prefix để gửi message đến 1 user cụ thể (khách hàng/nhân viên)
        config.setUserDestinationPrefix("/user");
    }

    @Override
   public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws-orders")
               .setAllowedOriginPatterns("*")
        .addInterceptors(new JwtHandshakeInterceptor())
        .withSockJS()
        .setSessionCookieNeeded(false)
        .setClientLibraryUrl("https://cdn.jsdelivr.net/sockjs/1.5.1/sockjs.min.js");
    }
}
