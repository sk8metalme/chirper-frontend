package com.chirper.frontend.domain.valueobject;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.*;

class DisplayTimestampTest {

    @Test
    void toRelativeTime_1分未満の場合はたった今を返す() {
        // Arrange: 30秒前
        Instant now = Instant.now();
        Instant timestamp = now.minus(30, ChronoUnit.SECONDS);
        DisplayTimestamp displayTimestamp = new DisplayTimestamp(timestamp);

        // Act
        String result = displayTimestamp.toRelativeTime();

        // Assert
        assertEquals("たった今", result);
    }

    @Test
    void toRelativeTime_1時間未満の場合は分前を返す() {
        // Arrange: 5分前
        Instant now = Instant.now();
        Instant timestamp = now.minus(5, ChronoUnit.MINUTES);
        DisplayTimestamp displayTimestamp = new DisplayTimestamp(timestamp);

        // Act
        String result = displayTimestamp.toRelativeTime();

        // Assert
        assertEquals("5分前", result);
    }

    @Test
    void toRelativeTime_24時間未満の場合は時間前を返す() {
        // Arrange: 3時間前
        Instant now = Instant.now();
        Instant timestamp = now.minus(3, ChronoUnit.HOURS);
        DisplayTimestamp displayTimestamp = new DisplayTimestamp(timestamp);

        // Act
        String result = displayTimestamp.toRelativeTime();

        // Assert
        assertEquals("3時間前", result);
    }

    @Test
    void toRelativeTime_7日未満の場合は日前を返す() {
        // Arrange: 2日前
        Instant now = Instant.now();
        Instant timestamp = now.minus(2, ChronoUnit.DAYS);
        DisplayTimestamp displayTimestamp = new DisplayTimestamp(timestamp);

        // Act
        String result = displayTimestamp.toRelativeTime();

        // Assert
        assertEquals("2日前", result);
    }

    @Test
    void toRelativeTime_7日以上の場合は日付を返す() {
        // Arrange: 8日前
        Instant now = Instant.now();
        Instant timestamp = now.minus(8, ChronoUnit.DAYS);
        DisplayTimestamp displayTimestamp = new DisplayTimestamp(timestamp);

        // Act
        String result = displayTimestamp.toRelativeTime();

        // Assert
        // 日付形式（例: "12月16日"）であることを確認
        assertTrue(result.matches("\\d+月\\d+日"), "日付形式である: " + result);
    }

    @Test
    void toAbsoluteTime_絶対時刻表示を返す() {
        // Arrange: 2025年12月23日 14:30:00 UTC
        Instant timestamp = Instant.parse("2025-12-23T14:30:00Z");
        DisplayTimestamp displayTimestamp = new DisplayTimestamp(timestamp);

        // Act
        String result = displayTimestamp.toAbsoluteTime();

        // Assert
        // システムのタイムゾーンに依存するため、形式のみ確認
        assertTrue(result.matches("\\d{4}年\\d{1,2}月\\d{1,2}日 \\d{1,2}:\\d{2}"),
            "絶対時刻形式である: " + result);
    }

    @Test
    void constructor_nullの場合は例外をスローする() {
        // Act & Assert
        assertThrows(NullPointerException.class, () -> {
            new DisplayTimestamp(null);
        });
    }
}
