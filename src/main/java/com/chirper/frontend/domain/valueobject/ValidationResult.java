package com.chirper.frontend.domain.valueobject;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * バリデーション結果を表す値オブジェクト
 */
public class ValidationResult {
    private final boolean isValid;
    private final List<FieldError> errors;

    /**
     * ValidationResultを構築する（privateコンストラクタ）
     *
     * @param isValid 有効かどうか
     * @param errors エラーリスト
     */
    private ValidationResult(boolean isValid, List<FieldError> errors) {
        this.isValid = isValid;
        this.errors = errors;
    }

    /**
     * 有効なValidationResultを作成する
     *
     * @return エラーがないValidationResult
     */
    public static ValidationResult valid() {
        return new ValidationResult(true, Collections.emptyList());
    }

    /**
     * 無効なValidationResultを作成する
     *
     * @param errors エラーリスト（必須）
     * @return エラーを含むValidationResult
     * @throws NullPointerException errorsがnullの場合
     */
    public static ValidationResult invalid(List<FieldError> errors) {
        Objects.requireNonNull(errors, "エラーリストはnullにできません");
        return new ValidationResult(false, errors);
    }

    public boolean isValid() {
        return isValid;
    }

    public List<FieldError> getErrors() {
        return errors;
    }
}
