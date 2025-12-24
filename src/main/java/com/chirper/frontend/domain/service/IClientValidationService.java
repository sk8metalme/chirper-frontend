package com.chirper.frontend.domain.service;

import com.chirper.frontend.domain.valueobject.ValidationResult;

/**
 * クライアント側バリデーションサービスインターフェース
 *
 * 各種フォームのバリデーションロジックを提供する
 */
public interface IClientValidationService {

    /**
     * ログインフォームをバリデートする
     *
     * @param username ユーザー名
     * @param password パスワード
     * @return バリデーション結果
     */
    ValidationResult validateLoginForm(String username, String password);

    /**
     * 登録フォームをバリデートする
     *
     * @param username ユーザー名
     * @param email メールアドレス
     * @param password パスワード
     * @param passwordConfirm パスワード確認
     * @return バリデーション結果
     */
    ValidationResult validateRegistrationForm(String username, String email, String password, String passwordConfirm);

    /**
     * ツイート投稿フォームをバリデートする
     *
     * @param content ツイート本文
     * @return バリデーション結果
     */
    ValidationResult validateTweetForm(String content);

    /**
     * プロフィール編集フォームをバリデートする
     *
     * @param displayName 表示名
     * @param bio 自己紹介
     * @param avatarUrl アバターURL
     * @return バリデーション結果
     */
    ValidationResult validateProfileEditForm(String displayName, String bio, String avatarUrl);
}
