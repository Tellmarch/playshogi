package com.playshogi.website.gwt.client.activity;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.web.bindery.event.shared.EventBus;
import com.playshogi.website.gwt.client.ui.MyGamesView;

public class MyGamesActivity extends MyAbstractActivity {

    private final MyGamesView myGamesView;

    public MyGamesActivity(final MyGamesView loginView) {
        this.myGamesView = loginView;
    }

    @Override
    public void start(final AcceptsOneWidget containerWidget, final EventBus eventBus) {
        GWT.log("Starting my games activity");
        containerWidget.setWidget(myGamesView.asWidget());
    }

}
