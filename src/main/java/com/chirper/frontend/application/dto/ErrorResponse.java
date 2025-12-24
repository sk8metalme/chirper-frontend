package com.chirper.frontend.application.dto;

import java.time.Instant;
import java.util.List;

/**
 * エラーレスポンスDTO
 */
public record ErrorResponse(
        String code,
        String message,
        List<ErrorDetail> details,
        Instant timestamp
) {
}
