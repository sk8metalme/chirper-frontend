package com.chirper.frontend.application.dto;

/**
 * エラー詳細DTO
 */
public record ErrorDetail(
        String field,
        String message
) {
}
