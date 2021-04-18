package com.playshogi.website.gwt.shared.models;

import java.io.Serializable;

@SuppressWarnings("serial")
public class LoginResult implements Serializable {

    private boolean loggedIn = false;
    private String sessionId = null;
    private String userName = null;
    private int userId = 0;
    private String errorMessage = null;

    public LoginResult() {
    }

    public LoginResult(final boolean loggedIn, final String sessionId, final String userName, final int userId,
                       final String errorMessage) {
        this.loggedIn = loggedIn;
        this.sessionId = sessionId;
        this.userName = userName;
        this.userId = userId;
        this.errorMessage = errorMessage;
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public void setLoggedIn(final boolean loggedIn) {
        this.loggedIn = loggedIn;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(final String sessionId) {
        this.sessionId = sessionId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(final String userName) {
        this.userName = userName;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(final String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "LoginResult{" +
                "loggedIn=" + loggedIn +
                ", sessionId='" + sessionId + '\'' +
                ", userName='" + userName + '\'' +
                ", userId=" + userId +
                ", errorMessage='" + errorMessage + '\'' +
                '}';
    }
}
