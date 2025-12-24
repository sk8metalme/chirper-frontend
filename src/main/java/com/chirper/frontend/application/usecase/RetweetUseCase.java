package com.chirper.frontend.application.usecase;

import com.chirper.frontend.application.exception.UnauthorizedException;
import com.chirper.frontend.domain.repository.IBackendApiRepository;
import com.chirper.frontend.infrastructure.session.JwtSessionManager;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

/**
 * リツイートユースケース
 */
@Service
public class RetweetUseCase {

    private final IBackendApiRepository apiRepository;
    private final JwtSessionManager sessionManager;

    public RetweetUseCase(
            IBackendApiRepository apiRepository,
            JwtSessionManager sessionManager
    ) {
        this.apiRepository = apiRepository;
        this.sessionManager = sessionManager;
    }

    /**
     * リツイートを実行
     *
     * @param request HTTPリクエスト
     * @param tweetId ツイートID
     * @throws UnauthorizedException 認証エラー
     */
    public void execute(HttpServletRequest request, String tweetId) {
        String jwtToken = sessionManager.getJwtToken(request);
        if (jwtToken == null || jwtToken.isBlank()) {
            throw new UnauthorizedException("ログインが必要です");
        }

        apiRepository.retweet(jwtToken, tweetId);
    }
}
