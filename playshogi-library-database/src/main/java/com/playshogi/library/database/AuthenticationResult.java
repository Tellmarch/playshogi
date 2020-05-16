package com.playshogi.library.database;


public class AuthenticationResult {
    public enum Status {
        LOGIN_OK, UNKNOWN, INVALID, UNAVAILABLE
    }

    private final Status status;
    private final Integer userId;
    private final String username;

    AuthenticationResult(Status status, Integer userId, String userName) {
        this.status = status;
        this.userId = userId;
        this.username = userName;
    }

    public Status getStatus() {
        return status;
    }

    public Integer getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    @Override
    public String toString() {
        return "AuthenticationResult{" +
                "status=" + status +
                ", userId=" + userId +
                ", username='" + username + '\'' +
                '}';
    }
}
