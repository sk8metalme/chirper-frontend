package com.chirper.frontend.domain.service;

import com.chirper.frontend.domain.valueobject.TweetContent;

/**
 * コンテンツレンダリングサービスインターフェース
 *
 * ツイート本文のHTML変換ロジックを提供する
 */
public interface IContentRenderingService {

    /**
     * ツイート本文をHTML変換する
     *
     * @param content ツイート本文
     * @return ハイライト処理されたHTML文字列
     */
    String renderTweetContent(TweetContent content);
}
