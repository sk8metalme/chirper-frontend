package com.chirper.frontend.domain.service;

import com.chirper.frontend.domain.model.TimelineViewModel;
import com.chirper.frontend.application.dto.TweetDto;

import java.util.List;

/**
 * タイムライン整形サービスインターフェース
 *
 * DTOをViewModelに変換するドメインロジックを提供する
 */
public interface ITimelineFormattingService {

    /**
     * タイムラインを整形する
     *
     * @param tweets ツイートDTOのリスト
     * @param currentPage 現在のページ番号
     * @param totalPages 総ページ数
     * @return 整形されたTimelineViewModel
     */
    TimelineViewModel formatTimeline(List<TweetDto> tweets, int currentPage, int totalPages);
}
