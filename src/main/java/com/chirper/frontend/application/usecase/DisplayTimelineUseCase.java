package com.chirper.frontend.application.usecase;

import com.chirper.frontend.application.dto.TimelineDto;
import com.chirper.frontend.application.exception.UnauthorizedException;
import com.chirper.frontend.application.service.ITimelineFormattingService;
import com.chirper.frontend.domain.model.TimelineViewModel;
import com.chirper.frontend.domain.repository.IBackendApiRepository;
import com.chirper.frontend.infrastructure.session.JwtSessionManager;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

/**
 * タイムライン表示ユースケース
 */
@Service
public class DisplayTimelineUseCase {

    private final IBackendApiRepository apiRepository;
    private final ITimelineFormattingService formattingService;
    private final JwtSessionManager sessionManager;

    public DisplayTimelineUseCase(
            IBackendApiRepository apiRepository,
            ITimelineFormattingService formattingService,
            JwtSessionManager sessionManager
    ) {
        this.apiRepository = apiRepository;
        this.formattingService = formattingService;
        this.sessionManager = sessionManager;
    }

    /**
     * タイムライン表示を実行
     *
     * @param request HTTPリクエスト
     * @param page    ページ番号
     * @param size    ページサイズ
     * @return タイムラインViewModel
     * @throws UnauthorizedException 認証エラー
     */
    public TimelineViewModel execute(HttpServletRequest request, int page, int size) {
        // 1. JWTトークン取得
        String jwtToken = sessionManager.getJwtToken(request);
        if (jwtToken == null || jwtToken.isBlank()) {
            throw new UnauthorizedException("ログインが必要です");
        }

        // 2. Backend APIからタイムライン取得
        TimelineDto timelineDto = apiRepository.getTimeline(jwtToken, page, size);

        // 3. ドメインサービスでViewModelに変換
        return formattingService.formatTimeline(
                timelineDto.tweets(),
                timelineDto.currentPage(),
                timelineDto.totalPages()
        );
    }
}
