package com.chirper.frontend.domain.valueobject;

import java.util.Objects;

/**
 * フィールドエラーを表す値オブジェクト
 */
public class FieldError {
    private final String field;
    private final String message;

    /**
     * FieldErrorを構築する
     *
     * @param field フィールド名（必須）
     * @param message エラーメッセージ（必須）
     * @throws NullPointerException fieldまたはmessageがnullの場合
     */
    public FieldError(String field, String message) {
        Objects.requireNonNull(field, "フィールド名はnullにできません");
        Objects.requireNonNull(message, "エラーメッセージはnullにできません");
        this.field = field;
        this.message = message;
    }

    public String getField() {
        return field;
    }

    public String getMessage() {
        return message;
    }
}
