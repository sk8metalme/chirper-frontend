package com.chirper.frontend.application.dto;

/**
 * ログインレスポンスDTO
 */
public record LoginResponse(
        String jwtToken,
        String userId
) {
}
