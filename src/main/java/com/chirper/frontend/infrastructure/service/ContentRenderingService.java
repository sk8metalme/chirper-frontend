package com.chirper.frontend.infrastructure.service;

import com.chirper.frontend.domain.service.IContentRenderingService;
import com.chirper.frontend.domain.valueobject.TweetContent;
import org.springframework.stereotype.Service;

/**
 * コンテンツレンダリングサービス実装
 */
@Service
public class ContentRenderingService implements IContentRenderingService {

    @Override
    public String renderTweetContent(TweetContent content) {
        return content.toHighlightedHtml();
    }
}
