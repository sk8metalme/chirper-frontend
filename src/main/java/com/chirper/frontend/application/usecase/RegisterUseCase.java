package com.chirper.frontend.application.usecase;

import com.chirper.frontend.application.dto.RegisterResponse;
import com.chirper.frontend.application.exception.ValidationException;
import com.chirper.frontend.domain.repository.IBackendApiRepository;
import com.chirper.frontend.domain.service.IClientValidationService;
import com.chirper.frontend.domain.valueobject.ValidationResult;
import org.springframework.stereotype.Service;

/**
 * ユーザー登録ユースケース
 */
@Service
public class RegisterUseCase {

    private final IBackendApiRepository apiRepository;
    private final IClientValidationService validationService;

    public RegisterUseCase(
            IBackendApiRepository apiRepository,
            IClientValidationService validationService
    ) {
        this.apiRepository = apiRepository;
        this.validationService = validationService;
    }

    /**
     * ユーザー登録処理を実行
     *
     * @param username        ユーザー名
     * @param email          メールアドレス
     * @param password       パスワード
     * @param passwordConfirm パスワード確認
     * @return 登録レスポンス
     * @throws ValidationException バリデーションエラー
     */
    public RegisterResponse execute(String username, String email, String password, String passwordConfirm) {
        // 1. クライアント側バリデーション
        ValidationResult validation = validationService.validateRegistrationForm(
                username, email, password, passwordConfirm
        );
        if (!validation.isValid()) {
            throw new ValidationException(validation.getErrors());
        }

        // 2. Backend APIで登録
        return apiRepository.register(username, email, password);
    }
}
