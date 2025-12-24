package com.nguyenthimynguyen.example10.config;

import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOServer;
import org.springframework.context.annotation.Bean;

@org.springframework.context.annotation.Configuration // dùng tên đầy đủ
public class SocketIOConfig {

    @Bean
    public SocketIOServer socketIOServer() {
        Configuration config = new Configuration(); // đây là socket.io config
        config.setHostname("0.0.0.0");
        config.setPort(9092);

        SocketIOServer server = new SocketIOServer(config);

        server.addEventListener("addItem", ItemEvent.class, (client, data, ackRequest) -> {
            System.out.println("Khách hàng thêm món: " + data);
            server.getBroadcastOperations().sendEvent("newItem", data);
        });

        return server;
    }
}
 