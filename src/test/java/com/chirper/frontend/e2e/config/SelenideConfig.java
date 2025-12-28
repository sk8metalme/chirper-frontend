package com.chirper.frontend.e2e.config;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.BeforeAll;
import org.openqa.selenium.chrome.ChromeOptions;

import java.util.HashMap;
import java.util.Map;

/**
 * Selenide E2E テスト設定クラス
 *
 * <p>CI/ローカル環境を自動判定し、最適なChromeOptions設定を適用します。</p>
 *
 * <h3>環境自動検出:</h3>
 * <ul>
 *   <li>CI環境: headless mode ON、安定性重視の設定</li>
 *   <li>ローカル環境: headless mode OFF（ブラウザ表示）、デバッグ重視</li>
 *   <li>ローカルでheadless化: {@code HEADLESS=true} 環境変数で切り替え</li>
 * </ul>
 *
 * <h3>使用例:</h3>
 * <pre>{@code
 * @BeforeAll
 * public static void setup() {
 *     SelenideConfig.setup();
 * }
 * }</pre>
 *
 * @see <a href="https://selenide.org/documentation.html">Selenide Documentation</a>
 */
public class SelenideConfig {

    /**
     * Selenide E2E テスト環境を初期化
     *
     * <p>CI/ローカル環境を自動判定し、ChromeOptionsを設定します。</p>
     */
    @BeforeAll
    public static void setup() {
        // ChromeDriver自動ダウンロード
        WebDriverManager.chromedriver().setup();

        // 環境判定（CI環境かローカルか）
        boolean isCI = isCIEnvironment();

        // Chrome Options で Headless 設定を明示
        ChromeOptions options = createChromeOptions(isCI);

        // Selenide設定
        Configuration.browser = "chrome";
        Configuration.browserCapabilities = options;
        Configuration.baseUrl = System.getProperty("base.url", "http://localhost:3000");
        Configuration.timeout = 10000;  // 10秒待機
        Configuration.screenshots = true;
        Configuration.savePageSource = true;
        Configuration.reportsFolder = "build/reports/tests/e2e";
    }

    /**
     * CI環境かどうかを判定
     *
     * @return CI環境の場合true
     */
    private static boolean isCIEnvironment() {
        return System.getenv("CI") != null ||
               System.getenv("GITHUB_ACTIONS") != null ||
               System.getenv("JENKINS_HOME") != null;
    }

    /**
     * 環境に応じたChromeOptionsを生成
     *
     * @param isCI CI環境の場合true
     * @return 設定済みのChromeOptions
     */
    private static ChromeOptions createChromeOptions(boolean isCI) {
        ChromeOptions options = new ChromeOptions();

        if (isCI) {
            // CI環境用設定（安定性重視）
            options.addArguments("--headless=new");           // 新しいヘッドレスモード
            options.addArguments("--no-sandbox");             // CI環境で必須
            options.addArguments("--disable-dev-shm-usage");  // メモリ不足対策
            options.addArguments("--disable-gpu");            // GPU無効化
            options.addArguments("--disable-extensions");     // 拡張機能無効化
            options.addArguments("--disable-software-rasterizer");
            options.addArguments("--window-size=1920,1080");  // ウィンドウサイズ固定

            // パフォーマンス最適化（並列実行対応）
            options.addArguments("--aggressive-cache-discard");
            options.addArguments("--disable-background-networking");
            options.addArguments("--disable-default-apps");
            options.addArguments("--disable-sync");
        } else {
            // ローカル環境用設定（デバッグしやすさ重視）
            // ヘッドレスモードはオフ（ブラウザが表示される）
            // 環境変数で上書き可能: HEADLESS=true
            if ("true".equals(System.getenv("HEADLESS"))) {
                options.addArguments("--headless=new");
            }
            options.addArguments("--window-size=1920,1080");
            // デバッグ用: DevToolsを有効化（コメントアウト可能）
            // options.addArguments("--auto-open-devtools-for-tabs");
        }

        // 共通設定
        options.addArguments("--remote-allow-origins=*");  // CORS対策

        return options;
    }

    /**
     * 特定のテストシナリオ用のChromeOptionsを取得
     *
     * <p>モバイルテスト、パフォーマンステスト、ファイルダウンロードテストなど、
     * 特定のシナリオに最適化されたChromeOptions設定を提供します。</p>
     *
     * <h3>サポートされるシナリオ:</h3>
     * <ul>
     *   <li>{@code "mobile"}: iPhone 12 Pro エミュレーション</li>
     *   <li>{@code "performance"}: パフォーマンス測定（キャッシュ無効化、ログ取得）</li>
     *   <li>{@code "download"}: ファイルダウンロード検証</li>
     * </ul>
     *
     * <h3>使用例:</h3>
     * <pre>{@code
     * @BeforeAll
     * static void setupMobile() {
     *     ChromeOptions options = SelenideConfig.getOptionsForScenario("mobile");
     *     Configuration.browserCapabilities = options;
     * }
     * }</pre>
     *
     * @param scenario テストシナリオ（"mobile", "performance", "download"）
     * @return シナリオ別の設定を追加したChromeOptions
     */
    public static ChromeOptions getOptionsForScenario(String scenario) {
        boolean isCI = isCIEnvironment();
        ChromeOptions options = createChromeOptions(isCI);

        switch (scenario) {
            case "mobile":
                // モバイルエミュレーション
                Map<String, Object> mobileEmulation = new HashMap<>();
                mobileEmulation.put("deviceName", "iPhone 12 Pro");
                options.setExperimentalOption("mobileEmulation", mobileEmulation);
                break;

            case "performance":
                // パフォーマンステスト用
                options.addArguments("--disable-cache");
                options.addArguments("--disk-cache-size=1");
                Map<String, Object> perfLogPrefs = new HashMap<>();
                perfLogPrefs.put("performance", "ALL");
                options.setCapability("goog:loggingPrefs", perfLogPrefs);
                break;

            case "download":
                // ファイルダウンロードテスト用
                Map<String, Object> prefs = new HashMap<>();
                prefs.put("download.default_directory", System.getProperty("java.io.tmpdir") + "/selenide-downloads");
                prefs.put("download.prompt_for_download", false);
                options.setExperimentalOption("prefs", prefs);
                break;

            default:
                throw new IllegalArgumentException("Unknown scenario: " + scenario +
                    ". Supported: mobile, performance, download");
        }

        return options;
    }
}
