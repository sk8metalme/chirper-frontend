package com.chirper.frontend.domain.model;

import java.util.List;
import java.util.Objects;

/**
 * タイムライン表示用ViewModel
 *
 * ツイートリスト、ページング情報、次ページ判定ロジックを持つ
 */
public class TimelineViewModel {
    private final List<TweetViewModel> tweets;
    private final int currentPage;
    private final int totalPages;
    private final boolean hasNextPage;

    /**
     * TimelineViewModelを構築する
     *
     * @param tweets ツイートリスト（nullは不可）
     * @param currentPage 現在のページ番号（0以上）
     * @param totalPages 総ページ数（0以上）
     * @param hasNextPage 次のページが存在するか（現在は未使用だが、将来的な拡張のため保持）
     * @throws NullPointerException tweetsがnullの場合
     * @throws IllegalArgumentException currentPageまたはtotalPagesが負の場合
     */
    public TimelineViewModel(List<TweetViewModel> tweets, int currentPage, int totalPages, boolean hasNextPage) {
        Objects.requireNonNull(tweets, "ツイートリストはnullにできません");

        if (currentPage < 0) {
            throw new IllegalArgumentException("現在のページ番号は0以上である必要があります");
        }

        if (totalPages < 0) {
            throw new IllegalArgumentException("総ページ数は0以上である必要があります");
        }

        this.tweets = tweets;
        this.currentPage = currentPage;
        this.totalPages = totalPages;
        this.hasNextPage = hasNextPage;
    }

    /**
     * 次のページが存在するか判定する
     *
     * @return 次のページが存在する場合true、そうでない場合false
     */
    public boolean hasNextPage() {
        return currentPage < totalPages - 1;
    }

    /**
     * タイムラインが空か判定する
     *
     * @return ツイートリストが空の場合true、そうでない場合false
     */
    public boolean isEmpty() {
        return tweets.isEmpty();
    }

    // Getters
    public List<TweetViewModel> getTweets() {
        return tweets;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public int getTotalPages() {
        return totalPages;
    }
}
