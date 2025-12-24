package com.nguyenthimynguyen.example10.controllers;

import com.nguyenthimynguyen.example10.dto.OrderDTO;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class OrderWebSocketController {

    // Client gửi message đến /app/order
    @MessageMapping("/order")
    // Server gửi message tới tất cả client subscribe /topic/orders
    @SendTo("/topic/orders")
    public OrderDTO sendOrder(OrderDTO order) throws Exception {
        // Bạn có thể xử lý order, lưu database, validate ở đây
        // Ví dụ demo: trả về luôn order nhận được
        return order;
    }
}
