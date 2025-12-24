package com.chirper.frontend.domain.model;

import com.chirper.frontend.domain.valueobject.TweetContent;
import com.chirper.frontend.domain.valueobject.DisplayTimestamp;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class TweetViewModelTest {

    @Test
    void canDelete_投稿者と同じユーザーIDの場合はtrueを返す() {
        // Arrange
        TweetViewModel tweet = createTweet("user1", "tweet1");

        // Act & Assert
        assertTrue(tweet.canDelete("user1"), "投稿者と同じユーザーIDの場合、削除可能");
    }

    @Test
    void canDelete_投稿者と異なるユーザーIDの場合はfalseを返す() {
        // Arrange
        TweetViewModel tweet = createTweet("user1", "tweet1");

        // Act & Assert
        assertFalse(tweet.canDelete("user2"), "投稿者と異なるユーザーIDの場合、削除不可");
    }

    @Test
    void canDelete_ユーザーIDがnullの場合はfalseを返す() {
        // Arrange
        TweetViewModel tweet = createTweet("user1", "tweet1");

        // Act & Assert
        assertFalse(tweet.canDelete(null), "ユーザーIDがnullの場合、削除不可");
    }

    @Test
    void isLiked_いいね済みフラグがtrueの場合はtrueを返す() {
        // Arrange
        TweetViewModel tweet = createTweetWithSocialStatus(true, false);

        // Act & Assert
        assertTrue(tweet.isLiked(), "いいね済みフラグがtrueの場合、isLikedはtrueを返す");
    }

    @Test
    void isLiked_いいね済みフラグがfalseの場合はfalseを返す() {
        // Arrange
        TweetViewModel tweet = createTweetWithSocialStatus(false, false);

        // Act & Assert
        assertFalse(tweet.isLiked(), "いいね済みフラグがfalseの場合、isLikedはfalseを返す");
    }

    @Test
    void isRetweeted_リツイート済みフラグがtrueの場合はtrueを返す() {
        // Arrange
        TweetViewModel tweet = createTweetWithSocialStatus(false, true);

        // Act & Assert
        assertTrue(tweet.isRetweeted(), "リツイート済みフラグがtrueの場合、isRetweetedはtrueを返す");
    }

    @Test
    void isRetweeted_リツイート済みフラグがfalseの場合はfalseを返す() {
        // Arrange
        TweetViewModel tweet = createTweetWithSocialStatus(false, false);

        // Act & Assert
        assertFalse(tweet.isRetweeted(), "リツイート済みフラグがfalseの場合、isRetweetedはfalseを返す");
    }

    @Test
    void constructor_必須フィールドがnullの場合は例外をスローする() {
        // Act & Assert: tweetIdがnull
        assertThrows(NullPointerException.class, () -> {
            new TweetViewModel(
                null, "user1", "username", "displayName", "avatar",
                null, null, 0, 0, false, false
            );
        });

        // Act & Assert: userIdがnull
        assertThrows(NullPointerException.class, () -> {
            new TweetViewModel(
                "tweet1", null, "username", "displayName", "avatar",
                null, null, 0, 0, false, false
            );
        });
    }

    @Test
    void constructor_負のカウント値の場合は例外をスローする() {
        // Act & Assert: 負のいいね数
        assertThrows(IllegalArgumentException.class, () -> {
            new TweetViewModel(
                "tweet1", "user1", "username", "displayName", "avatar",
                null, null, -1, 0, false, false
            );
        });

        // Act & Assert: 負のリツイート数
        assertThrows(IllegalArgumentException.class, () -> {
            new TweetViewModel(
                "tweet1", "user1", "username", "displayName", "avatar",
                null, null, 0, -1, false, false
            );
        });
    }

    /**
     * 基本的なTweetViewModelを生成するヘルパーメソッド
     */
    private TweetViewModel createTweet(String userId, String tweetId) {
        return new TweetViewModel(
            tweetId,
            userId,
            "username",
            "Display Name",
            "https://example.com/avatar.jpg",
            null,  // TweetContent（後で実装）
            null,  // DisplayTimestamp（後で実装）
            0,     // likesCount
            0,     // retweetsCount
            false, // likedByCurrentUser
            false  // retweetedByCurrentUser
        );
    }

    /**
     * ソーシャル状態を指定してTweetViewModelを生成するヘルパーメソッド
     */
    private TweetViewModel createTweetWithSocialStatus(boolean liked, boolean retweeted) {
        return new TweetViewModel(
            "tweet1",
            "user1",
            "username",
            "Display Name",
            "https://example.com/avatar.jpg",
            null,  // TweetContent（後で実装）
            null,  // DisplayTimestamp（後で実装）
            0,     // likesCount
            0,     // retweetsCount
            liked,
            retweeted
        );
    }
}
