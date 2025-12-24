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
            // Phase 2: CSRF保護を有効化(Thymeleafフォームで使用)
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/api/**") // API エンドポイントは除外 (将来の拡張用)
            )
            .authorizeHttpRequests(auth -> auth
                // 公開ページ
                .requestMatchers("/", "/login", "/register", "/logout", "/error/**").permitAll()
                // 静的リソース
                .requestMatchers("/css/**", "/js/**", "/images/**").permitAll()
                // その他は認証が必要
                .anyRequest().authenticated()
            )
            // ログアウト設定（AuthControllerが独自の認証・ログアウト処理を実装）
            .logout(logout -> logout
                .logoutUrl("/logout-deprecated")  // AuthControllerの/logoutと競合しないように変更
                .logoutSuccessUrl("/")
                .permitAll()
            )
            // セッション管理 (JWTトークンベース認証)
            .sessionManagement(session -> session
                .sessionFixation().changeSessionId()  // セッション固定攻撃対策
            );

        return http.build();
    }
}
