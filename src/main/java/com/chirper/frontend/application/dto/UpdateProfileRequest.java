package com.chirper.frontend.application.dto;

/**
 * プロフィール更新リクエストDTO
 */
public record UpdateProfileRequest(
        String displayName,
        String bio,
        String avatarUrl
) {
}
