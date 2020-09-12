package com.playshogi.website.gwt.client.activity;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.web.bindery.event.shared.EventBus;
import com.playshogi.website.gwt.client.events.ListCollectionGamesEvent;
import com.playshogi.website.gwt.client.events.ListGameCollectionsEvent;
import com.playshogi.website.gwt.client.place.GameCollectionsPlace;
import com.playshogi.website.gwt.client.ui.GameCollectionsView;
import com.playshogi.website.gwt.shared.models.GameCollectionDetails;
import com.playshogi.website.gwt.shared.models.KifuDetails;

public class GameCollectionsActivity extends MyAbstractActivity {

    private final GameCollectionsPlace place;
    private final GameCollectionsView gameCollectionsView;

    public GameCollectionsActivity(GameCollectionsPlace place, final GameCollectionsView gameCollectionsView) {
        this.place = place;
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
            eventBus.fireEvent(new ListGameCollectionsEvent(new GameCollectionDetails[]{details}));
        });

        if (place.getCollectionId().isPresent()) {
            Scheduler.get().scheduleDeferred(() -> {
                KifuDetails details = new KifuDetails();
                details.setId("4883");
                details.setSente("高田尚平");
                details.setGote("久保利明");
                eventBus.fireEvent(new ListCollectionGamesEvent(new KifuDetails[]{details}));
            });

        }

        containerWidget.setWidget(gameCollectionsView.asWidget());
    }

}
