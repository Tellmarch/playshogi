package com.playshogi.website.gwt.client.activity;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.web.bindery.event.shared.EventBus;
import com.playshogi.website.gwt.client.ui.GameCollectionsView;

public class GameCollectionsActivity extends MyAbstractActivity {

    private final GameCollectionsView gameCollectionsView;

    public GameCollectionsActivity(final GameCollectionsView gameCollectionsView) {
        this.gameCollectionsView = gameCollectionsView;
    }

    @Override
    public void start(final AcceptsOneWidget containerWidget, final EventBus eventBus) {
        GWT.log("Starting game collections activity");
        containerWidget.setWidget(gameCollectionsView.asWidget());
    }

}
