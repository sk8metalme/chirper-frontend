package com.chirper.frontend.infrastructure.repository;

import com.chirper.frontend.application.dto.FollowListDto;
import com.chirper.frontend.application.dto.LoginResponse;
import com.chirper.frontend.application.dto.RegisterResponse;
import com.chirper.frontend.application.dto.TimelineDto;
import com.chirper.frontend.application.dto.TweetDto;
import com.chirper.frontend.application.dto.UserProfileDto;
import com.chirper.frontend.domain.repository.IBackendApiRepository;
import com.chirper.frontend.infrastructure.client.BackendApiClient;
import org.springframework.stereotype.Repository;

/**
 * Backend APIリポジトリ実装
 */
@Repository
public class BackendApiRepositoryImpl implements IBackendApiRepository {

    private final BackendApiClient apiClient;

    public BackendApiRepositoryImpl(BackendApiClient apiClient) {
        this.apiClient = apiClient;
    }

    @Override
    public LoginResponse login(String username, String password) {
        return apiClient.login(username, password);
    }

    @Override
    public RegisterResponse register(String username, String email, String password) {
        return apiClient.register(username, email, password);
    }

    @Override
    public TimelineDto getTimeline(String jwtToken, int page, int size) {
        return apiClient.getTimeline(jwtToken, page, size);
    }

    @Override
    public TweetDto createTweet(String jwtToken, String content) {
        return apiClient.createTweet(jwtToken, content);
    }

    @Override
    public TweetDto getTweet(String tweetId) {
        return apiClient.getTweet(tweetId);
    }

    @Override
    public void deleteTweet(String jwtToken, String tweetId) {
        apiClient.deleteTweet(jwtToken, tweetId);
    }

    @Override
    public UserProfileDto getUserProfile(String username) {
        return apiClient.getUserProfile(username);
    }

    @Override
    public UserProfileDto updateProfile(String jwtToken, String displayName, String bio, String avatarUrl) {
        return apiClient.updateProfile(jwtToken, displayName, bio, avatarUrl);
    }

    @Override
    public void followUser(String jwtToken, String userId) {
        apiClient.followUser(jwtToken, userId);
    }

    @Override
    public void unfollowUser(String jwtToken, String userId) {
        apiClient.unfollowUser(jwtToken, userId);
    }

    @Override
    public void likeTweet(String jwtToken, String tweetId) {
        apiClient.likeTweet(jwtToken, tweetId);
    }

    @Override
    public void unlikeTweet(String jwtToken, String tweetId) {
        apiClient.unlikeTweet(jwtToken, tweetId);
    }

    @Override
    public void retweet(String jwtToken, String tweetId) {
        apiClient.retweetTweet(jwtToken, tweetId);
    }

    @Override
    public FollowListDto getFollowers(String jwtToken, String username, int page, int size) {
        return apiClient.getFollowers(jwtToken, username, page, size);
    }

    @Override
    public FollowListDto getFollowing(String jwtToken, String username, int page, int size) {
        return apiClient.getFollowing(jwtToken, username, page, size);
    }
}
