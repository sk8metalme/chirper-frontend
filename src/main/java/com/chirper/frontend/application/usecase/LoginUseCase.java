package com.chirper.frontend.application.usecase;

import com.chirper.frontend.application.dto.LoginResponse;
import com.chirper.frontend.application.exception.UnauthorizedException;
import com.chirper.frontend.application.exception.ValidationException;
import com.chirper.frontend.domain.repository.IBackendApiRepository;
import com.chirper.frontend.domain.service.IClientValidationService;
import com.chirper.frontend.domain.valueobject.ValidationResult;
import com.chirper.frontend.infrastructure.session.JwtSessionManager;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

/**
 * ログインユースケース
 */
@Service
public class LoginUseCase {

    private final IBackendApiRepository apiRepository;
    private final IClientValidationService validationService;
    private final JwtSessionManager sessionManager;

    public LoginUseCase(
            IBackendApiRepository apiRepository,
            IClientValidationService validationService,
            JwtSessionManager sessionManager
    ) {
        this.apiRepository = apiRepository;
        this.validationService = validationService;
        this.sessionManager = sessionManager;
    }

    /**
     * ログイン処理を実行
     *
     * @param request  HTTPリクエスト
     * @param username ユーザー名
     * @param password パスワード
     * @return ログインレスポンス
     * @throws ValidationException    バリデーションエラー
     * @throws UnauthorizedException  認証エラー
     */
    public LoginResponse execute(HttpServletRequest request, String username, String password) {
        // 1. クライアント側バリデーション
        ValidationResult validation = validationService.validateLoginForm(username, password);
        if (!validation.isValid()) {
            throw new ValidationException(validation.getErrors());
        }

        // 2. Backend APIでログイン
        LoginResponse response = apiRepository.login(username, password);

        // 3. セッションに保存
        sessionManager.saveJwtToken(request, response.jwtToken(), response.userId());

        return response;
    }
}
