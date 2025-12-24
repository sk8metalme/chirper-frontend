package com.chirper.frontend.presentation.exception;

import com.chirper.frontend.application.exception.UnauthorizedException;
import com.chirper.frontend.application.exception.ValidationException;
import com.chirper.frontend.infrastructure.exception.BackendApiException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

/**
 * グローバル例外ハンドラー
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 認証エラー処理
     */
    @ExceptionHandler(UnauthorizedException.class)
    public String handleUnauthorizedException(UnauthorizedException ex, RedirectAttributes redirectAttributes) {
        logger.warn("Unauthorized access: {}", ex.getMessage());
        redirectAttributes.addFlashAttribute("error", ex.getMessage());
        return "redirect:/login";
    }

    /**
     * バリデーションエラー処理
     */
    @ExceptionHandler(ValidationException.class)
    public String handleValidationException(
            ValidationException ex,
            HttpServletRequest request,
            RedirectAttributes redirectAttributes) {
        logger.warn("Validation error: {}", ex.getMessage());
        String errorMessage = ex.getErrors().stream()
                .map(error -> error.getField() + ": " + error.getMessage())
                .reduce((a, b) -> a + ", " + b)
                .orElse("バリデーションエラーが発生しました");
        redirectAttributes.addFlashAttribute("error", errorMessage);

        // Refererヘッダーを検証して、元のページに戻る（UX改善）
        String referer = request.getHeader("Referer");
        String redirectUrl = validateAndGetRedirectUrl(referer);
        return "redirect:" + redirectUrl;
    }

    /**
     * バックエンドAPIエラー処理
     */
    @ExceptionHandler(BackendApiException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleBackendApiException(BackendApiException ex, Model model) {
        logger.error("Backend API error: {}", ex.getMessage(), ex);
        model.addAttribute("error", "サーバーエラーが発生しました");
        model.addAttribute("message", ex.getMessage());
        return "error/500";
    }

    /**
     * 一般的な例外処理
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleException(Exception ex, Model model) {
        logger.error("Unexpected error: {}", ex.getMessage(), ex);
        model.addAttribute("error", "予期しないエラーが発生しました");
        model.addAttribute("message", ex.getMessage());
        return "error/500";
    }

    /**
     * Refererヘッダーを検証し、安全なリダイレクトURLを返す
     * オープンリダイレクト対策: 内部URLのみ許可
     *
     * @param referer Refererヘッダー
     * @return 検証済みのリダイレクトURL
     */
    private String validateAndGetRedirectUrl(String referer) {
        // Refererが空の場合はデフォルト
        if (referer == null || referer.isBlank()) {
            return "/timeline";
        }

        try {
            URI uri = new URI(referer);
            String path = uri.getPath();

            // 許可するパスのホワイトリスト
            List<String> allowedPaths = List.of(
                    "/timeline",
                    "/profile",
                    "/users/",
                    "/login",
                    "/register"
            );

            // パスが許可リストのいずれかで始まるかチェック
            if (path != null) {
                for (String allowedPath : allowedPaths) {
                    if (path.startsWith(allowedPath)) {
                        return path;
                    }
                }
            }

            // 許可されていない場合はデフォルト
            return "/timeline";
        } catch (URISyntaxException e) {
            // 不正なURIの場合はデフォルト
            return "/timeline";
        }
    }
}
