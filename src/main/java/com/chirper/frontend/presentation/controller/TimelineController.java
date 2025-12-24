package com.chirper.frontend.presentation.controller;

import com.chirper.frontend.application.usecase.DisplayTimelineUseCase;
import com.chirper.frontend.domain.model.TimelineViewModel;
import com.chirper.frontend.presentation.form.TweetForm;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * タイムラインコントローラー
 */
@Controller
public class TimelineController {

    private final DisplayTimelineUseCase displayTimelineUseCase;

    public TimelineController(DisplayTimelineUseCase displayTimelineUseCase) {
        this.displayTimelineUseCase = displayTimelineUseCase;
    }

    /**
     * タイムライン表示
     */
    @GetMapping("/timeline")
    public String timeline(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            HttpServletRequest request,
            Model model
    ) {
        // タイムラインを取得
        TimelineViewModel timeline = displayTimelineUseCase.execute(request, page, size);

        // モデルに追加
        model.addAttribute("timeline", timeline);
        model.addAttribute("tweetForm", new TweetForm(""));

        return "timeline";
    }
}
