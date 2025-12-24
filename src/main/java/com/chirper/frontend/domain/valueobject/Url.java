package com.chirper.frontend.domain.valueobject;

/**
 * URL を表す値オブジェクト
 */
public class Url {
    private final String url;
    private final int start;
    private final int end;

    public Url(String url, int start, int end) {
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
