package org.example.Security;

import org.example.Controller.AuthController;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http.csrf(csrf -> csrf.disable()) // Отключить CSRF (только для разработки)
//            .authorizeHttpRequests(authorize -> authorize
//                .requestMatchers("/api/v1/login", "/api/v1/registration").permitAll() // Разрешить доступ к эндпоинтам аутентификации
//                .anyRequest().authenticated()
//            );
//        return http.build();
//    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable) // Отключить CSRF (только для разработки)
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/api/v1/**").permitAll() // Разрешить доступ к эндпоинтам аутентификации
                .anyRequest().permitAll()
            );
        LoggerFactory.getLogger(WebSecurityConfig.class).debug("мы прошли секурити");
        return http.build();
    }

    @Bean
    public AuthenticationManager authManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder =
                http.getSharedObject(AuthenticationManagerBuilder.class);
        return authenticationManagerBuilder.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}