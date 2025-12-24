package com.chirper.frontend.domain.repository;

import com.chirper.frontend.application.dto.*;

/**
 * Backend APIリポジトリインターフェース
 *
 * Backend Serviceが提供するREST APIとの通信を抽象化する
 */
public interface IBackendApiRepository {

    // 認証API

    /**
     * ログイン
     *
     * @param username ユーザー名
     * @param password パスワード
     * @return ログインレスポンス（JWTトークン含む）
     */
    LoginResponse login(String username, String password);

    /**
     * 新規ユーザー登録
     *
     * @param username ユーザー名
     * @param email メールアドレス
     * @param password パスワード
     * @return 登録レスポンス
     */
    RegisterResponse register(String username, String email, String password);

    // タイムラインAPI

    /**
     * タイムライン取得
     *
     * @param jwtToken JWTトークン
     * @param page ページ番号
     * @param size ページサイズ
     * @return タイムラインDTO
     */
    TimelineDto getTimeline(String jwtToken, int page, int size);

    // ツイートAPI

    /**
     * ツイート投稿
     *
     * @param jwtToken JWTトークン
     * @param content ツイート本文
     * @return 作成されたツイートDTO
     */
    TweetDto createTweet(String jwtToken, String content);

    /**
     * ツイート取得
     *
     * @param tweetId ツイートID
     * @return ツイートDTO
     */
    TweetDto getTweet(String tweetId);

    /**
     * ツイート削除
     *
     * @param jwtToken JWTトークン
     * @param tweetId ツイートID
     */
    void deleteTweet(String jwtToken, String tweetId);

    // ユーザーAPI

    /**
     * ユーザープロフィール取得
     *
     * @param username ユーザー名
     * @return ユーザープロフィールDTO
     */
    UserProfileDto getUserProfile(String username);

    /**
     * プロフィール更新
     *
     * @param jwtToken JWTトークン
     * @param displayName 表示名
     * @param bio 自己紹介
     * @param avatarUrl アバターURL
     * @return 更新されたユーザープロフィールDTO
     */
    UserProfileDto updateProfile(String jwtToken, String displayName, String bio, String avatarUrl);

    // ソーシャルAPI

    /**
     * ユーザーをフォロー
     *
     * @param jwtToken JWTトークン
     * @param userId フォロー対象のユーザーID
     */
    void followUser(String jwtToken, String userId);

    /**
     * ユーザーのフォローを解除
     *
     * @param jwtToken JWTトークン
     * @param userId フォロー解除対象のユーザーID
     */
    void unfollowUser(String jwtToken, String userId);

    /**
     * ツイートにいいね
     *
     * @param jwtToken JWTトークン
     * @param tweetId ツイートID
     */
    void likeTweet(String jwtToken, String tweetId);

    /**
     * ツイートのいいねを取り消し
     *
     * @param jwtToken JWTトークン
     * @param tweetId ツイートID
     */
    void unlikeTweet(String jwtToken, String tweetId);

    /**
     * リツイート
     *
     * @param jwtToken JWTトークン
     * @param tweetId ツイートID
     */
    void retweet(String jwtToken, String tweetId);
}
