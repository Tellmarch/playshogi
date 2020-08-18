package com.playshogi.website.gwt.client;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.playshogi.website.gwt.client.events.UserLoggedInEvent;
import com.playshogi.website.gwt.client.events.UserLoggedOutEvent;
import com.playshogi.website.gwt.shared.models.LoginResult;
import com.playshogi.website.gwt.shared.services.LoginService;
import com.playshogi.website.gwt.shared.services.LoginServiceAsync;

import java.util.Date;

public class SessionInformation implements AsyncCallback<LoginResult> {

    private final LoginServiceAsync loginService = GWT.create(LoginService.class);

    private boolean loggedIn = false;
    private String username = null;
    private String sessionId = null;
    private String errorMessage;

    private final EventBus eventBus;

    @Inject
    public SessionInformation(final EventBus eventBus) {
        this.eventBus = eventBus;
        String sid = Cookies.getCookie("sid");
        if (sid != null) {
            loginService.checkSession(sid, this);
        }
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public String getUsername() {
        return username;
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    @Override
    public String toString() {
        return "SessionInformation [loggedIn=" + loggedIn + ", username=" + username + ", sessionId=" + sessionId
                + ", errorMessage=" + errorMessage + "]";
    }

    public void logout() {
        GWT.log("Logging out");
        if (sessionId != null) {
            loginService.logout(sessionId, this);
        }
    }

    @Override
    public void onFailure(final Throwable caught) {
        GWT.log("ERROR validating session", caught);
    }

    @Override
    public void onSuccess(final LoginResult result) {
        GWT.log("Got session validation result: " + result);
        if (result != null) {
            loggedIn = result.isLoggedIn();
            sessionId = result.getSessionId();
            username = result.getUserName();
            errorMessage = result.getErrorMessage();
            if (loggedIn) {

                String sessionID = result.getSessionId();
                final long DURATION = 1000 * 60 * 60 * 24 * 14;
                Date expires = new Date(System.currentTimeMillis() + DURATION);
                Cookies.setCookie("sid", sessionID, expires, null, "/", false);

                eventBus.fireEvent(new UserLoggedInEvent());
            } else {
                eventBus.fireEvent(new UserLoggedOutEvent());
            }
        } else {
            eventBus.fireEvent(new UserLoggedOutEvent());
        }
    }

    public void login(final String username, final String password) {
        GWT.log("Logging in as " + username);
        loginService.login(username, password, this);
    }

    public void register(final String username, final String password) {
        GWT.log("Registering as " + username);
        loginService.register(username, password, this);
    }
}
