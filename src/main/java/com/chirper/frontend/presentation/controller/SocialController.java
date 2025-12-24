package com.chirper.frontend.presentation.controller;

import com.chirper.frontend.application.usecase.FollowUserUseCase;
import com.chirper.frontend.application.usecase.UnfollowUserUseCase;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * ソーシャル機能コントローラー
 */
@Controller
public class SocialController {

    private final FollowUserUseCase followUserUseCase;
    private final UnfollowUserUseCase unfollowUserUseCase;

    public SocialController(
            FollowUserUseCase followUserUseCase,
            UnfollowUserUseCase unfollowUserUseCase
    ) {
        this.followUserUseCase = followUserUseCase;
        this.unfollowUserUseCase = unfollowUserUseCase;
    }

    /**
     * ユーザーフォロー
     */
    @PostMapping("/follow/{userId}")
    public String followUser(
            @PathVariable String userId,
            HttpServletRequest request,
            RedirectAttributes redirectAttributes
    ) {
        try {
            followUserUseCase.execute(request, userId);
            redirectAttributes.addFlashAttribute("success", "フォローしました");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        // リファラーにリダイレクト（前のページに戻る）
        String referer = request.getHeader("Referer");
        if (referer != null && !referer.isEmpty()) {
            return "redirect:" + referer;
        }
        return "redirect:/timeline";
    }

    /**
     * ユーザーアンフォロー
     */
    @PostMapping("/unfollow/{userId}")
    public String unfollowUser(
            @PathVariable String userId,
            HttpServletRequest request,
            RedirectAttributes redirectAttributes
    ) {
        try {
            unfollowUserUseCase.execute(request, userId);
            redirectAttributes.addFlashAttribute("success", "フォローを解除しました");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        // リファラーにリダイレクト（前のページに戻る）
        String referer = request.getHeader("Referer");
        if (referer != null && !referer.isEmpty()) {
            return "redirect:" + referer;
        }
        return "redirect:/timeline";
    }

    /**
     * フォロワー一覧表示
     */
    @GetMapping("/followers/{username}")
    public String followers(@PathVariable String username, Model model) {
        // TODO: Phase 2.5 - BackendApiClientにgetFollowersメソッドを追加後に実装
        model.addAttribute("username", username);
        model.addAttribute("followers", java.util.Collections.emptyList());
        return "followers";
    }

    /**
     * フォロー中一覧表示
     */
    @GetMapping("/following/{username}")
    public String following(@PathVariable String username, Model model) {
        // TODO: Phase 2.5 - BackendApiClientにgetFollowingメソッドを追加後に実装
        model.addAttribute("username", username);
        model.addAttribute("following", java.util.Collections.emptyList());
        return "following";
    }
}
