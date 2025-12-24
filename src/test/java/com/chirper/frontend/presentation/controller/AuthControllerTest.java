package com.chirper.frontend.presentation.controller;

import com.chirper.frontend.application.dto.LoginResponse;
import com.chirper.frontend.application.dto.RegisterResponse;
import com.chirper.frontend.application.exception.UnauthorizedException;
import com.chirper.frontend.application.exception.ValidationException;
import com.chirper.frontend.application.usecase.LoginUseCase;
import com.chirper.frontend.application.usecase.LogoutUseCase;
import com.chirper.frontend.application.usecase.RegisterUseCase;
import com.chirper.frontend.domain.valueobject.FieldError;
import com.chirper.frontend.infrastructure.config.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * AuthController のテスト
 */
@WebMvcTest(AuthController.class)
@Import(SecurityConfig.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LoginUseCase loginUseCase;

    @MockBean
    private RegisterUseCase registerUseCase;

    @MockBean
    private LogoutUseCase logoutUseCase;

    // ログイン画面表示のテスト
    @Test
    void shouldDisplayLoginForm() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"))
                .andExpect(model().attributeExists("loginForm"));
    }

    // ログイン成功のテスト
    @Test
    void shouldLoginSuccessfully() throws Exception {
        // Arrange
        LoginResponse response = new LoginResponse("test-jwt-token", "user-id-123");
        when(loginUseCase.execute(any(), eq("testuser"), eq("password123")))
                .thenReturn(response);

        // Act & Assert
        mockMvc.perform(post("/login")
                        .with(csrf())
                        .param("username", "testuser")
                        .param("password", "password123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/timeline"))
                .andExpect(flash().attribute("success", "ログインしました"));

        verify(loginUseCase).execute(any(), eq("testuser"), eq("password123"));
    }

    // ログインバリデーションエラーのテスト
    @Test
    void shouldReturnLoginFormWhenValidationFails() throws Exception {
        // 空のusernameでバリデーションエラー
        mockMvc.perform(post("/login")
                        .with(csrf())
                        .param("username", "")
                        .param("password", "password123"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"))
                .andExpect(model().hasErrors());

        verify(loginUseCase, never()).execute(any(), anyString(), anyString());
    }

    // ログイン認証失敗のテスト
    @Test
    void shouldHandleLoginFailure() throws Exception {
        // Arrange
        when(loginUseCase.execute(any(), eq("testuser"), eq("wrongpassword")))
                .thenThrow(new UnauthorizedException("ユーザー名またはパスワードが間違っています"));

        // Act & Assert
        mockMvc.perform(post("/login")
                        .with(csrf())
                        .param("username", "testuser")
                        .param("password", "wrongpassword"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"))
                .andExpect(flash().attribute("error", "ユーザー名またはパスワードが間違っています"));
    }

    // 新規登録画面表示のテスト
    @Test
    void shouldDisplayRegisterForm() throws Exception {
        mockMvc.perform(get("/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attributeExists("registerForm"));
    }

    // 新規登録成功のテスト
    @Test
    void shouldRegisterSuccessfully() throws Exception {
        // Arrange
        RegisterResponse response = new RegisterResponse("user-id-123", "登録が完了しました");
        when(registerUseCase.execute(
                eq("newuser"),
                eq("new@example.com"),
                eq("password123"),
                eq("password123")
        )).thenReturn(response);

        // Act & Assert
        mockMvc.perform(post("/register")
                        .with(csrf())
                        .param("username", "newuser")
                        .param("email", "new@example.com")
                        .param("password", "password123")
                        .param("passwordConfirm", "password123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"))
                .andExpect(flash().attribute("success", "登録が完了しました。ログインしてください"));

        verify(registerUseCase).execute(
                eq("newuser"),
                eq("new@example.com"),
                eq("password123"),
                eq("password123")
        );
    }

    // 新規登録バリデーションエラーのテスト
    @Test
    void shouldReturnRegisterFormWhenValidationFails() throws Exception {
        // 空のusernameでバリデーションエラー
        mockMvc.perform(post("/register")
                        .with(csrf())
                        .param("username", "")
                        .param("email", "test@example.com")
                        .param("password", "password123")
                        .param("passwordConfirm", "password123"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().hasErrors());

        verify(registerUseCase, never()).execute(anyString(), anyString(), anyString(), anyString());
    }

    // パスワード不一致のテスト
    @Test
    void shouldReturnRegisterFormWhenPasswordsDoNotMatch() throws Exception {
        mockMvc.perform(post("/register")
                        .with(csrf())
                        .param("username", "newuser")
                        .param("email", "new@example.com")
                        .param("password", "password123")
                        .param("passwordConfirm", "different"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrors("registerForm", "passwordConfirm"));

        verify(registerUseCase, never()).execute(anyString(), anyString(), anyString(), anyString());
    }

    // 新規登録失敗のテスト（ユーザー名重複など）
    @Test
    void shouldHandleRegisterFailure() throws Exception {
        // Arrange
        when(registerUseCase.execute(
                eq("existinguser"),
                eq("existing@example.com"),
                eq("password123"),
                eq("password123")
        )).thenThrow(new ValidationException(
                List.of(new FieldError("username", "このユーザー名は既に使用されています"))
        ));

        // Act & Assert
        mockMvc.perform(post("/register")
                        .with(csrf())
                        .param("username", "existinguser")
                        .param("email", "existing@example.com")
                        .param("password", "password123")
                        .param("passwordConfirm", "password123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/register"))
                .andExpect(flash().attributeExists("error"));
    }

    // ログアウト成功のテスト
    @Test
    void shouldLogoutSuccessfully() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/logout")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(flash().attribute("success", "ログアウトしました"));

        verify(logoutUseCase).execute(any());
    }
}
