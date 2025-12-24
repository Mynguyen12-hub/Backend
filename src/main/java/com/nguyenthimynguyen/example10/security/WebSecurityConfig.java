package com.nguyenthimynguyen.example10.security;

import com.nguyenthimynguyen.example10.security.jwt.AuthEntryPointJwt;
import com.nguyenthimynguyen.example10.security.jwt.AuthTokenFilter;
import com.nguyenthimynguyen.example10.CustomUserDetailsService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class WebSecurityConfig {

    @Autowired
    private AuthEntryPointJwt unauthorizedHandler;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        // CORS cho React
        http.cors(cors -> cors.configurationSource(request -> {
            CorsConfiguration config = new CorsConfiguration();
config.setAllowedOrigins(List.of(
            "http://localhost:3000",
            "http://localhost:3001",
            "http://localhost:3002"
        ));            config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
            config.addAllowedHeader("*");
            config.setAllowCredentials(true);
            return config;
        }));

        http.csrf(csrf -> csrf.disable());

        http.exceptionHandling(ex -> ex.authenticationEntryPoint(unauthorizedHandler));

        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.headers(headers -> headers.frameOptions(frameOptions -> frameOptions.sameOrigin()));

        // Quy tắc phân quyền
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/images/**").permitAll()                     // ảnh public
                .requestMatchers("/api/upload/images/**").permitAll()         // upload chỉ ADMIN
        .requestMatchers("/api/auth/**", "/", "/index.html", "/favicon.ico","/api/admin/user/all").permitAll()

        // Public APIs
        .requestMatchers("/api/user/categories").permitAll()
        .requestMatchers("/api/user/products").permitAll()
        .requestMatchers("/api/user/table").permitAll()
        .requestMatchers("/api/user/reservations").permitAll()
        .requestMatchers("/api/user/reservations/login").permitAll()
        .requestMatchers("/api/orders/report/**").permitAll()        // Websocket
        .requestMatchers("/ws-orders/**", "/topic/**", "/queue/**").permitAll()

        // User role
        .requestMatchers("/api/user/**").hasAnyRole("USER", "ADMIN", "MODERATOR", "EMPLOYEE")

        // Admin
        .requestMatchers("/api/admin/**").hasRole("ADMIN")

        // Employee
        .requestMatchers("/api/employee/**").hasAnyRole("ADMIN", "MODERATOR", "EMPLOYEE")

                .anyRequest().authenticated()
        );

        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
