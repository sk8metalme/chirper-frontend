package com.chirper.frontend.domain.valueobject;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * 表示用タイムスタンプを表す値オブジェクト
 *
 * 相対時刻表示、絶対時刻表示ロジックを持つ
 */
public class DisplayTimestamp {
    private final Instant timestamp;

    // 日本語ロケールのフォーマッター
    private static final DateTimeFormatter DATE_FORMATTER =
        DateTimeFormatter.ofPattern("M月d日");
    private static final DateTimeFormatter DATETIME_FORMATTER =
        DateTimeFormatter.ofPattern("yyyy年M月d日 H:mm");

    /**
     * DisplayTimestampを構築する
     *
     * @param timestamp タイムスタンプ（必須）
     * @throws NullPointerException timestampがnullの場合
     */
    public DisplayTimestamp(Instant timestamp) {
        Objects.requireNonNull(timestamp, "タイムスタンプはnullにできません");
        this.timestamp = timestamp;
    }

    /**
     * 相対時刻表示
     *
     * @return 相対時刻の文字列（"たった今"、"3分前"、"2時間前"、"3日前"、"12月23日"）
     */
    public String toRelativeTime() {
        Duration duration = Duration.between(timestamp, Instant.now());

        if (duration.toMinutes() < 1) {
            return "たった今";
        } else if (duration.toMinutes() < 60) {
            return duration.toMinutes() + "分前";
        } else if (duration.toHours() < 24) {
            return duration.toHours() + "時間前";
        } else if (duration.toDays() < 7) {
            return duration.toDays() + "日前";
        } else {
            return formatAsDate();
        }
    }

    /**
     * 絶対時刻表示
     *
     * @return 絶対時刻の文字列（"2025年12月23日 14:30"）
     */
    public String toAbsoluteTime() {
        ZonedDateTime zonedDateTime = timestamp.atZone(ZoneId.systemDefault());
        return zonedDateTime.format(DATETIME_FORMATTER);
    }

    /**
     * 日付形式で表示
     *
     * @return 日付の文字列（"12月23日"）
     */
    private String formatAsDate() {
        ZonedDateTime zonedDateTime = timestamp.atZone(ZoneId.systemDefault());
        return zonedDateTime.format(DATE_FORMATTER);
    }

    /**
     * 元のタイムスタンプを取得
     *
     * @return Instantタイムスタンプ
     */
    public Instant getTimestamp() {
        return timestamp;
    }
}
