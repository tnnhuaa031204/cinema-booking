package com.cinema.gateway;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

@Component
public class JwtAuthFilter implements GlobalFilter, Ordered {

    private static final String CLAIM_USER_ID = "userId";
    private static final String CLAIM_ROLE = "role";

    private final SecretKey key;

    public JwtAuthFilter(@Value("${jwt.secret}") String secret) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest req = exchange.getRequest();
        String path = req.getURI().getPath();
        HttpMethod method = req.getMethod();

        // 1. Xóa header do client tự gửi (chống giả mạo)
        ServerHttpRequest.Builder builder = req.mutate().headers(h -> {
            h.remove("X-User-Id");
            h.remove("X-Role");
            h.remove("X-Internal-Token");
        });

        // 2. Chặn đường nội bộ
        if (path.startsWith("/internal")) {
            return reject(exchange, HttpStatus.NOT_FOUND);
        }

        // 3. Preflight CORS và đường công khai
        if (method == HttpMethod.OPTIONS || isPublic(path, method)) {
            return chain.filter(exchange.mutate().request(builder.build()).build());
        }

        // 4. Bắt buộc có JWT hợp lệ
        String auth = req.getHeaders().getFirst("Authorization");
        if (auth == null || !auth.startsWith("Bearer ")) {
            return reject(exchange, HttpStatus.UNAUTHORIZED);
        }

        Claims claims;
        try {
            claims = Jwts.parser().verifyWith(key).build()
                    .parseSignedClaims(auth.substring(7)).getPayload();
        } catch (Exception e) {
            return reject(exchange, HttpStatus.UNAUTHORIZED);
        }

        String userId = claims.get(CLAIM_USER_ID) != null
                ? String.valueOf(claims.get(CLAIM_USER_ID)) : claims.getSubject();
        String role = String.valueOf(claims.get(CLAIM_ROLE)).replace("ROLE_", "");

        // 5. Phân quyền admin
        if (requiresAdmin(path, method) && !"ADMIN".equals(role)) {
            return reject(exchange, HttpStatus.FORBIDDEN);
        }

        // 6. Gắn lại header đáng tin cậy cho service phía sau
        builder.header("X-User-Id", userId).header("X-Role", role);
        return chain.filter(exchange.mutate().request(builder.build()).build());
    }

    private boolean isPublic(String path, HttpMethod method) {
        if (path.equals("/api/auth/login") || path.equals("/api/auth/register")) return true;
        if (method == HttpMethod.GET) {
            return path.startsWith("/api/movies") || path.startsWith("/api/showtimes")
                    || path.startsWith("/api/cinemas") || path.startsWith("/api/rooms");
        }
        return false;
    }

    private boolean requiresAdmin(String path, HttpMethod method) {
        boolean write = method != HttpMethod.GET;
        return write && (path.startsWith("/api/movies") || path.startsWith("/api/cinemas")
                || path.startsWith("/api/rooms") || path.startsWith("/api/showtimes"));
    }

    private Mono<Void> reject(ServerWebExchange exchange, HttpStatus status) {
        exchange.getResponse().setStatusCode(status);
        return exchange.getResponse().setComplete();
    }

    @Override
    public int getOrder() {
        return -1;
    }
}