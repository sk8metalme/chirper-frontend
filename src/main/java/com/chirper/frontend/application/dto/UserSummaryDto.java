package com.chirper.frontend.application.dto;

/**
 * フォロワー/フォロー一覧用のユーザー情報DTO
 * UserProfileDtoより軽量で、必要最小限の情報のみを含む
 */
public record UserSummaryDto(
        String userId,
        String username,
        String bio,
        boolean isFollowing
) {
}
