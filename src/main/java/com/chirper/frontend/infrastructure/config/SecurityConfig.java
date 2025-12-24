package com.chirper.frontend.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security設定
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // TODO: Phase 2 (Presentation Layer) 実装時にCSRF保護を有効化する
            // 現在はAPIクライアントのみのため無効化しているが、
            // Thymeleafフォームを実装する際は必ず有効化すること
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                // Phase 1: APIクライアントのみのため、全てのリクエストを許可
                // TODO: Phase 2でコントローラー実装時に適切な認証設定を追加
                .anyRequest().permitAll()
            )
            // Phase 1ではカスタムログイン処理を使用（Presentation Layer未実装）
            .formLogin(form -> form.disable())
            // Phase 1ではセッション管理は不要（JWTトークンベース認証）
            .sessionManagement(session -> session
                .sessionFixation().none()
            );

        return http.build();
    }
}
