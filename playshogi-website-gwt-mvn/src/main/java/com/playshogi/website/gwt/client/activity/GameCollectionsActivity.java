package com.playshogi.website.gwt.client.activity;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.binder.EventBinder;
import com.google.web.bindery.event.shared.binder.EventHandler;
import com.playshogi.website.gwt.client.SessionInformation;
import com.playshogi.website.gwt.client.events.DraftCollectionUploadedEvent;
import com.playshogi.website.gwt.client.events.collections.*;
import com.playshogi.website.gwt.client.place.GameCollectionsPlace;
import com.playshogi.website.gwt.client.ui.GameCollectionsView;
import com.playshogi.website.gwt.shared.models.GameCollectionDetails;
import com.playshogi.website.gwt.shared.models.KifuDetails;
import com.playshogi.website.gwt.shared.services.KifuService;
import com.playshogi.website.gwt.shared.services.KifuServiceAsync;

public class GameCollectionsActivity extends MyAbstractActivity {

    private final KifuServiceAsync kifuService = GWT.create(KifuService.class);
    private EventBus eventBus;

    interface MyEventBinder extends EventBinder<GameCollectionsActivity> {
    }

    private final MyEventBinder eventBinder = GWT.create(MyEventBinder.class);

    private final GameCollectionsPlace place;
    private final GameCollectionsView gameCollectionsView;
    private final SessionInformation sessionInformation;

    public GameCollectionsActivity(final GameCollectionsPlace place, final GameCollectionsView gameCollectionsView,
                                   final SessionInformation sessionInformation) {
        this.place = place;
        this.gameCollectionsView = gameCollectionsView;
        this.sessionInformation = sessionInformation;
    }

    @Override
    public void start(final AcceptsOneWidget containerWidget, final EventBus eventBus) {
        GWT.log("Starting game collections activity");
        this.eventBus = eventBus;
        eventBinder.bindEventHandlers(this, eventBus);
        gameCollectionsView.activate(eventBus);

        kifuService.getGameCollections(sessionInformation.getSessionId(), new AsyncCallback<GameCollectionDetails[]>() {
            @Override
            public void onFailure(Throwable throwable) {
                GWT.log("GameCollectionsActivity: error retrieving collections list");
            }

            @Override
            public void onSuccess(GameCollectionDetails[] gameCollectionDetails) {
                GWT.log("GameCollectionsActivity: retrieved collections list");
                eventBus.fireEvent(new ListGameCollectionsEvent(gameCollectionDetails));
            }
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

    @EventHandler
    public void onDraftCollectionUploaded(final DraftCollectionUploadedEvent event) {
        GWT.log("GameCollectionsActivity: Handling DraftCollectionUploadedEvent");
        kifuService.saveGameCollection(sessionInformation.getSessionId(), event.getId(), new AsyncCallback<String>() {
            @Override
            public void onFailure(final Throwable throwable) {
                GWT.log("GameCollectionsActivity: error saving draft collection");
            }

            @Override
            public void onSuccess(final String s) {
                GWT.log("GameCollectionsActivity: saved draft collection");
            }
        });
    }

    @EventHandler
    public void onSaveGameCollectionDetails(final SaveGameCollectionDetailsEvent event) {
        GWT.log("GameCollectionsActivity: Handling SaveGameCollectionDetailsEvent: " + event.getDetails());
        kifuService.updateGameCollectionDetails(sessionInformation.getSessionId(), event.getDetails(),
                new AsyncCallback<Void>() {
                    @Override
                    public void onFailure(final Throwable throwable) {
                        GWT.log("GameCollectionsActivity: error during saveGameCollectionDetails");
                        eventBus.fireEvent(new SaveGameCollectionDetailsResultEvent(false));
                    }

                    @Override
                    public void onSuccess(final Void unused) {
                        GWT.log("GameCollectionsActivity: saveGameCollectionDetails success");
                        eventBus.fireEvent(new SaveGameCollectionDetailsResultEvent(true));
                    }
                });
    }

    @EventHandler
    public void onCreateGameCollection(final CreateGameCollectionEvent event) {
        GWT.log("GameCollectionsActivity: Handling CreateGameCollectionEvent: " + event.getDetails());
        kifuService.createGameCollection(sessionInformation.getSessionId(), event.getDetails(),
                new AsyncCallback<Void>() {
                    @Override
                    public void onFailure(final Throwable throwable) {
                        GWT.log("GameCollectionsActivity: error during createGameCollection");
                        eventBus.fireEvent(new SaveGameCollectionDetailsResultEvent(false));
                    }

                    @Override
                    public void onSuccess(final Void unused) {
                        GWT.log("GameCollectionsActivity: createGameCollection success");
                        eventBus.fireEvent(new SaveGameCollectionDetailsResultEvent(true));
                    }
                });
    }

}
