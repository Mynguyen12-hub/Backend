package com.nguyenthimynguyen.example10.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.Map;
import java.util.UUID;

/**
 * Interceptor để kiểm tra JWT khi WebSocket handshake.
 * Lấy token từ header Authorization: Bearer <token>
 */
public class JwtHandshakeInterceptor implements HandshakeInterceptor {

    @Override
    public boolean beforeHandshake(ServerHttpRequest request,
                                   ServerHttpResponse response,
                                   WebSocketHandler wsHandler,
                                   Map<String, Object> attributes) throws Exception {
        String token = extractToken(request);

        if (token != null && validateToken(token)) {
            String username = getUsernameFromToken(token);
            if (username == null) username = UUID.randomUUID().toString();
            attributes.put("principal", new StompPrincipal(username));
            return true;
        }

        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        return false;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request,
                               ServerHttpResponse response,
                               WebSocketHandler wsHandler,
                               Exception exception) {
        // Không cần xử lý
    }

    /**
     * Lấy token từ header Authorization hoặc query param
     */
    private String extractToken(ServerHttpRequest request) {
        // 1️⃣ Header Authorization
        String authHeader = request.getHeaders().getFirst("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }

        // 2️⃣ Hoặc query param ?token=xxx (nếu bạn muốn fallback)
        if (request.getURI().getQuery() != null) {
            for (String param : request.getURI().getQuery().split("&")) {
                if (param.startsWith("token=")) {
                    return param.substring(6);
                }
            }
        }

        // 3️⃣ TODO: Có thể lấy từ cookie nếu bạn dùng JWT lưu cookie
        return null;
    }

    /**
     * Kiểm tra token hợp lệ
     */
    private boolean validateToken(String token) {
        // TODO: gọi service JWT validate
        return true;
    }

    /**
     * Lấy username từ token
     */
    private String getUsernameFromToken(String token) {
        // TODO: decode JWT lấy username
        return "user"; // placeholder
    }

    private static class StompPrincipal implements Principal {
        private final String name;
        public StompPrincipal(String name) { this.name = name; }
        @Override
        public String getName() { return name; }
    }
}
