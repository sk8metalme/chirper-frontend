package com.chirper.frontend.application.usecase;

import com.chirper.frontend.application.dto.UserProfileDto;
import com.chirper.frontend.application.exception.UnauthorizedException;
import com.chirper.frontend.application.exception.ValidationException;
import com.chirper.frontend.domain.repository.IBackendApiRepository;
import com.chirper.frontend.domain.service.IClientValidationService;
import com.chirper.frontend.domain.valueobject.ValidationResult;
import com.chirper.frontend.infrastructure.session.JwtSessionManager;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

/**
 * プロフィール更新ユースケース
 */
@Service
public class UpdateProfileUseCase {

    private final IBackendApiRepository apiRepository;
    private final IClientValidationService validationService;
    private final JwtSessionManager sessionManager;

    public UpdateProfileUseCase(
            IBackendApiRepository apiRepository,
            IClientValidationService validationService,
            JwtSessionManager sessionManager
    ) {
        this.apiRepository = apiRepository;
        this.validationService = validationService;
        this.sessionManager = sessionManager;
    }

    /**
     * プロフィール更新を実行
     *
     * @param request     HTTPリクエスト
     * @param displayName 表示名
     * @param bio         自己紹介
     * @param avatarUrl   アバターURL
     * @return 更新されたユーザープロフィールDTO
     * @throws ValidationException   バリデーションエラー
     * @throws UnauthorizedException 認証エラー
     */
    public UserProfileDto execute(
            HttpServletRequest request,
            String displayName,
            String bio,
            String avatarUrl
    ) {
        // 1. クライアント側バリデーション
        ValidationResult validation = validationService.validateProfileEditForm(
                displayName, bio, avatarUrl
        );
        if (!validation.isValid()) {
            throw new ValidationException(validation.getErrors());
        }

        // 2. JWTトークン取得
        String jwtToken = sessionManager.getJwtToken(request);
        if (jwtToken == null || jwtToken.isBlank()) {
            throw new UnauthorizedException("ログインが必要です");
        }

        // 3. Backend APIでプロフィール更新
        return apiRepository.updateProfile(jwtToken, displayName, bio, avatarUrl);
    }
}
