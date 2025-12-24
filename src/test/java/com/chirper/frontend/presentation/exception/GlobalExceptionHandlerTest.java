package com.chirper.frontend.presentation.exception;

import com.chirper.frontend.application.exception.UnauthorizedException;
import com.chirper.frontend.application.exception.ValidationException;
import com.chirper.frontend.domain.valueobject.FieldError;
import com.chirper.frontend.infrastructure.exception.BackendApiException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * GlobalExceptionHandler のテスト
 */
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;
    private RedirectAttributes redirectAttributes;
    private Model model;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
        redirectAttributes = new RedirectAttributesModelMap();
        model = new ExtendedModelMap();
    }

    @Test
    void shouldHandleUnauthorizedException() {
        // Arrange
        UnauthorizedException exception = new UnauthorizedException("認証が必要です");

        // Act
        String result = handler.handleUnauthorizedException(exception, redirectAttributes);

        // Assert
        assertEquals("redirect:/login", result);
        assertEquals("認証が必要です", redirectAttributes.getFlashAttributes().get("error"));
    }

    @Test
    void shouldHandleValidationExceptionWithReferer() {
        // Arrange
        FieldError error1 = new FieldError("username", "ユーザー名は必須です");
        FieldError error2 = new FieldError("password", "パスワードは8文字以上必要です");
        ValidationException exception = new ValidationException(List.of(error1, error2));

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Referer", "http://localhost/timeline");

        // Act
        String result = handler.handleValidationException(exception, request, redirectAttributes);

        // Assert
        assertEquals("redirect:/timeline", result);
        String errorMessage = (String) redirectAttributes.getFlashAttributes().get("error");
        assertTrue(errorMessage.contains("username: ユーザー名は必須です"));
        assertTrue(errorMessage.contains("password: パスワードは8文字以上必要です"));
    }

    @Test
    void shouldHandleValidationExceptionWithoutReferer() {
        // Arrange
        FieldError error = new FieldError("content", "内容は必須です");
        ValidationException exception = new ValidationException(List.of(error));

        MockHttpServletRequest request = new MockHttpServletRequest();
        // Refererヘッダーなし

        // Act
        String result = handler.handleValidationException(exception, request, redirectAttributes);

        // Assert
        assertEquals("redirect:/timeline", result);
        assertEquals("content: 内容は必須です", redirectAttributes.getFlashAttributes().get("error"));
    }

    @Test
    void shouldHandleValidationExceptionWithDisallowedReferer() {
        // Arrange
        FieldError error = new FieldError("field", "エラー");
        ValidationException exception = new ValidationException(List.of(error));

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Referer", "http://localhost/admin/secret");  // 許可されていないパス

        // Act
        String result = handler.handleValidationException(exception, request, redirectAttributes);

        // Assert
        assertEquals("redirect:/timeline", result);  // デフォルトにリダイレクト
    }

    @Test
    void shouldHandleValidationExceptionWithInvalidRefererUri() {
        // Arrange
        FieldError error = new FieldError("field", "エラー");
        ValidationException exception = new ValidationException(List.of(error));

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Referer", "invalid::uri");  // 不正なURI

        // Act
        String result = handler.handleValidationException(exception, request, redirectAttributes);

        // Assert
        assertEquals("redirect:/timeline", result);  // デフォルトにリダイレクト
    }

    @Test
    void shouldHandleBackendApiException() {
        // Arrange
        BackendApiException exception = new BackendApiException("バックエンドAPI呼び出しに失敗しました", 500, "Internal Server Error");

        // Act
        String result = handler.handleBackendApiException(exception, model);

        // Assert
        assertEquals("error/500", result);
        assertEquals("サーバーエラーが発生しました", model.getAttribute("error"));
        assertEquals("バックエンドAPI呼び出しに失敗しました", model.getAttribute("message"));
    }

    @Test
    void shouldHandleGeneralException() {
        // Arrange
        Exception exception = new RuntimeException("予期しないエラー");

        // Act
        String result = handler.handleException(exception, model);

        // Assert
        assertEquals("error/500", result);
        assertEquals("予期しないエラーが発生しました", model.getAttribute("error"));
        assertEquals("予期しないエラー", model.getAttribute("message"));
    }
}
