package com.playshogi.website.gwt.shared.models;

import java.io.Serializable;

@SuppressWarnings("serial")
public class LoginResult implements Serializable {

    private boolean loggedIn = false;
    private String sessionId = null;
    private String userName = null;
    private String errorMessage = null;

    public LoginResult() {
    }

    public LoginResult(final boolean loggedIn, final String sessionId, final String userName,
                       final String errorMessage) {
        this.loggedIn = loggedIn;
        this.sessionId = sessionId;
        this.userName = userName;
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

    @Override
    public String toString() {
        return "LoginResult [loggedIn=" + loggedIn + ", sessionId=" + sessionId + ", userName=" + userName
                + ", errorMessage=" + errorMessage + "]";
    }

}
