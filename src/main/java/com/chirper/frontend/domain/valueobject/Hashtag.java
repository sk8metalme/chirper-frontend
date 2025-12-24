package com.chirper.frontend.domain.valueobject;

/**
 * #hashtag を表す値オブジェクト
 */
public class Hashtag {
    private final String tag;
    private final int start;
    private final int end;

    public Hashtag(String tag, int start, int end) {
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
