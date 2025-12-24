package com.chirper.frontend.application.dto;

/**
 * 登録レスポンスDTO
 */
public record RegisterResponse(
        String userId,
        String message
) {
}
