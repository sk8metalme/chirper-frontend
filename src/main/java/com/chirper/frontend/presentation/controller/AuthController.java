package com.chirper.frontend.presentation.controller;

import com.chirper.frontend.application.dto.LoginResponse;
import com.chirper.frontend.application.dto.RegisterResponse;
import com.chirper.frontend.application.usecase.LoginUseCase;
import com.chirper.frontend.application.usecase.LogoutUseCase;
import com.chirper.frontend.application.usecase.RegisterUseCase;
import com.chirper.frontend.presentation.form.LoginForm;
import com.chirper.frontend.presentation.form.RegisterForm;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * 認証コントローラー
 */
@Controller
public class AuthController {

    private final LoginUseCase loginUseCase;
    private final RegisterUseCase registerUseCase;
    private final LogoutUseCase logoutUseCase;

    public AuthController(
            LoginUseCase loginUseCase,
            RegisterUseCase registerUseCase,
            LogoutUseCase logoutUseCase
    ) {
        this.loginUseCase = loginUseCase;
        this.registerUseCase = registerUseCase;
        this.logoutUseCase = logoutUseCase;
    }

    /**
     * ログイン画面表示
     */
    @GetMapping("/login")
    public String loginForm(Model model) {
        model.addAttribute("loginForm", new LoginForm("", ""));
        return "login";
    }

    /**
     * ログイン処理
     */
    @PostMapping("/login")
    public String login(
            @Valid @ModelAttribute LoginForm loginForm,
            BindingResult bindingResult,
            HttpServletRequest request,
            RedirectAttributes redirectAttributes
    ) {
        // バリデーションエラーがある場合
        if (bindingResult.hasErrors()) {
            return "login";
        }

        try {
            // ログイン実行
            LoginResponse response = loginUseCase.execute(
                    request,
                    loginForm.username(),
                    loginForm.password()
            );

            redirectAttributes.addFlashAttribute("success", "ログインしました");
            return "redirect:/timeline";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/login";
        }
    }

    /**
     * 新規登録画面表示
     */
    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("registerForm", new RegisterForm("", "", "", ""));
        return "register";
    }

    /**
     * 新規登録処理
     */
    @PostMapping("/register")
    public String register(
            @Valid @ModelAttribute RegisterForm registerForm,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes
    ) {
        // バリデーションエラーがある場合
        if (bindingResult.hasErrors()) {
            return "register";
        }

        // パスワード一致チェック
        if (!registerForm.password().equals(registerForm.passwordConfirm())) {
            bindingResult.rejectValue("passwordConfirm", "error.registerForm", "パスワードが一致しません");
            return "register";
        }

        try {
            // 登録実行
            RegisterResponse response = registerUseCase.execute(
                    registerForm.username(),
                    registerForm.email(),
                    registerForm.password(),
                    registerForm.passwordConfirm()
            );

            redirectAttributes.addFlashAttribute("success", "登録が完了しました。ログインしてください");
            return "redirect:/login";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/register";
        }
    }

    /**
     * ログアウト処理
     */
    @PostMapping("/logout")
    public String logout(HttpServletRequest request, RedirectAttributes redirectAttributes) {
        logoutUseCase.execute(request);
        redirectAttributes.addFlashAttribute("success", "ログアウトしました");
        return "redirect:/";
    }
}
