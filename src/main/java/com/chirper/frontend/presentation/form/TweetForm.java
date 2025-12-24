package com.chirper.frontend.presentation.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * ツイート投稿フォーム
 */
public record TweetForm(
        @NotBlank(message = "ツイート内容を入力してください")
        @Size(min = 1, max = 280, message = "ツイートは1〜280文字で入力してください")
        String content
) {
}
