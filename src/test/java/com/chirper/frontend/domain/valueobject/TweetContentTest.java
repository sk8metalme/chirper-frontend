package com.chirper.frontend.domain.valueobject;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TweetContentTest {

    @Test
    void constructor_有効なツイート本文の場合はインスタンスを作成できる() {
        // Act & Assert
        assertDoesNotThrow(() -> {
            new TweetContent("有効なツイート本文です");
        });
    }

    @Test
    void constructor_nullの場合は例外をスローする() {
        // Act & Assert
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            new TweetContent(null);
        });
        assertEquals("ツイート本文は必須です", ex.getMessage());
    }

    @Test
    void constructor_空文字列の場合は例外をスローする() {
        // Act & Assert
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            new TweetContent("");
        });
        assertEquals("ツイート本文は必須です", ex.getMessage());
    }

    @Test
    void constructor_空白のみの場合は例外をスローする() {
        // Act & Assert
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            new TweetContent("   ");
        });
        assertEquals("ツイート本文は必須です", ex.getMessage());
    }

    @Test
    void constructor_281文字以上の場合は例外をスローする() {
        // Arrange: 281文字の文字列を生成
        String longText = "a".repeat(281);

        // Act & Assert
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            new TweetContent(longText);
        });
        assertEquals("ツイートは280文字以内である必要があります", ex.getMessage());
    }

    @Test
    void constructor_280文字の場合はインスタンスを作成できる() {
        // Arrange: 280文字の文字列を生成
        String text280 = "a".repeat(280);

        // Act & Assert
        assertDoesNotThrow(() -> {
            new TweetContent(text280);
        });
    }

    @Test
    void toHighlightedHtml_メンションを含むツイートの場合はリンクに変換される() {
        // Arrange
        TweetContent content = new TweetContent("こんにちは @user1 さん！");

        // Act
        String html = content.toHighlightedHtml();

        // Assert
        assertTrue(html.contains("<a href=\"/profile/user1\" class=\"mention\">@user1</a>"),
            "@user1がリンクに変換されている");
    }

    @Test
    void toHighlightedHtml_ハッシュタグを含むツイートの場合はハイライトされる() {
        // Arrange
        TweetContent content = new TweetContent("今日は #晴れ です");

        // Act
        String html = content.toHighlightedHtml();

        // Assert
        assertTrue(html.contains("<span class=\"hashtag\">#晴れ</span>"),
            "#晴れがハイライトされている");
    }

    @Test
    void toHighlightedHtml_URLを含むツイートの場合はリンクに変換される() {
        // Arrange
        TweetContent content = new TweetContent("詳細はこちら https://example.com");

        // Act
        String html = content.toHighlightedHtml();

        // Assert
        assertTrue(html.contains("<a href=\"https://example.com\" target=\"_blank\" rel=\"noopener\">https://example.com</a>"),
            "URLがリンクに変換されている");
    }

    @Test
    void toHighlightedHtml_複数の要素を含むツイートの場合はすべて変換される() {
        // Arrange
        TweetContent content = new TweetContent("@user1 #こんにちは https://example.com を確認してください");

        // Act
        String html = content.toHighlightedHtml();

        // Assert
        assertTrue(html.contains("@user1"), "メンションが含まれている");
        assertTrue(html.contains("#こんにちは"), "ハッシュタグが含まれている");
        assertTrue(html.contains("https://example.com"), "URLが含まれている");
    }

    @Test
    void getRawText_元のテキストを返す() {
        // Arrange
        String originalText = "テスト用のツイート本文";
        TweetContent content = new TweetContent(originalText);

        // Act & Assert
        assertEquals(originalText, content.getRawText());
    }
}
