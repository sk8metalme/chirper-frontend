package com.chirper.frontend.application.dto;

import java.util.List;

/**
 * フォロワー/フォロー一覧のDTO
 * ページネーション情報を含む
 */
public record FollowListDto(
        List<UserSummaryDto> users,
        int currentPage,
        int totalPages,
        long totalItems
) {
}
