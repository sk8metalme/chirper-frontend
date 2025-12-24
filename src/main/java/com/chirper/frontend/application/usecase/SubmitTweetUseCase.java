package com.chirper.frontend.application.usecase;

import com.chirper.frontend.application.dto.TweetDto;
import com.chirper.frontend.application.exception.UnauthorizedException;
import com.chirper.frontend.application.exception.ValidationException;
import com.chirper.frontend.domain.repository.IBackendApiRepository;
import com.chirper.frontend.domain.service.IClientValidationService;
import com.chirper.frontend.domain.valueobject.ValidationResult;
import com.chirper.frontend.infrastructure.session.JwtSessionManager;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

/**
 * ツイート投稿ユースケース
 */
@Service
public class SubmitTweetUseCase {

    private final IBackendApiRepository apiRepository;
    private final IClientValidationService validationService;
    private final JwtSessionManager sessionManager;

    public SubmitTweetUseCase(
            IBackendApiRepository apiRepository,
            IClientValidationService validationService,
            JwtSessionManager sessionManager
    ) {
        this.apiRepository = apiRepository;
        this.validationService = validationService;
        this.sessionManager = sessionManager;
    }

    /**
     * ツイート投稿を実行
     *
     * @param request HTTPリクエスト
     * @param content ツイート内容
     * @return 作成されたツイートDTO
     * @throws ValidationException    バリデーションエラー
     * @throws UnauthorizedException  認証エラー
     */
    public TweetDto execute(HttpServletRequest request, String content) {
        // 1. クライアント側バリデーション
        ValidationResult validation = validationService.validateTweetForm(content);
        if (!validation.isValid()) {
            throw new ValidationException(validation.getErrors());
        }

        // 2. JWTトークン取得
        String jwtToken = sessionManager.getJwtToken(request);
        if (jwtToken == null || jwtToken.isBlank()) {
            throw new UnauthorizedException("ログインが必要です");
        }

        // 3. Backend APIでツイート投稿
        return apiRepository.createTweet(jwtToken, content);
    }
}
