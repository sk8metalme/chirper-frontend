package com.chirper.frontend.application.dto;

import java.time.Instant;

/**
 * TweetDTO
 */
public record TweetDto(
        String tweetId,
        String userId,
        String username,
        String content,
        Instant createdAt,
        int likeCount,
        int retweetCount,
        boolean likedByCurrentUser,
        boolean retweetedByCurrentUser
) {
}
