package com.social.user.dto;

public class AuthResponse {
    private String token;
    private String refreshToken;
    private String userId;
    private String username;

    public AuthResponse(String token, String refreshToken, String userId, String username) {
        this.token = token;
        this.refreshToken = refreshToken;
        this.userId = userId;
        this.username = username;
    }

    public String getToken() {
        return token;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public String getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }
}
