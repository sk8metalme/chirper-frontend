package com.chirper.e2e.config;

import java.util.UUID;

public class TestDataFixture {

    public static class Users {
        public static final String TEST_USER_1_USERNAME = "testuser1";
        public static final String TEST_USER_1_PASSWORD = "password123";
        public static final String TEST_USER_1_EMAIL = "testuser1@example.com";

        public static final String TEST_USER_2_USERNAME = "testuser2";
        public static final String TEST_USER_2_PASSWORD = "password123";
        public static final String TEST_USER_2_EMAIL = "testuser2@example.com";
    }

    public static class Tweets {
        public static String generateTweetContent() {
            return "Test tweet " + UUID.randomUUID().toString().substring(0, 8);
        }
    }
}
