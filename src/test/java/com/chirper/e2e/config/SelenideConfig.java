package com.chirper.e2e.config;

import com.codeborne.selenide.Configuration;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.BeforeAll;

public class SelenideConfig {

    @BeforeAll
    public static void setup() {
        // ChromeDriver自動ダウンロード
        WebDriverManager.chromedriver().setup();

        // Selenide設定
        Configuration.browser = "chrome";

        // CI環境またはシステムプロパティからheadlessモードを判定
        String headlessProp = System.getProperty("selenide.headless",
            System.getenv("CI") != null ? "true" : "false");
        Configuration.headless = Boolean.parseBoolean(headlessProp);

        Configuration.baseUrl = "http://localhost:8081";  // Frontend port
        Configuration.timeout = 10000;  // 10秒待機
        Configuration.screenshots = true;
        Configuration.savePageSource = true;
        Configuration.reportsFolder = "build/reports/tests/e2e";
    }
}
