package com.chirper.frontend.presentation.controller;

import com.chirper.frontend.application.dto.UserProfileDto;
import com.chirper.frontend.application.usecase.DisplayUserProfileUseCase;
import com.chirper.frontend.application.usecase.UpdateProfileUseCase;
import com.chirper.frontend.infrastructure.session.JwtSessionManager;
import com.chirper.frontend.presentation.form.ProfileForm;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * プロフィールコントローラー
 */
@Controller
public class ProfileController {

    private final DisplayUserProfileUseCase displayUserProfileUseCase;
    private final UpdateProfileUseCase updateProfileUseCase;
    private final JwtSessionManager sessionManager;

    public ProfileController(
            DisplayUserProfileUseCase displayUserProfileUseCase,
            UpdateProfileUseCase updateProfileUseCase,
            JwtSessionManager sessionManager
    ) {
        this.displayUserProfileUseCase = displayUserProfileUseCase;
        this.updateProfileUseCase = updateProfileUseCase;
        this.sessionManager = sessionManager;
    }

    /**
     * プロフィール表示
     */
    @GetMapping("/profile/{username}")
    public String profile(@PathVariable String username, HttpServletRequest request, Model model) {
        // プロフィール取得
        UserProfileDto profile = displayUserProfileUseCase.execute(username);

        // 現在のユーザーがプロフィール所有者かどうかを判定
        boolean isOwner = false;
        try {
            String currentUsername = sessionManager.getUsername(request);
            isOwner = currentUsername != null && currentUsername.equals(username);
        } catch (Exception e) {
            // セッション取得エラーは無視（未ログイン状態）
        }

        model.addAttribute("profile", profile);
        model.addAttribute("isOwner", isOwner);
        return "profile";
    }

    /**
     * プロフィール編集画面
     */
    @GetMapping("/profile/edit")
    public String editProfileForm(HttpServletRequest request, Model model) {
        try {
            // セッションから現在のユーザー名を取得
            String username = sessionManager.getUsername(request);
            if (username == null) {
                // ユーザー名が取得できない場合は空のフォームを表示
                model.addAttribute("profileForm", new ProfileForm("", "", ""));
                model.addAttribute("error", "ログインが必要です");
                return "profile-edit";
            }

            // 現在のユーザープロフィールを取得
            UserProfileDto currentProfile = displayUserProfileUseCase.execute(username);

            // ProfileFormに変換（displayNameとavatarUrlは現状未サポート）
            ProfileForm profileForm = new ProfileForm(
                    "", // displayName (未サポート)
                    currentProfile.bio() != null ? currentProfile.bio() : "",
                    "" // avatarUrl (未サポート)
            );

            model.addAttribute("profileForm", profileForm);
            return "profile-edit";

        } catch (Exception e) {
            // エラー時は空のフォームを表示
            model.addAttribute("profileForm", new ProfileForm("", "", ""));
            model.addAttribute("error", "プロフィール情報の取得に失敗しました");
            return "profile-edit";
        }
    }

    /**
     * プロフィール更新
     */
    @PostMapping("/profile/edit")
    public String updateProfile(
            @Valid @ModelAttribute("profileForm") ProfileForm profileForm,
            BindingResult bindingResult,
            HttpServletRequest request,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        // バリデーションエラーがある場合
        if (bindingResult.hasErrors()) {
            model.addAttribute("error", "入力内容に誤りがあります");
            // リダイレクトせず、直接ビューを返す（フォームデータを保持）
            return "profile-edit";
        }

        try {
            // プロフィール更新
            UserProfileDto updatedProfile = updateProfileUseCase.execute(
                    request,
                    profileForm.displayName(),
                    profileForm.bio(),
                    profileForm.avatarUrl()
            );

            redirectAttributes.addFlashAttribute("success", "プロフィールを更新しました");
            return "redirect:/profile/" + updatedProfile.username();

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/profile/edit";
        }
    }
}
