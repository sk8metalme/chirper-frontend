package com.chirper.frontend.domain.model;

import com.chirper.frontend.domain.valueobject.TweetContent;
import com.chirper.frontend.domain.valueobject.DisplayTimestamp;

import java.util.Objects;

/**
 * ツイート表示用ViewModel
 *
 * ツイート情報、削除可否判定、いいね/リツイート状態判定ロジックを持つ
 */
public class TweetViewModel {
    private final String tweetId;
    private final String userId;
    private final String username;
    private final String displayName;
    private final String avatarUrl;
    private final TweetContent content;
    private final DisplayTimestamp timestamp;
    private final int likesCount;
    private final int retweetsCount;
    private final boolean likedByCurrentUser;
    private final boolean retweetedByCurrentUser;

    /**
     * TweetViewModelを構築する
     *
     * @param tweetId ツイートID（必須）
     * @param userId ユーザーID（必須）
     * @param username ユーザー名
     * @param displayName 表示名
     * @param avatarUrl アバターURL
     * @param content ツイート本文
     * @param timestamp 投稿日時
     * @param likesCount いいね数（0以上）
     * @param retweetsCount リツイート数（0以上）
     * @param likedByCurrentUser 現在のユーザーがいいね済みか
     * @param retweetedByCurrentUser 現在のユーザーがリツイート済みか
     * @throws NullPointerException tweetIdまたはuserIdがnullの場合
     * @throws IllegalArgumentException likesCountまたはretweetsCountが負の場合
     */
    public TweetViewModel(
            String tweetId,
            String userId,
            String username,
            String displayName,
            String avatarUrl,
            TweetContent content,
            DisplayTimestamp timestamp,
            int likesCount,
            int retweetsCount,
            boolean likedByCurrentUser,
            boolean retweetedByCurrentUser
    ) {
        Objects.requireNonNull(tweetId, "ツイートIDはnullにできません");
        Objects.requireNonNull(userId, "ユーザーIDはnullにできません");

        if (likesCount < 0) {
            throw new IllegalArgumentException("いいね数は0以上である必要があります");
        }

        if (retweetsCount < 0) {
            throw new IllegalArgumentException("リツイート数は0以上である必要があります");
        }

        this.tweetId = tweetId;
        this.userId = userId;
        this.username = username;
        this.displayName = displayName;
        this.avatarUrl = avatarUrl;
        this.content = content;
        this.timestamp = timestamp;
        this.likesCount = likesCount;
        this.retweetsCount = retweetsCount;
        this.likedByCurrentUser = likedByCurrentUser;
        this.retweetedByCurrentUser = retweetedByCurrentUser;
    }

    /**
     * ツイート削除可能か判定する（投稿者のみ削除可能）
     *
     * @param currentUserId 現在のユーザーID
     * @return 投稿者と同じユーザーIDの場合true、そうでない場合false
     */
    public boolean canDelete(String currentUserId) {
        if (currentUserId == null) {
            return false;
        }
        return userId.equals(currentUserId);
    }

    /**
     * いいね済みか判定する
     *
     * @return いいね済みの場合true、そうでない場合false
     */
    public boolean isLiked() {
        return likedByCurrentUser;
    }

    /**
     * リツイート済みか判定する
     *
     * @return リツイート済みの場合true、そうでない場合false
     */
    public boolean isRetweeted() {
        return retweetedByCurrentUser;
    }

    // Getters
    public String getTweetId() {
        return tweetId;
    }

    public String getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public TweetContent getContent() {
        return content;
    }

    public DisplayTimestamp getTimestamp() {
        return timestamp;
    }

    public int getLikesCount() {
        return likesCount;
    }

    public int getRetweetsCount() {
        return retweetsCount;
    }
}
