package com.chirper.frontend.application.dto;

import java.util.List;

/**
 * タイムラインDTO
 */
public record TimelineDto(
        List<TweetDto> tweets,
        int currentPage,
        int totalPages,
        long totalElements
) {
}
