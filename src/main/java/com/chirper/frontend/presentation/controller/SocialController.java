package com.chirper.frontend.presentation.controller;

import com.chirper.frontend.application.dto.FollowListDto;
import com.chirper.frontend.application.usecase.FollowUserUseCase;
import com.chirper.frontend.application.usecase.UnfollowUserUseCase;
import com.chirper.frontend.domain.repository.IBackendApiRepository;
import com.chirper.frontend.infrastructure.session.JwtSessionManager;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

/**
 * ソーシャル機能コントローラー
 */
@Controller
public class SocialController {

    private final FollowUserUseCase followUserUseCase;
    private final UnfollowUserUseCase unfollowUserUseCase;
    private final IBackendApiRepository apiRepository;
    private final JwtSessionManager sessionManager;

    public SocialController(
            FollowUserUseCase followUserUseCase,
            UnfollowUserUseCase unfollowUserUseCase,
            IBackendApiRepository apiRepository,
            JwtSessionManager sessionManager
    ) {
        this.followUserUseCase = followUserUseCase;
        this.unfollowUserUseCase = unfollowUserUseCase;
        this.apiRepository = apiRepository;
        this.sessionManager = sessionManager;
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
        // オープンリダイレクト対策: 内部URLのみ許可
        String referer = request.getHeader("Referer");
        String redirectUrl = validateAndGetRedirectUrl(referer);
        return "redirect:" + redirectUrl;
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
        // オープンリダイレクト対策: 内部URLのみ許可
        String referer = request.getHeader("Referer");
        String redirectUrl = validateAndGetRedirectUrl(referer);
        return "redirect:" + redirectUrl;
    }

    /**
     * フォロワー一覧表示
     */
    @GetMapping("/followers/{username}")
    public String followers(
            @PathVariable String username,
            HttpServletRequest request,
            Model model
    ) {
        String jwtToken = sessionManager.getJwtToken(request);
        FollowListDto followersDto = apiRepository.getFollowers(jwtToken, username, 0, 100);

        model.addAttribute("username", username);
        model.addAttribute("followers", followersDto.users());
        return "followers";
    }

    /**
     * フォロー中一覧表示
     */
    @GetMapping("/following/{username}")
    public String following(
            @PathVariable String username,
            HttpServletRequest request,
            Model model
    ) {
        String jwtToken = sessionManager.getJwtToken(request);
        FollowListDto followingDto = apiRepository.getFollowing(jwtToken, username, 0, 100);

        model.addAttribute("username", username);
        model.addAttribute("following", followingDto.users());
        return "following";
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
                    "/users/"  // 他のユーザーのプロフィール
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
