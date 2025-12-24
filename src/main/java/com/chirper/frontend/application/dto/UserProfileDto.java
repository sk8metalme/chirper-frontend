package com.chirper.frontend.application.dto;

/**
 * ユーザープロフィールDTO
 */
public record UserProfileDto(
        String userId,
        String username,
        String email,
        String bio,
        int followerCount,
        int followingCount,
        boolean followedByCurrentUser
) {
}
