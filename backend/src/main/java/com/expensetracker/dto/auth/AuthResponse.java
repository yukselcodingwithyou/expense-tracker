package com.expensetracker.dto.auth;

public class AuthResponse {
    private String accessToken;
    private String refreshToken;
    private String tokenType = "Bearer";
    private Long expiresIn;
    private UserInfo user;

    public AuthResponse() {}

    public AuthResponse(String accessToken, String refreshToken, Long expiresIn, UserInfo user) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.expiresIn = expiresIn;
        this.user = user;
    }

    public static class UserInfo {
        private String id;
        private String email;

        public UserInfo() {}

        public UserInfo(String id, String email) {
            this.id = id;
            this.email = email;
        }

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
    }

    public String getAccessToken() { return accessToken; }
    public void setAccessToken(String accessToken) { this.accessToken = accessToken; }

    public String getRefreshToken() { return refreshToken; }
    public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }

    public String getTokenType() { return tokenType; }
    public void setTokenType(String tokenType) { this.tokenType = tokenType; }

    public Long getExpiresIn() { return expiresIn; }
    public void setExpiresIn(Long expiresIn) { this.expiresIn = expiresIn; }

    public UserInfo getUser() { return user; }
    public void setUser(UserInfo user) { this.user = user; }
}