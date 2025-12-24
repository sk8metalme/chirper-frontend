package com.chirper.frontend.presentation.form;

import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.URL;

/**
 * プロフィール編集フォーム
 */
public record ProfileForm(
        @Size(max = 50, message = "表示名は50文字以内で入力してください")
        String displayName,

        @Size(max = 160, message = "自己紹介は160文字以内で入力してください")
        String bio,

        @URL(message = "有効なURLを入力してください")
        String avatarUrl
) {
}
