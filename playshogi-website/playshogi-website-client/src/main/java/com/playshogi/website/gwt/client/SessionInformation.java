package com.playshogi.website.gwt.client;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.binder.EventBinder;
import com.google.web.bindery.event.shared.binder.EventHandler;
import com.playshogi.website.gwt.client.events.user.NotationStyleSelectedEvent;
import com.playshogi.website.gwt.client.events.user.PieceStyleSelectedEvent;
import com.playshogi.website.gwt.client.events.user.UserLoggedInEvent;
import com.playshogi.website.gwt.client.events.user.UserLoggedOutEvent;
import com.playshogi.website.gwt.shared.models.LoginResult;
import com.playshogi.website.gwt.shared.services.LoginService;
import com.playshogi.website.gwt.shared.services.LoginServiceAsync;
import org.dominokit.domino.ui.notifications.Notification;

import java.util.Date;

public class SessionInformation implements AsyncCallback<LoginResult> {

    interface MyEventBinder extends EventBinder<SessionInformation> {
    }

    private final MyEventBinder eventBinder = com.google.gwt.core.client.GWT.create(MyEventBinder.class);

    private final LoginServiceAsync loginService = GWT.create(LoginService.class);

    private boolean loggedIn = false;
    private boolean admin = false;
    private String username = null;
    private String sessionId = null;
    private String errorMessage;
    private final UserPreferences userPreferences = new UserPreferences();

    private final EventBus eventBus;

    @Inject
    public SessionInformation(final EventBus eventBus) {
        this.eventBus = eventBus;
        eventBinder.bindEventHandlers(this, eventBus);
        String sid = Cookies.getCookie("sid");
        if (sid != null) {
            loginService.checkSession(sid, this);
        }
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public boolean isAdmin() {
        return admin;
    }

    public String getUsername() {
        if (isLoggedIn()) {
            return username;
        } else {
            return "Guest";
        }
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public UserPreferences getUserPreferences() {
        return userPreferences;
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
        Notification.createDanger("Error trying to login/register to the server.").show();
    }

    @Override
    public void onSuccess(final LoginResult result) {
        GWT.log("Got session validation result: " + result);
        if (result != null) {
            if (result.getErrorMessage() != null) {
                Notification.createDanger(result.getErrorMessage()).show();
            }

            loggedIn = result.isLoggedIn();
            admin = result.isAdmin();
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

    public void ifLoggedIn(final Runnable runnable) {
        if (isLoggedIn()) {
            runnable.run();
        } else {
            Window.alert("This functionality is only available to logged in users - please log-in or register.");
        }
    }

    @EventHandler
    public void onPieceStyleSelected(final PieceStyleSelectedEvent event) {
        userPreferences.setPieceStyle(event.getStyle());
    }

    @EventHandler
    public void onNotationStyleSelected(final NotationStyleSelectedEvent event) {
        userPreferences.setNotationStyle(event.getStyle());
    }

    @Override
    public String toString() {
        return "SessionInformation{" +
                "loggedIn=" + loggedIn +
                ", admin=" + admin +
                ", username='" + username + '\'' +
                ", errorMessage='" + errorMessage + '\'' +
                ", userPreferences=" + userPreferences +
                '}';
    }
}
