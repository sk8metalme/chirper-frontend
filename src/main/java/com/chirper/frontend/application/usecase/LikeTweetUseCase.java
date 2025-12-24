package com.chirper.frontend.application.usecase;

import com.chirper.frontend.application.exception.UnauthorizedException;
import com.chirper.frontend.domain.repository.IBackendApiRepository;
import com.chirper.frontend.infrastructure.session.JwtSessionManager;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

/**
 * ツイートいいねユースケース
 */
@Service
public class LikeTweetUseCase {

    private final IBackendApiRepository apiRepository;
    private final JwtSessionManager sessionManager;

    public LikeTweetUseCase(
            IBackendApiRepository apiRepository,
            JwtSessionManager sessionManager
    ) {
        this.apiRepository = apiRepository;
        this.sessionManager = sessionManager;
    }

    /**
     * ツイートいいねを実行
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

        apiRepository.likeTweet(jwtToken, tweetId);
    }
}
