package com.playshogi.website.gwt.client.activity;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.web.bindery.event.shared.EventBus;
import com.playshogi.website.gwt.client.SessionInformation;
import com.playshogi.website.gwt.client.events.ListCollectionGamesEvent;
import com.playshogi.website.gwt.client.events.ListGameCollectionsEvent;
import com.playshogi.website.gwt.client.place.GameCollectionsPlace;
import com.playshogi.website.gwt.client.ui.GameCollectionsView;
import com.playshogi.website.gwt.shared.models.GameCollectionDetails;
import com.playshogi.website.gwt.shared.models.KifuDetails;
import com.playshogi.website.gwt.shared.services.KifuService;
import com.playshogi.website.gwt.shared.services.KifuServiceAsync;

public class GameCollectionsActivity extends MyAbstractActivity {

    private final KifuServiceAsync kifuService = GWT.create(KifuService.class);

    private final GameCollectionsPlace place;
    private final GameCollectionsView gameCollectionsView;
    private final SessionInformation sessionInformation;

    public GameCollectionsActivity(final GameCollectionsPlace place, final GameCollectionsView gameCollectionsView,
                                   SessionInformation sessionInformation) {
        this.place = place;
        this.gameCollectionsView = gameCollectionsView;
        this.sessionInformation = sessionInformation;
    }

    @Override
    public void start(final AcceptsOneWidget containerWidget, final EventBus eventBus) {
        GWT.log("Starting game collections activity");

        gameCollectionsView.activate(eventBus);
        Scheduler.get().scheduleDeferred(() -> {
            GameCollectionDetails details = new GameCollectionDetails();
            details.setId("1");
            details.setName("57k Pro Games");
            eventBus.fireEvent(new ListGameCollectionsEvent(new GameCollectionDetails[]{details}));
        });

        if (place.getCollectionId().isPresent()) {
            GWT.log("Querying for collection games");
            kifuService.getGameSetKifuDetails(sessionInformation.getSessionId(), place.getCollectionId().get(),
                    new AsyncCallback<KifuDetails[]>() {
                @Override
                public void onFailure(Throwable throwable) {
                    GWT.log("GameCollectionsActivity: error retrieving collection games");
                }

                @Override
                public void onSuccess(KifuDetails[] kifuDetails) {
                    GWT.log("GameCollectionsActivity: retrieved collection games");
                    eventBus.fireEvent(new ListCollectionGamesEvent(kifuDetails));
                }
            });

        }

        containerWidget.setWidget(gameCollectionsView.asWidget());
    }

}
