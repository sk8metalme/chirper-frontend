package com.chirper.frontend.application.usecase;

import com.chirper.frontend.infrastructure.session.JwtSessionManager;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

/**
 * ログアウトユースケース
 */
@Service
public class LogoutUseCase {

    private final JwtSessionManager sessionManager;

    public LogoutUseCase(JwtSessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    /**
     * ログアウト処理を実行
     *
     * @param request HTTPリクエスト
     */
    public void execute(HttpServletRequest request) {
        // セッションをクリア
        sessionManager.clearSession(request);
    }
}
