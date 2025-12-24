package com.chirper.frontend.presentation.controller;

import com.chirper.frontend.application.usecase.DeleteTweetUseCase;
import com.chirper.frontend.application.usecase.SubmitTweetUseCase;
import com.chirper.frontend.presentation.form.TweetForm;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * ツイートコントローラー
 */
@Controller
public class TweetController {

    private final SubmitTweetUseCase submitTweetUseCase;
    private final DeleteTweetUseCase deleteTweetUseCase;

    public TweetController(
            SubmitTweetUseCase submitTweetUseCase,
            DeleteTweetUseCase deleteTweetUseCase
    ) {
        this.submitTweetUseCase = submitTweetUseCase;
        this.deleteTweetUseCase = deleteTweetUseCase;
    }

    /**
     * ツイート投稿
     */
    @PostMapping("/tweets")
    public String submitTweet(
            @Valid @ModelAttribute TweetForm tweetForm,
            BindingResult bindingResult,
            HttpServletRequest request,
            RedirectAttributes redirectAttributes
    ) {
        // バリデーションエラーがある場合
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "ツイート内容に誤りがあります");
            return "redirect:/timeline";
        }

        try {
            // ツイート投稿
            submitTweetUseCase.execute(request, tweetForm.content());
            redirectAttributes.addFlashAttribute("success", "ツイートを投稿しました");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/timeline";
    }

    /**
     * ツイート削除
     */
    @PostMapping("/tweets/{id}/delete")
    public String deleteTweet(
            @PathVariable String id,
            HttpServletRequest request,
            RedirectAttributes redirectAttributes
    ) {
        try {
            // ツイート削除
            deleteTweetUseCase.execute(request, id);
            redirectAttributes.addFlashAttribute("success", "ツイートを削除しました");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/timeline";
    }
}
