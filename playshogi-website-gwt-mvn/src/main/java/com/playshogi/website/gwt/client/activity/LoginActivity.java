package com.playshogi.website.gwt.client.activity;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.binder.EventBinder;
import com.google.web.bindery.event.shared.binder.EventHandler;
import com.playshogi.website.gwt.client.SessionInformation;
import com.playshogi.website.gwt.client.events.UserLoggedInEvent;
import com.playshogi.website.gwt.client.events.UserLoggedOutEvent;
import com.playshogi.website.gwt.client.place.LoginPlace;
import com.playshogi.website.gwt.client.ui.LoginView;

public class LoginActivity extends MyAbstractActivity {

    interface MyEventBinder extends EventBinder<LoginActivity> {
    }

    private final MyEventBinder eventBinder = GWT.create(MyEventBinder.class);

    private final LoginView loginView;
    private final LoginPlace place;
    private final SessionInformation sessionInformation;

    public LoginActivity(final LoginPlace place, final LoginView loginView,
                         final SessionInformation sessionInformation) {
        this.place = place;
        this.loginView = loginView;
        this.sessionInformation = sessionInformation;
    }

    @Override
    public void start(final AcceptsOneWidget containerWidget, final EventBus eventBus) {
        GWT.log("Starting login activity");
        eventBinder.bindEventHandlers(this, eventBus);

        if ("logout".equals(place.getAction())) {
            sessionInformation.logout();
        }
        containerWidget.setWidget(loginView.asWidget());
    }

    @EventHandler
    public void onUserLoggedIn(final UserLoggedInEvent event) {
        loginView.setLoginVisible(false);
        loginView.setInfoText("Logged in as " + sessionInformation.getUsername());
    }

    @EventHandler
    public void onUserLoggedOut(final UserLoggedOutEvent event) {
        loginView.setLoginVisible(true);
        loginView.setInfoText(sessionInformation.getErrorMessage());
    }

}
