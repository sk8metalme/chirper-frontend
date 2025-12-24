package com.chirper.frontend.domain.model;

import java.util.List;
import java.util.Objects;

/**
 * ユーザープロフィール表示用ViewModel
 *
 * ユーザー情報、フォローボタン表示判定、プロフィール編集可否判定ロジックを持つ
 */
public class UserProfileViewModel {
    private final String userId;
    private final String username;
    private final String displayName;
    private final String bio;
    private final String avatarUrl;
    private final int followersCount;
    private final int followingCount;
    private final boolean followedByCurrentUser;
    private final boolean isCurrentUser;
    private final List<TweetViewModel> userTweets;

    /**
     * UserProfileViewModelを構築する
     *
     * @param userId ユーザーID（必須）
     * @param username ユーザー名（必須）
     * @param displayName 表示名
     * @param bio 自己紹介
     * @param avatarUrl アバターURL
     * @param followersCount フォロワー数（0以上）
     * @param followingCount フォロー数（0以上）
     * @param followedByCurrentUser 現在のユーザーがフォロー済みか
     * @param isCurrentUser 現在のユーザー自身か
     * @param userTweets ユーザーのツイートリスト
     * @throws NullPointerException userIdまたはusernameがnullの場合
     * @throws IllegalArgumentException followersCountまたはfollowingCountが負の場合
     */
    public UserProfileViewModel(
            String userId,
            String username,
            String displayName,
            String bio,
            String avatarUrl,
            int followersCount,
            int followingCount,
            boolean followedByCurrentUser,
            boolean isCurrentUser,
            List<TweetViewModel> userTweets
    ) {
        Objects.requireNonNull(userId, "ユーザーIDはnullにできません");
        Objects.requireNonNull(username, "ユーザー名はnullにできません");

        if (followersCount < 0) {
            throw new IllegalArgumentException("フォロワー数は0以上である必要があります");
        }

        if (followingCount < 0) {
            throw new IllegalArgumentException("フォロー数は0以上である必要があります");
        }

        this.userId = userId;
        this.username = username;
        this.displayName = displayName;
        this.bio = bio;
        this.avatarUrl = avatarUrl;
        this.followersCount = followersCount;
        this.followingCount = followingCount;
        this.followedByCurrentUser = followedByCurrentUser;
        this.isCurrentUser = isCurrentUser;
        this.userTweets = userTweets != null ? userTweets : List.of();
    }

    /**
     * フォローボタン表示判定（自分以外のみフォロー可能）
     *
     * @return 自分以外のプロフィールの場合true、自分のプロフィールの場合false
     */
    public boolean canFollow() {
        return !isCurrentUser;
    }

    /**
     * プロフィール編集可能か判定（自分のみ編集可能）
     *
     * @return 自分のプロフィールの場合true、そうでない場合false
     */
    public boolean canEdit() {
        return isCurrentUser;
    }

    /**
     * フォロー状態の表示文言を取得
     *
     * @return フォロー済みの場合"フォロー中"、未フォローの場合"フォローする"
     */
    public String followButtonText() {
        return followedByCurrentUser ? "フォロー中" : "フォローする";
    }

    // Getters
    public String getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getBio() {
        return bio;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public int getFollowersCount() {
        return followersCount;
    }

    public int getFollowingCount() {
        return followingCount;
    }

    public boolean isFollowedByCurrentUser() {
        return followedByCurrentUser;
    }

    public boolean isCurrentUser() {
        return isCurrentUser;
    }

    public List<TweetViewModel> getUserTweets() {
        return userTweets;
    }
}
