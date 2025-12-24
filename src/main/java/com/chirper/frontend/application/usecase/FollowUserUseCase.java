package com.chirper.frontend.application.usecase;

import com.chirper.frontend.application.exception.UnauthorizedException;
import com.chirper.frontend.domain.repository.IBackendApiRepository;
import com.chirper.frontend.infrastructure.session.JwtSessionManager;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

/**
 * ユーザーフォローユースケース
 */
@Service
public class FollowUserUseCase {

    private final IBackendApiRepository apiRepository;
    private final JwtSessionManager sessionManager;

    public FollowUserUseCase(
            IBackendApiRepository apiRepository,
            JwtSessionManager sessionManager
    ) {
        this.apiRepository = apiRepository;
        this.sessionManager = sessionManager;
    }

    /**
     * ユーザーフォローを実行
     *
     * @param request HTTPリクエスト
     * @param userId  フォロー対象のユーザーID
     * @throws UnauthorizedException 認証エラー
     */
    public void execute(HttpServletRequest request, String userId) {
        String jwtToken = sessionManager.getJwtToken(request);
        if (jwtToken == null || jwtToken.isBlank()) {
            throw new UnauthorizedException("ログインが必要です");
        }

        apiRepository.followUser(jwtToken, userId);
    }
}
