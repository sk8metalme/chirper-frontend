package com.chirper.frontend.domain.valueobject;

/**
 * @mention を表す値オブジェクト
 */
public class Mention {
    private final String username;
    private final int start;
    private final int end;

    public Mention(String username, int start, int end) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Mention username cannot be null or empty");
        }
        if (start < 0) {
            throw new IllegalArgumentException("Mention start position cannot be negative");
        }
        if (end < 0) {
            throw new IllegalArgumentException("Mention end position cannot be negative");
        }
        if (start >= end) {
            throw new IllegalArgumentException("Mention start position must be less than end position");
        }
        this.username = username;
        this.start = start;
        this.end = end;
    }

    public String getUsername() {
        return username;
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }
}
