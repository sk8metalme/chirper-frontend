package com.chirper.frontend.presentation.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * ホームコントローラー
 */
@Controller
public class HomeController {

    /**
     * ホーム画面
     */
    @GetMapping("/")
    public String home() {
        return "home";
    }
}
