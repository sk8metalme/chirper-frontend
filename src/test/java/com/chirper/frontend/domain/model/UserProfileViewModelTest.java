package com.chirper.frontend.domain.model;

import org.junit.jupiter.api.Test;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class UserProfileViewModelTest {

    @Test
    void canFollow_自分以外のプロフィールの場合はtrueを返す() {
        // Arrange
        UserProfileViewModel profile = createProfile("user1", false);

        // Act & Assert
        assertTrue(profile.canFollow(), "自分以外のプロフィールの場合、フォロー可能");
    }

    @Test
    void canFollow_自分のプロフィールの場合はfalseを返す() {
        // Arrange
        UserProfileViewModel profile = createProfile("user1", true);

        // Act & Assert
        assertFalse(profile.canFollow(), "自分のプロフィールの場合、フォロー不可");
    }

    @Test
    void canEdit_自分のプロフィールの場合はtrueを返す() {
        // Arrange
        UserProfileViewModel profile = createProfile("user1", true);

        // Act & Assert
        assertTrue(profile.canEdit(), "自分のプロフィールの場合、編集可能");
    }

    @Test
    void canEdit_自分以外のプロフィールの場合はfalseを返す() {
        // Arrange
        UserProfileViewModel profile = createProfile("user1", false);

        // Act & Assert
        assertFalse(profile.canEdit(), "自分以外のプロフィールの場合、編集不可");
    }

    @Test
    void followButtonText_フォロー済みの場合はフォロー中を返す() {
        // Arrange
        UserProfileViewModel profile = createProfileWithFollowStatus(true);

        // Act & Assert
        assertEquals("フォロー中", profile.followButtonText(), "フォロー済みの場合、ボタンテキストは「フォロー中」");
    }

    @Test
    void followButtonText_未フォローの場合はフォローするを返す() {
        // Arrange
        UserProfileViewModel profile = createProfileWithFollowStatus(false);

        // Act & Assert
        assertEquals("フォローする", profile.followButtonText(), "未フォローの場合、ボタンテキストは「フォローする」");
    }

    @Test
    void constructor_必須フィールドがnullの場合は例外をスローする() {
        // Act & Assert: userIdがnull
        assertThrows(NullPointerException.class, () -> {
            new UserProfileViewModel(
                null, "username", "displayName", "bio", "avatar",
                0, 0, false, false, Collections.emptyList()
            );
        });

        // Act & Assert: usernameがnull
        assertThrows(NullPointerException.class, () -> {
            new UserProfileViewModel(
                "user1", null, "displayName", "bio", "avatar",
                0, 0, false, false, Collections.emptyList()
            );
        });
    }

    @Test
    void constructor_負のカウント値の場合は例外をスローする() {
        // Act & Assert: 負のフォロワー数
        assertThrows(IllegalArgumentException.class, () -> {
            new UserProfileViewModel(
                "user1", "username", "displayName", "bio", "avatar",
                -1, 0, false, false, Collections.emptyList()
            );
        });

        // Act & Assert: 負のフォロー数
        assertThrows(IllegalArgumentException.class, () -> {
            new UserProfileViewModel(
                "user1", "username", "displayName", "bio", "avatar",
                0, -1, false, false, Collections.emptyList()
            );
        });
    }

    /**
     * 基本的なUserProfileViewModelを生成するヘルパーメソッド
     */
    private UserProfileViewModel createProfile(String userId, boolean isCurrentUser) {
        return new UserProfileViewModel(
            userId,
            "username",
            "Display Name",
            "Bio text",
            "https://example.com/avatar.jpg",
            100,   // followersCount
            50,    // followingCount
            false, // followedByCurrentUser
            isCurrentUser,
            Collections.emptyList()
        );
    }

    /**
     * フォロー状態を指定してUserProfileViewModelを生成するヘルパーメソッド
     */
    private UserProfileViewModel createProfileWithFollowStatus(boolean followed) {
        return new UserProfileViewModel(
            "user1",
            "username",
            "Display Name",
            "Bio text",
            "https://example.com/avatar.jpg",
            100,   // followersCount
            50,    // followingCount
            followed,
            false, // isCurrentUser
            Collections.emptyList()
        );
    }
}
