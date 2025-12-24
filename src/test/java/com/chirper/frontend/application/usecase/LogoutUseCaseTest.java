package com.chirper.frontend.application.usecase;

import com.chirper.frontend.infrastructure.session.JwtSessionManager;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LogoutUseCaseTest {

    @Mock
    private JwtSessionManager sessionManager;

    @Mock
    private HttpServletRequest request;

    private LogoutUseCase logoutUseCase;

    @BeforeEach
    void setUp() {
        logoutUseCase = new LogoutUseCase(sessionManager);
    }

    @Test
    void shouldLogoutSuccessfully() {
        // Act
        logoutUseCase.execute(request);

        // Assert
        verify(sessionManager).clearSession(request);
    }

    @Test
    void shouldHandleMultipleLogoutCalls() {
        // Act
        logoutUseCase.execute(request);
        logoutUseCase.execute(request);

        // Assert
        verify(sessionManager, times(2)).clearSession(request);
    }
}
