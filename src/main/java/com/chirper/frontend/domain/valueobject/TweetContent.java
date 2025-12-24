package com.chirper.frontend.domain.valueobject;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * ツイート本文を表す値オブジェクト
 *
 * 280文字バリデーション、@mention/@hashtag/URL抽出、HTML変換ロジックを持つ
 */
public class TweetContent {
    private static final int MAX_LENGTH = 280;
    private static final Pattern MENTION_PATTERN = Pattern.compile("@([a-zA-Z0-9_]+)");
    private static final Pattern HASHTAG_PATTERN = Pattern.compile("#([\\p{L}\\p{N}_]+)");
    private static final Pattern URL_PATTERN = Pattern.compile("https?://[^\\s]+");

    private final String rawText;
    private final List<Mention> mentions;
    private final List<Hashtag> hashtags;
    private final List<Url> urls;

    /**
     * TweetContentを構築する
     *
     * @param text ツイート本文
     * @throws IllegalArgumentException textがnull、空、または280文字を超える場合
     */
    public TweetContent(String text) {
        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException("ツイート本文は必須です");
        }

        if (text.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("ツイートは280文字以内である必要があります");
        }

        this.rawText = text;
        this.mentions = extractMentions(text);
        this.hashtags = extractHashtags(text);
        this.urls = extractUrls(text);
    }

    /**
     * @mention抽出
     *
     * @param text ツイート本文
     * @return 抽出されたMentionのリスト
     */
    private List<Mention> extractMentions(String text) {
        List<Mention> result = new ArrayList<>();
        Matcher matcher = MENTION_PATTERN.matcher(text);
        while (matcher.find()) {
            String username = matcher.group(1);
            result.add(new Mention(username, matcher.start(), matcher.end()));
        }
        return result;
    }

    /**
     * #hashtag抽出
     *
     * @param text ツイート本文
     * @return 抽出されたHashtagのリスト
     */
    private List<Hashtag> extractHashtags(String text) {
        List<Hashtag> result = new ArrayList<>();
        Matcher matcher = HASHTAG_PATTERN.matcher(text);
        while (matcher.find()) {
            String tag = matcher.group(1);
            result.add(new Hashtag(tag, matcher.start(), matcher.end()));
        }
        return result;
    }

    /**
     * URL抽出
     *
     * @param text ツイート本文
     * @return 抽出されたUrlのリスト
     */
    private List<Url> extractUrls(String text) {
        List<Url> result = new ArrayList<>();
        Matcher matcher = URL_PATTERN.matcher(text);
        while (matcher.find()) {
            String url = matcher.group();
            result.add(new Url(url, matcher.start(), matcher.end()));
        }
        return result;
    }

    /**
     * HTML変換（ハイライト付き）
     *
     * @return ハイライト処理されたHTML文字列
     */
    public String toHighlightedHtml() {
        StringBuilder html = new StringBuilder();
        int lastIndex = 0;

        // すべての要素（mentions, hashtags, urls）を位置順にソートして処理
        List<HighlightableElement> elements = new ArrayList<>();
        mentions.forEach(m -> elements.add(new HighlightableElement(m.getStart(), m.getEnd(), m)));
        hashtags.forEach(h -> elements.add(new HighlightableElement(h.getStart(), h.getEnd(), h)));
        urls.forEach(u -> elements.add(new HighlightableElement(u.getStart(), u.getEnd(), u)));

        elements.sort((a, b) -> Integer.compare(a.start, b.start));

        for (HighlightableElement element : elements) {
            // 前回の終了位置から今回の開始位置までのテキストをエスケープして追加
            if (lastIndex < element.start) {
                html.append(escapeHtml(rawText.substring(lastIndex, element.start)));
            }

            // 要素をHTMLに変換
            if (element.object instanceof Mention) {
                Mention mention = (Mention) element.object;
                html.append("<a href=\"/profile/")
                    .append(escapeHtml(mention.getUsername()))
                    .append("\" class=\"mention\">@")
                    .append(escapeHtml(mention.getUsername()))
                    .append("</a>");
            } else if (element.object instanceof Hashtag) {
                Hashtag hashtag = (Hashtag) element.object;
                html.append("<span class=\"hashtag\">#")
                    .append(escapeHtml(hashtag.getTag()))
                    .append("</span>");
            } else if (element.object instanceof Url) {
                Url url = (Url) element.object;
                html.append("<a href=\"")
                    .append(escapeHtml(url.getUrl()))
                    .append("\" target=\"_blank\" rel=\"noopener\">")
                    .append(escapeHtml(url.getUrl()))
                    .append("</a>");
            }

            lastIndex = element.end;
        }

        // 残りのテキストをエスケープして追加
        if (lastIndex < rawText.length()) {
            html.append(escapeHtml(rawText.substring(lastIndex)));
        }

        return html.toString();
    }

    /**
     * HTMLエスケープ
     *
     * @param text エスケープ対象のテキスト
     * @return エスケープされたテキスト
     */
    private String escapeHtml(String text) {
        return text.replace("&", "&amp;")
                   .replace("<", "&lt;")
                   .replace(">", "&gt;")
                   .replace("\"", "&quot;")
                   .replace("'", "&#39;");
    }

    /**
     * 元のテキストを取得
     *
     * @return 元のツイート本文
     */
    public String getRawText() {
        return rawText;
    }

    // Getters for lists (for potential future use)
    public List<Mention> getMentions() {
        return mentions;
    }

    public List<Hashtag> getHashtags() {
        return hashtags;
    }

    public List<Url> getUrls() {
        return urls;
    }

    /**
     * ハイライト可能な要素を位置順にソートするためのヘルパークラス
     */
    private static class HighlightableElement {
        final int start;
        final int end;
        final Object object;

        HighlightableElement(int start, int end, Object object) {
            this.start = start;
            this.end = end;
            this.object = object;
        }
    }
}
