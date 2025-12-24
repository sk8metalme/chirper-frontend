package com.chirper.frontend.application.usecase;

import com.chirper.frontend.application.dto.UserProfileDto;
import com.chirper.frontend.domain.repository.IBackendApiRepository;
import org.springframework.stereotype.Service;

/**
 * ユーザープロフィール表示ユースケース
 */
@Service
public class DisplayUserProfileUseCase {

    private final IBackendApiRepository apiRepository;

    public DisplayUserProfileUseCase(IBackendApiRepository apiRepository) {
        this.apiRepository = apiRepository;
    }

    /**
     * ユーザープロフィール表示を実行
     *
     * @param username ユーザー名
     * @return ユーザープロフィールDTO
     */
    public UserProfileDto execute(String username) {
        return apiRepository.getUserProfile(username);
    }
}
