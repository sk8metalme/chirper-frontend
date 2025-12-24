package com.chirper.frontend.domain.valueobject;

/**
 * #hashtag を表す値オブジェクト
 */
public class Hashtag {
    private final String tag;
    private final int start;
    private final int end;

    public Hashtag(String tag, int start, int end) {
        if (tag == null || tag.isBlank()) {
            throw new IllegalArgumentException("Hashtag tag cannot be null or empty");
        }
        if (start < 0) {
            throw new IllegalArgumentException("Hashtag start position cannot be negative");
        }
        if (end < 0) {
            throw new IllegalArgumentException("Hashtag end position cannot be negative");
        }
        if (start >= end) {
            throw new IllegalArgumentException("Hashtag start position must be less than end position");
        }
        this.tag = tag;
        this.start = start;
        this.end = end;
    }

    public String getTag() {
        return tag;
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }
}
