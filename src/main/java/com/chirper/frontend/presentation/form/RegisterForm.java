package com.chirper.frontend.presentation.form;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * ユーザー登録フォーム
 */
public record RegisterForm(
        @NotBlank(message = "ユーザー名を入力してください")
        @Size(min = 3, max = 50, message = "ユーザー名は3〜50文字で入力してください")
        @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "ユーザー名は英数字とアンダースコアのみ使用できます")
        String username,

        @NotBlank(message = "メールアドレスを入力してください")
        @Email(message = "有効なメールアドレスを入力してください")
        String email,

        @NotBlank(message = "パスワードを入力してください")
        @Size(min = 8, max = 100, message = "パスワードは8〜100文字で入力してください")
        String password,

        @NotBlank(message = "パスワード(確認)を入力してください")
        String passwordConfirm
) {
}
