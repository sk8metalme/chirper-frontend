package com.chirper.frontend.presentation.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * ログインフォーム
 */
public record LoginForm(
        @NotBlank(message = "ユーザー名を入力してください")
        @Size(min = 1, max = 50, message = "ユーザー名は1〜50文字で入力してください")
        String username,

        @NotBlank(message = "パスワードを入力してください")
        @Size(min = 8, max = 100, message = "パスワードは8〜100文字で入力してください")
        String password
) {
}
