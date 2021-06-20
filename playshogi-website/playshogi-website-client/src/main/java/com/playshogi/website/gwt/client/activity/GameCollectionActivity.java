package com.playshogi.website.gwt.client.activity;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.binder.EventBinder;
import com.google.web.bindery.event.shared.binder.EventHandler;
import com.playshogi.library.shogi.models.formats.usf.UsfFormat;
import com.playshogi.website.gwt.client.SessionInformation;
import com.playshogi.website.gwt.client.events.collections.ListCollectionGamesEvent;
import com.playshogi.website.gwt.client.events.collections.RemoveGameFromCollectionEvent;
import com.playshogi.website.gwt.client.events.collections.SaveCollectionDetailsResultEvent;
import com.playshogi.website.gwt.client.events.collections.SaveGameCollectionDetailsEvent;
import com.playshogi.website.gwt.client.events.kifu.ImportGameRecordEvent;
import com.playshogi.website.gwt.client.events.user.UserLoggedInEvent;
import com.playshogi.website.gwt.client.place.GameCollectionPlace;
import com.playshogi.website.gwt.client.ui.GameCollectionView;
import com.playshogi.website.gwt.shared.models.GameCollectionDetailsAndGames;
import com.playshogi.website.gwt.shared.services.KifuService;
import com.playshogi.website.gwt.shared.services.KifuServiceAsync;
import org.dominokit.domino.ui.notifications.Notification;

public class GameCollectionActivity extends MyAbstractActivity {

    private final KifuServiceAsync kifuService = GWT.create(KifuService.class);
    private EventBus eventBus;

    interface MyEventBinder extends EventBinder<GameCollectionActivity> {
    }

    private final MyEventBinder eventBinder = GWT.create(MyEventBinder.class);

    private final GameCollectionPlace place;
    private final GameCollectionView view;
    private final SessionInformation sessionInformation;

    public GameCollectionActivity(final GameCollectionPlace place, final GameCollectionView view,
                                  final SessionInformation sessionInformation) {
        this.place = place;
        this.view = view;
        this.sessionInformation = sessionInformation;
    }

    @Override
    public void start(final AcceptsOneWidget containerWidget, final EventBus eventBus) {
        GWT.log("Starting game collection activity");
        this.eventBus = eventBus;
        eventBinder.bindEventHandlers(this, eventBus);
        view.activate(eventBus);

        fetchData();

        containerWidget.setWidget(view.asWidget());
    }

    private void fetchData() {
        GWT.log("Querying for collection games");
        kifuService.getGameSetKifuDetails(sessionInformation.getSessionId(), place.getCollectionId(),
                new AsyncCallback<GameCollectionDetailsAndGames>() {
                    @Override
                    public void onFailure(Throwable throwable) {
                        GWT.log("GameCollectionActivity: error retrieving collection games");
                    }

                    @Override
                    public void onSuccess(GameCollectionDetailsAndGames result) {
                        GWT.log("GameCollectionActivity: retrieved collection games");
                        eventBus.fireEvent(new ListCollectionGamesEvent(result.getGames(), result.getDetails()));
                    }
                });
    }

    @EventHandler
    public void onSaveGameCollectionDetails(final SaveGameCollectionDetailsEvent event) {
        GWT.log("GameCollectionActivity: Handling SaveGameCollectionDetailsEvent: " + event.getDetails());
        kifuService.updateGameCollectionDetails(sessionInformation.getSessionId(), event.getDetails(),
                new AsyncCallback<Void>() {
                    @Override
                    public void onFailure(final Throwable throwable) {
                        GWT.log("GameCollectionActivity: error during saveGameCollectionDetails");
                        eventBus.fireEvent(new SaveCollectionDetailsResultEvent(false));
                    }

                    @Override
                    public void onSuccess(final Void unused) {
                        GWT.log("GameCollectionActivity: saveGameCollectionDetails success");
                        eventBus.fireEvent(new SaveCollectionDetailsResultEvent(true));
                        refresh();
                    }
                });
    }

    @EventHandler
    public void onImportGameRecord(final ImportGameRecordEvent event) {
        GWT.log("GameCollectionActivity Handling ImportGameRecordEvent");

        kifuService.saveGameAndAddToCollection(sessionInformation.getSessionId(),
                UsfFormat.INSTANCE.write(event.getGameRecord()),
                event.getCollectionId(), new AsyncCallback<Void>() {
                    @Override
                    public void onFailure(final Throwable throwable) {
                        GWT.log("GameCollectionActivity: error during saveKifu");
                    }

                    @Override
                    public void onSuccess(final Void unused) {
                        GWT.log("GameCollectionActivity: saveKifu success");
                        refresh();
                    }
                });

    }

    @EventHandler
    public void onUserLoggedIn(final UserLoggedInEvent event) {
        refresh();
    }

    @EventHandler
    public void onRemoveGameFromCollection(final RemoveGameFromCollectionEvent event) {
        GWT.log("GameCollectionActivity Handling RemoveGameFromCollectionEvent");
        kifuService.removeGameFromCollection(sessionInformation.getSessionId(), event.getGameId(),
                place.getCollectionId(),
                new AsyncCallback<Void>() {
                    @Override
                    public void onFailure(final Throwable throwable) {
                        GWT.log("GameCollectionActivity: error during removeGameFromCollection");
                        Notification.createDanger("Deletion failed - maybe you do not have permission?").show();
                    }

                    @Override
                    public void onSuccess(final Void unused) {
                        GWT.log("GameCollectionActivity: removeGameFromCollection success");
                        refresh();
                    }
                });
    }

    private void refresh() {
        fetchData();
    }

}
