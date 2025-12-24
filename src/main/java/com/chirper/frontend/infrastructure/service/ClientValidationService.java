package com.chirper.frontend.infrastructure.service;

import com.chirper.frontend.domain.service.IClientValidationService;
import com.chirper.frontend.domain.valueobject.FieldError;
import com.chirper.frontend.domain.valueobject.TweetContent;
import com.chirper.frontend.domain.valueobject.ValidationResult;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * クライアント側バリデーションサービス実装
 */
@Service
public class ClientValidationService implements IClientValidationService {

    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_]{3,20}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    private static final int MIN_PASSWORD_LENGTH = 8;
    private static final int MAX_BIO_LENGTH = 160;

    @Override
    public ValidationResult validateLoginForm(String username, String password) {
        List<FieldError> errors = new ArrayList<>();

        errors.addAll(validateUsernameField(username));
        errors.addAll(validatePasswordField(password));

        return errors.isEmpty() ? ValidationResult.valid() : ValidationResult.invalid(errors);
    }

    @Override
    public ValidationResult validateRegistrationForm(String username, String email, String password, String passwordConfirm) {
        List<FieldError> errors = new ArrayList<>();

        errors.addAll(validateUsernameField(username));
        errors.addAll(validateEmailField(email));
        errors.addAll(validatePasswordField(password));

        if (password != null && !password.equals(passwordConfirm)) {
            errors.add(new FieldError("passwordConfirm", "パスワードが一致しません"));
        }

        return errors.isEmpty() ? ValidationResult.valid() : ValidationResult.invalid(errors);
    }

    @Override
    public ValidationResult validateTweetForm(String content) {
        List<FieldError> errors = new ArrayList<>();

        if (content == null || content.isBlank()) {
            errors.add(new FieldError("content", "ツイート内容は必須です"));
            return ValidationResult.invalid(errors);
        }

        try {
            TweetContent tweetContent = new TweetContent(content);
            // TweetContentの内部バリデーションは既に完了している
        } catch (IllegalArgumentException e) {
            errors.add(new FieldError("content", e.getMessage()));
        }

        return errors.isEmpty() ? ValidationResult.valid() : ValidationResult.invalid(errors);
    }

    @Override
    public ValidationResult validateProfileEditForm(String displayName, String bio, String avatarUrl) {
        List<FieldError> errors = new ArrayList<>();

        if (displayName != null && !displayName.isBlank()) {
            if (displayName.length() > 50) {
                errors.add(new FieldError("displayName", "表示名は50文字以下である必要があります"));
            }
        }

        if (bio != null && !bio.isBlank()) {
            if (bio.length() > MAX_BIO_LENGTH) {
                errors.add(new FieldError("bio",
                        String.format("自己紹介は%d文字以下である必要があります", MAX_BIO_LENGTH)));
            }
        }

        if (avatarUrl != null && !avatarUrl.isBlank()) {
            if (!avatarUrl.startsWith("http://") && !avatarUrl.startsWith("https://")) {
                errors.add(new FieldError("avatarUrl", "有効なURLを入力してください"));
            }
        }

        return errors.isEmpty() ? ValidationResult.valid() : ValidationResult.invalid(errors);
    }

    /**
     * ユーザー名フィールドのバリデーション
     */
    private List<FieldError> validateUsernameField(String username) {
        List<FieldError> errors = new ArrayList<>();

        if (username == null || username.isBlank()) {
            errors.add(new FieldError("username", "ユーザー名は必須です"));
            return errors;
        }

        if (username.length() < 3) {
            errors.add(new FieldError("username", "ユーザー名は3文字以上である必要があります"));
        }

        if (username.length() > 20) {
            errors.add(new FieldError("username", "ユーザー名は20文字以下である必要があります"));
        }

        if (!USERNAME_PATTERN.matcher(username).matches()) {
            errors.add(new FieldError("username", "ユーザー名は英数字とアンダースコアのみ使用できます"));
        }

        return errors;
    }

    /**
     * メールアドレスフィールドのバリデーション
     */
    private List<FieldError> validateEmailField(String email) {
        List<FieldError> errors = new ArrayList<>();

        if (email == null || email.isBlank()) {
            errors.add(new FieldError("email", "メールアドレスは必須です"));
            return errors;
        }

        if (!EMAIL_PATTERN.matcher(email).matches()) {
            errors.add(new FieldError("email", "有効なメールアドレスを入力してください"));
        }

        return errors;
    }

    /**
     * パスワードフィールドのバリデーション
     */
    private List<FieldError> validatePasswordField(String password) {
        List<FieldError> errors = new ArrayList<>();

        if (password == null || password.isEmpty()) {
            errors.add(new FieldError("password", "パスワードは必須です"));
            return errors;
        }

        if (password.length() < MIN_PASSWORD_LENGTH) {
            errors.add(new FieldError("password",
                    String.format("パスワードは%d文字以上である必要があります", MIN_PASSWORD_LENGTH)));
        }

        return errors;
    }
}
