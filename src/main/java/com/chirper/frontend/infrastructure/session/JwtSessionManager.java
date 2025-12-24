package com.chirper.frontend.infrastructure.session;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;

/**
 * JWTセッション管理
 */
@Component
public class JwtSessionManager {

    private static final String JWT_TOKEN_ATTRIBUTE = "JWT_TOKEN";
    private static final String USER_ID_ATTRIBUTE = "USER_ID";
    private static final String USERNAME_ATTRIBUTE = "USERNAME";

    /**
     * セッションにJWTトークンを保存
     */
    public void saveJwtToken(HttpServletRequest request, String jwtToken, String userId) {
        HttpSession session = request.getSession(true);
        session.setAttribute(JWT_TOKEN_ATTRIBUTE, jwtToken);
        session.setAttribute(USER_ID_ATTRIBUTE, userId);
    }

    /**
     * セッションにJWTトークンとユーザー情報を保存
     */
    public void saveJwtToken(HttpServletRequest request, String jwtToken, String userId, String username) {
        // 3パラメータメソッドを呼び出してから、usernameを追加（DRY原則）
        saveJwtToken(request, jwtToken, userId);
        if (username != null) {
            HttpSession session = request.getSession(true);
            session.setAttribute(USERNAME_ATTRIBUTE, username);
        }
    }

    /**
     * セッションからJWTトークンを取得
     */
    public String getJwtToken(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return null;
        }
        return (String) session.getAttribute(JWT_TOKEN_ATTRIBUTE);
    }

    /**
     * セッションからユーザーIDを取得
     */
    public String getUserId(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return null;
        }
        return (String) session.getAttribute(USER_ID_ATTRIBUTE);
    }

    /**
     * セッションからユーザー名を取得
     */
    public String getUsername(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return null;
        }
        return (String) session.getAttribute(USERNAME_ATTRIBUTE);
    }

    /**
     * セッションをクリア
     */
    public void clearSession(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
    }

    /**
     * 認証済みかチェック
     */
    public boolean isAuthenticated(HttpServletRequest request) {
        String jwtToken = getJwtToken(request);
        return jwtToken != null && !jwtToken.isBlank();
    }
}
