package com.playshogi.website.gwt.client.activity;

import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.binder.EventBinder;
import com.google.web.bindery.event.shared.binder.EventHandler;
import com.playshogi.website.gwt.client.SessionInformation;
import com.playshogi.website.gwt.client.events.user.UserLoggedInEvent;
import com.playshogi.website.gwt.client.events.user.UserLoggedOutEvent;
import com.playshogi.website.gwt.client.place.LoginPlace;
import com.playshogi.website.gwt.client.place.ProblemStatisticsPlace;
import com.playshogi.website.gwt.client.place.UserKifusPlace;
import com.playshogi.website.gwt.client.ui.LoginView;

public class LoginActivity extends MyAbstractActivity {

    interface MyEventBinder extends EventBinder<LoginActivity> {
    }

    private final MyEventBinder eventBinder = GWT.create(MyEventBinder.class);

    private final LoginView loginView;
    private final LoginPlace place;
    private final SessionInformation sessionInformation;
    private final PlaceController placeController;

    public LoginActivity(final LoginPlace place, final LoginView loginView,
                         final SessionInformation sessionInformation, final PlaceController placeController) {
        this.place = place;
        this.loginView = loginView;
        this.sessionInformation = sessionInformation;
        this.placeController = placeController;
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
       placeController.goTo(new ProblemStatisticsPlace());
    }

    @EventHandler
    public void onUserLoggedOut(final UserLoggedOutEvent event) {
    }

}
