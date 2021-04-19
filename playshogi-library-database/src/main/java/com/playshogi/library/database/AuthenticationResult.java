package com.playshogi.library.database;


public class AuthenticationResult {
    public enum Status {
        LOGIN_OK, INVALID, UNAVAILABLE
    }

    private final Status status;
    private final Integer userId;
    private final String username;
    private final boolean admin;

    AuthenticationResult(Status status) {
        this(status, null, null, false);
    }

    AuthenticationResult(final Status status, final Integer userId, final String userName, final boolean admin) {
        this.status = status;
        this.userId = userId;
        this.username = userName;
        this.admin = admin;
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

    public boolean isAdmin() {
        return admin;
    }

    @Override
    public String toString() {
        return "AuthenticationResult{" +
                "status=" + status +
                ", userId=" + userId +
                ", username='" + username + '\'' +
                ", admin=" + admin +
                '}';
    }
}
