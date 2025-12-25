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
    /**
     * PII保護のため、emailフィールドをマスクしたtoString()を実装
     */
    @Override
    public String toString() {
        return "UserProfileDto{" +
                "userId='" + userId + '\'' +
                ", username='" + username + '\'' +
                ", email='[PROTECTED]'" +
                ", bio='" + bio + '\'' +
                ", followerCount=" + followerCount +
                ", followingCount=" + followingCount +
                ", followedByCurrentUser=" + followedByCurrentUser +
                '}';
    }
}
