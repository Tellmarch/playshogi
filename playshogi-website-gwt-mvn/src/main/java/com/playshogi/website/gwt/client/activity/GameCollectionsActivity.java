package com.playshogi.website.gwt.client.activity;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.web.bindery.event.shared.EventBus;
import com.playshogi.website.gwt.client.events.GameCollectionsEvent;
import com.playshogi.website.gwt.client.ui.GameCollectionsView;
import com.playshogi.website.gwt.shared.models.GameCollectionDetails;

public class GameCollectionsActivity extends MyAbstractActivity {

    private final GameCollectionsView gameCollectionsView;

    public GameCollectionsActivity(final GameCollectionsView gameCollectionsView) {
        this.gameCollectionsView = gameCollectionsView;
    }

    @Override
    public void start(final AcceptsOneWidget containerWidget, final EventBus eventBus) {
        GWT.log("Starting game collections activity");

        gameCollectionsView.activate(eventBus);
        Scheduler.get().scheduleDeferred(() -> {
            GameCollectionDetails details = new GameCollectionDetails();
            details.setId("2");
            details.setName("57k Pro Games");
            eventBus.fireEvent(new GameCollectionsEvent(new GameCollectionDetails[]{details}));
        });

        containerWidget.setWidget(gameCollectionsView.asWidget());
    }

}
