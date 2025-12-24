package com.chirper.frontend.application.dto;

/**
 * ログインリクエストDTO
 */
public record LoginRequest(
        String username,
        String password
) {
}
