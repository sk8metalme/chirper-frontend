package com.chirper.frontend.presentation.form;

import jakarta.validation.constraints.Size;

/**
 * プロフィール編集フォーム
 */
public record ProfileForm(
        @Size(max = 50, message = "表示名は50文字以内で入力してください")
        String displayName,

        @Size(max = 160, message = "自己紹介は160文字以内で入力してください")
        String bio,

        // アバターURLは任意（空文字列を許可）
        // クライアントサイド（HTML type="url"）でURL形式を検証
        @Size(max = 500, message = "URLは500文字以内で入力してください")
        String avatarUrl
) {
}
