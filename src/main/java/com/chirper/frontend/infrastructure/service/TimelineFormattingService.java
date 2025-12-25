package com.chirper.frontend.infrastructure.service;

import com.chirper.frontend.application.dto.TweetDto;
import com.chirper.frontend.application.service.ITimelineFormattingService;
import com.chirper.frontend.domain.model.TimelineViewModel;
import com.chirper.frontend.domain.model.TweetViewModel;
import com.chirper.frontend.domain.valueobject.DisplayTimestamp;
import com.chirper.frontend.domain.valueobject.TweetContent;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * タイムラインフォーマットサービス実装
 */
@Service
public class TimelineFormattingService implements ITimelineFormattingService {

    @Override
    public TimelineViewModel formatTimeline(List<TweetDto> tweets, int currentPage, int totalPages) {
        List<TweetViewModel> viewModels = tweets.stream()
                .map(this::convertToViewModel)
                .toList();

        return new TimelineViewModel(
                viewModels,
                currentPage,
                totalPages
        );
    }

    /**
     * TweetDtoをTweetViewModelに変換
     */
    private TweetViewModel convertToViewModel(TweetDto dto) {
        TweetContent content = new TweetContent(dto.content());
        DisplayTimestamp timestamp = new DisplayTimestamp(dto.createdAt());

        return new TweetViewModel(
                dto.tweetId(),
                dto.userId(),
                dto.username(),
                dto.username(), // displayName (currently same as username)
                null, // avatarUrl (not available in DTO)
                content,
                timestamp,
                dto.likeCount(),
                dto.retweetCount(),
                dto.likedByCurrentUser(),
                dto.retweetedByCurrentUser()
        );
    }
}
