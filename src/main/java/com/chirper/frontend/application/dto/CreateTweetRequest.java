package com.chirper.frontend.application.dto;

/**
 * ツイート作成リクエストDTO
 */
public record CreateTweetRequest(
        String content
) {
}
