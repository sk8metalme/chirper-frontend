package com.chirper.frontend.application.usecase;

import com.chirper.frontend.application.exception.UnauthorizedException;
import com.chirper.frontend.domain.repository.IBackendApiRepository;
import com.chirper.frontend.infrastructure.session.JwtSessionManager;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

/**
 * ツイート削除ユースケース
 */
@Service
public class DeleteTweetUseCase {

    private final IBackendApiRepository apiRepository;
    private final JwtSessionManager sessionManager;

    public DeleteTweetUseCase(
            IBackendApiRepository apiRepository,
            JwtSessionManager sessionManager
    ) {
        this.apiRepository = apiRepository;
        this.sessionManager = sessionManager;
    }

    /**
     * ツイート削除を実行
     *
     * @param request HTTPリクエスト
     * @param tweetId ツイートID
     * @throws UnauthorizedException 認証エラー
     */
    public void execute(HttpServletRequest request, String tweetId) {
        // 1. JWTトークン取得
        String jwtToken = sessionManager.getJwtToken(request);
        if (jwtToken == null || jwtToken.isBlank()) {
            throw new UnauthorizedException("ログインが必要です");
        }

        // 2. Backend APIでツイート削除
        apiRepository.deleteTweet(jwtToken, tweetId);
    }
}
