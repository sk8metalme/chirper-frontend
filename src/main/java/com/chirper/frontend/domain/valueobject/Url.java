package com.chirper.frontend.domain.valueobject;

/**
 * URL を表す値オブジェクト
 */
public class Url {
    private final String url;
    private final int start;
    private final int end;

    public Url(String url, int start, int end) {
        if (url == null || url.isBlank()) {
            throw new IllegalArgumentException("URL cannot be null or empty");
        }
        if (start < 0) {
            throw new IllegalArgumentException("URL start position cannot be negative");
        }
        if (end < 0) {
            throw new IllegalArgumentException("URL end position cannot be negative");
        }
        if (start >= end) {
            throw new IllegalArgumentException("URL start position must be less than end position");
        }
        this.url = url;
        this.start = start;
        this.end = end;
    }

    public String getUrl() {
        return url;
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }
}
