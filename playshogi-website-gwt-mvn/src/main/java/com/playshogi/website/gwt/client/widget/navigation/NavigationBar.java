package com.playshogi.website.gwt.client.widget.navigation;

import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.binder.EventBinder;
import com.google.web.bindery.event.shared.binder.EventHandler;
import com.playshogi.website.gwt.client.SessionInformation;
import com.playshogi.website.gwt.client.events.UserLoggedInEvent;
import com.playshogi.website.gwt.client.events.UserLoggedOutEvent;
import com.playshogi.website.gwt.client.mvp.AppPlaceHistoryMapper;
import com.playshogi.website.gwt.client.place.*;

@Singleton
public class NavigationBar extends Composite {

    interface MyEventBinder extends EventBinder<NavigationBar> {
    }

    private final MyEventBinder eventBinder = GWT.create(MyEventBinder.class);

    private final AppPlaceHistoryMapper historyMapper;

    private final Hyperlink loginHyperlink;
    private final Hyperlink logoutHyperlink;

    private final SessionInformation sessionInformation;

    private final FlowPanel flowPanel;

    @Inject
    public NavigationBar(final AppPlaceHistoryMapper historyMapper, final SessionInformation sessionInformation, final EventBus eventBus) {
        GWT.log("Creating navigation bar");

        this.historyMapper = historyMapper;
        this.sessionInformation = sessionInformation;
        flowPanel = new FlowPanel();
        flowPanel.setStyleName("contentButtons");

        flowPanel.add(createHyperlink("Main page", new MainPagePlace()));
        flowPanel.add(createHyperlink("How to Play", new MainPagePlace()));
        flowPanel.add(createHyperlink("Problems", new TsumePlace()));
        flowPanel.add(createHyperlink("Openings", new OpeningsPlace()));
        flowPanel.add(createHyperlink("My Games", new MyGamesPlace()));
        flowPanel.add(createHyperlink("Free Board", new FreeBoardPlace()));
        flowPanel.add(createHyperlink("Play Online", new MainPagePlace()));
        flowPanel.add(createHyperlink("About", new MainPagePlace()));

        loginHyperlink = createHyperlink("Login/Register", new LoginPlace("login"));
        logoutHyperlink = createHyperlink("Logout", new LoginPlace("logout"));
        flowPanel.add(loginHyperlink);

        eventBinder.bindEventHandlers(this, eventBus);

        initWidget(flowPanel);
    }

    private String getLogoutText(final SessionInformation sessionInformation) {
        return "Logout [" + sessionInformation.getUsername() + "]";
    }

    private Hyperlink createHyperlink(final String title, final Place place) {
        Hyperlink hyperlink = new Hyperlink(title, historyMapper.getToken(place));
        hyperlink.setStyleName("contentButton");
        return hyperlink;
    }

    @EventHandler
    public void onUserLoggedIn(final UserLoggedInEvent event) {
        logoutHyperlink.setText(getLogoutText(sessionInformation));
        flowPanel.remove(loginHyperlink);
        flowPanel.add(logoutHyperlink);
    }

    @EventHandler
    public void onUserLoggedOut(final UserLoggedOutEvent event) {
        flowPanel.remove(logoutHyperlink);
        flowPanel.add(loginHyperlink);
    }
}
