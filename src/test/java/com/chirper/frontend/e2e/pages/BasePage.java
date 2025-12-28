package com.chirper.frontend.e2e.pages;

import com.codeborne.selenide.SelenideElement;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;

/**
 * Page Object の基底クラス
 *
 * <p>すべてのPage Objectが継承する共通機能を提供します。</p>
 *
 * <h3>設計原則:</h3>
 * <ul>
 *   <li>Page ObjectはUIの抽象化のみを提供し、ビジネスロジックを含まない</li>
 *   <li>Fluent APIパターンに従い、メソッドチェーンを可能にする</li>
 *   <li>セレクタはIDまたはCSS classを使用（XPath禁止）</li>
 * </ul>
 *
 * @see <a href="https://selenide.org/documentation/page-objects.html">Selenide Page Objects</a>
 */
public abstract class BasePage {

    /**
     * ページを開く
     *
     * @param url 相対パス（例: "/login", "/timeline"）
     */
    protected void openPage(String url) {
        open(url);
    }

    /**
     * ヘッダー要素を取得
     *
     * @return ヘッダー要素
     */
    protected SelenideElement getHeader() {
        return $("header");
    }

    /**
     * ナビゲーションバーを取得
     *
     * @return ナビゲーションバー要素
     */
    protected SelenideElement getNavbar() {
        return $(".navbar");
    }

    /**
     * フッター要素を取得
     *
     * @return フッター要素
     */
    protected SelenideElement getFooter() {
        return $("footer");
    }

    /**
     * ページタイトルを取得
     *
     * @return ページタイトル要素
     */
    protected SelenideElement getPageTitle() {
        return $("h1");
    }

    /**
     * エラーメッセージ要素を取得
     *
     * @return エラーメッセージ要素
     */
    protected SelenideElement getErrorMessage() {
        return $(".error-message, .alert-danger");
    }

    /**
     * 成功メッセージ要素を取得
     *
     * @return 成功メッセージ要素
     */
    protected SelenideElement getSuccessMessage() {
        return $(".success-message, .alert-success");
    }
}
