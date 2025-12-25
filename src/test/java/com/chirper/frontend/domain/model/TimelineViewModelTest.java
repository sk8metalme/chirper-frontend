package com.chirper.frontend.domain.model;

import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class TimelineViewModelTest {

    @Test
    void hasNextPage_現在のページが最終ページより前の場合はtrueを返す() {
        // Arrange
        TimelineViewModel viewModel = new TimelineViewModel(
            Collections.emptyList(),
            0,  // currentPage
            2   // totalPages
        );

        // Act & Assert
        assertTrue(viewModel.hasNextPage(), "currentPage=0, totalPages=2の場合、次のページが存在する");
    }

    @Test
    void hasNextPage_現在のページが最終ページの場合はfalseを返す() {
        // Arrange
        TimelineViewModel viewModel = new TimelineViewModel(
            Collections.emptyList(),
            1,  // currentPage
            2   // totalPages
        );

        // Act & Assert
        assertFalse(viewModel.hasNextPage(), "currentPage=1, totalPages=2の場合、次のページは存在しない");
    }

    @Test
    void hasNextPage_単一ページの場合はfalseを返す() {
        // Arrange
        TimelineViewModel viewModel = new TimelineViewModel(
            Collections.emptyList(),
            0,  // currentPage
            1   // totalPages
        );

        // Act & Assert
        assertFalse(viewModel.hasNextPage(), "currentPage=0, totalPages=1の場合、次のページは存在しない");
    }

    @Test
    void isEmpty_ツイートリストが空の場合はtrueを返す() {
        // Arrange
        TimelineViewModel viewModel = new TimelineViewModel(
            Collections.emptyList(),
            0,
            1
        );

        // Act & Assert
        assertTrue(viewModel.isEmpty(), "ツイートリストが空の場合、isEmptyはtrueを返す");
    }

    @Test
    void isEmpty_ツイートリストにデータがある場合はfalseを返す() {
        // Arrange: 1つのツイートを持つリストを作成（ダミーデータ）
        List<TweetViewModel> tweets = List.of(
            createDummyTweet()
        );

        TimelineViewModel viewModel = new TimelineViewModel(
            tweets,
            0,
            1
        );

        // Act & Assert
        assertFalse(viewModel.isEmpty(), "ツイートリストにデータがある場合、isEmptyはfalseを返す");
    }

    @Test
    void constructor_ツイートリストがnullの場合は例外をスローする() {
        // Act & Assert
        assertThrows(NullPointerException.class, () -> {
            new TimelineViewModel(null, 0, 1);
        }, "ツイートリストがnullの場合、NullPointerExceptionをスローする");
    }

    @Test
    void constructor_負のページ番号の場合は例外をスローする() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            new TimelineViewModel(Collections.emptyList(), -1, 1);
        }, "負のページ番号の場合、IllegalArgumentExceptionをスローする");
    }

    @Test
    void constructor_負の総ページ数の場合は例外をスローする() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            new TimelineViewModel(Collections.emptyList(), 0, -1);
        }, "負の総ページ数の場合、IllegalArgumentExceptionをスローする");
    }

    /**
     * ダミーのTweetViewModelを生成するヘルパーメソッド
     */
    private TweetViewModel createDummyTweet() {
        // 最小限のダミーデータでTweetViewModelを作成
        return new TweetViewModel(
            "tweet1",
            "user1",
            "username",
            "Display Name",
            "https://example.com/avatar.jpg",
            null,  // TweetContent
            null,  // DisplayTimestamp
            0,     // likesCount
            0,     // retweetsCount
            false, // likedByCurrentUser
            false  // retweetedByCurrentUser
        );
    }
}
