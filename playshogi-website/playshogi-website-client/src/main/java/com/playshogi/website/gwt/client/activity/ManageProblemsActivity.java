package com.playshogi.website.gwt.client.activity;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.binder.EventBinder;
import com.google.web.bindery.event.shared.binder.EventHandler;
import com.playshogi.website.gwt.client.SessionInformation;
import com.playshogi.website.gwt.client.events.collections.DraftCollectionUploadedEvent;
import com.playshogi.website.gwt.client.events.collections.ListGameCollectionsEvent;
import com.playshogi.website.gwt.client.events.collections.ListKifusEvent;
import com.playshogi.website.gwt.client.events.collections.RequestAddKifuToCollectionEvent;
import com.playshogi.website.gwt.client.events.kifu.RequestKifuDeletionEvent;
import com.playshogi.website.gwt.client.events.user.UserLoggedInEvent;
import com.playshogi.website.gwt.client.place.ManageProblemsPlace;
import com.playshogi.website.gwt.client.ui.ManageProblemsView;
import com.playshogi.website.gwt.shared.models.GameCollectionDetailsList;
import com.playshogi.website.gwt.shared.models.KifuDetails;
import com.playshogi.website.gwt.shared.services.KifuService;
import com.playshogi.website.gwt.shared.services.KifuServiceAsync;
import com.playshogi.website.gwt.shared.services.ProblemsService;
import com.playshogi.website.gwt.shared.services.ProblemsServiceAsync;

public class ManageProblemsActivity extends MyAbstractActivity {


    private final KifuServiceAsync kifuService = GWT.create(KifuService.class);
    private final ProblemsServiceAsync problemsService = GWT.create(ProblemsService.class);
    private EventBus eventBus;

    interface MyEventBinder extends EventBinder<ManageProblemsActivity> {
    }

    private final MyEventBinder eventBinder = GWT.create(MyEventBinder.class);

    private final ManageProblemsPlace place;
    private final ManageProblemsView view;
    private final SessionInformation sessionInformation;

    public ManageProblemsActivity(final ManageProblemsPlace place, final ManageProblemsView view,
                                  final SessionInformation sessionInformation) {
        this.place = place;
        this.view = view;
        this.sessionInformation = sessionInformation;
    }

    @Override
    public void start(final AcceptsOneWidget containerWidget, final EventBus eventBus) {
        GWT.log("Starting ManageProblemsActivity");
        this.eventBus = eventBus;
        eventBinder.bindEventHandlers(this, eventBus);
        view.activate(eventBus);

        if (sessionInformation.isLoggedIn()) {
            fetchData();
        }

        containerWidget.setWidget(view.asWidget());
    }

    private void fetchData() {
        kifuService.getUserKifus(sessionInformation.getSessionId(), sessionInformation.getUsername(),
                new AsyncCallback<KifuDetails[]>() {
                    @Override
                    public void onFailure(final Throwable throwable) {
                        GWT.log("UserKifusActivity: error retrieving kifus list");
                    }

                    @Override
                    public void onSuccess(final KifuDetails[] kifuDetails) {
                        GWT.log("UserKifusActivity: retrieved kifus list");
                        eventBus.fireEvent(new ListKifusEvent(kifuDetails));
                    }
                });

        kifuService.getGameCollections(sessionInformation.getSessionId(),
                new AsyncCallback<GameCollectionDetailsList>() {
                    @Override
                    public void onFailure(Throwable throwable) {
                        GWT.log("GameCollectionsActivity: error retrieving collections list");
                    }

                    @Override
                    public void onSuccess(GameCollectionDetailsList gameCollectionDetails) {
                        GWT.log("GameCollectionsActivity: retrieved collections list");
                        eventBus.fireEvent(new ListGameCollectionsEvent(gameCollectionDetails.getMyCollections(),
                                gameCollectionDetails.getPublicCollections()));
                    }
                });
    }

    private void refresh() {
        fetchData();
    }

    @EventHandler
    public void onUserLoggedIn(final UserLoggedInEvent event) {
        refresh();
    }

    @EventHandler
    public void onRequestKifuDeletionEvent(final RequestKifuDeletionEvent event) {
        kifuService.deleteKifu(sessionInformation.getSessionId(), event.getKifuId(), new AsyncCallback<Void>() {
            @Override
            public void onFailure(final Throwable throwable) {
                GWT.log("UserKifusActivity: error deleting kifu");
                Window.alert("Could not delete the Kifu - It should be removed from any collection first");
            }

            @Override
            public void onSuccess(final Void unused) {
                GWT.log("UserKifusActivity: successfully deleted kifu");
                refresh();
            }
        });
    }

    @EventHandler
    public void onRequestAddKifuToCollection(final RequestAddKifuToCollectionEvent event) {
        kifuService.addExistingKifuToCollection(sessionInformation.getSessionId(), event.getKifuId(),
                event.getCollectionId(), new AsyncCallback<Void>() {
                    @Override
                    public void onFailure(final Throwable throwable) {
                        GWT.log("onRequestAddKifuToCollection: error adding kifu to collection");
                        Window.alert("Error: Could not add the kifu to the collection.");
                    }

                    @Override
                    public void onSuccess(final Void unused) {
                        GWT.log("onRequestAddKifuToCollection: added kifu to collection");
                        Window.alert("Kifu successfully added to collection.");
                    }
                });
    }

    @EventHandler
    public void onDraftCollectionUploaded(final DraftCollectionUploadedEvent event) {
        GWT.log("ManageProblemsActivity: Handling DraftCollectionUploadedEvent");
        Window.alert("Your collection is uploading - it may take a few minutes to import all games to the database. " +
                "You can keep using the website during that time.");
        problemsService.saveProblemsCollection(sessionInformation.getSessionId(), event.getId(),
                new AsyncCallback<String>() {
                    @Override
                    public void onFailure(final Throwable throwable) {
                        GWT.log("ManageProblemsActivity: error saving draft collection");
                        Window.alert("Failed to upload the problems collection.");
                    }

                    @Override
                    public void onSuccess(final String s) {
                        GWT.log("ManageProblemsActivity: saved draft collection");
                        refresh();
                    }
                });
    }

}
