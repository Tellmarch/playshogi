package com.playshogi.website.gwt.client.activity;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.binder.EventBinder;
import com.google.web.bindery.event.shared.binder.EventHandler;
import com.playshogi.website.gwt.client.SessionInformation;
import com.playshogi.website.gwt.client.events.collections.ListGameCollectionsEvent;
import com.playshogi.website.gwt.client.events.collections.ListKifusEvent;
import com.playshogi.website.gwt.client.events.collections.ListProblemCollectionsEvent;
import com.playshogi.website.gwt.client.events.collections.RequestAddKifuToCollectionEvent;
import com.playshogi.website.gwt.client.events.kifu.RequestKifuDeletionEvent;
import com.playshogi.website.gwt.client.events.user.UserLoggedInEvent;
import com.playshogi.website.gwt.client.place.UserKifusPlace;
import com.playshogi.website.gwt.client.ui.UserKifusView;
import com.playshogi.website.gwt.shared.models.GameCollectionDetails;
import com.playshogi.website.gwt.shared.models.KifuDetails;
import com.playshogi.website.gwt.shared.models.ProblemCollectionDetails;
import com.playshogi.website.gwt.shared.services.KifuService;
import com.playshogi.website.gwt.shared.services.KifuServiceAsync;
import com.playshogi.website.gwt.shared.services.ProblemsService;
import com.playshogi.website.gwt.shared.services.ProblemsServiceAsync;

public class UserKifusActivity extends MyAbstractActivity {

    private final KifuServiceAsync kifuService = GWT.create(KifuService.class);
    private final ProblemsServiceAsync problemService = GWT.create(ProblemsService.class);

    private EventBus eventBus;

    interface MyEventBinder extends EventBinder<UserKifusActivity> {
    }

    private final MyEventBinder eventBinder = GWT.create(MyEventBinder.class);

    private final UserKifusPlace place;
    private final UserKifusView view;
    private final SessionInformation sessionInformation;

    public UserKifusActivity(final UserKifusPlace place, final UserKifusView view,
                             final SessionInformation sessionInformation) {
        this.place = place;
        this.view = view;
        this.sessionInformation = sessionInformation;
    }

    @Override
    public void start(final AcceptsOneWidget containerWidget, final EventBus eventBus) {
        GWT.log("Starting user kifus activity");
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

        kifuService.getUserGameCollections(sessionInformation.getSessionId(), sessionInformation.getUsername(),
                new AsyncCallback<GameCollectionDetails[]>() {
                    @Override
                    public void onFailure(Throwable throwable) {
                        GWT.log("MyCollectionsActivity: error retrieving collections list");
                    }

                    @Override
                    public void onSuccess(GameCollectionDetails[] gameCollectionDetails) {
                        GWT.log("MyCollectionsActivity: retrieved collections list");
                        eventBus.fireEvent(new ListGameCollectionsEvent(gameCollectionDetails, null));
                    }
                });

        problemService.getUserProblemCollections(sessionInformation.getSessionId(), sessionInformation.getUsername(),
                new AsyncCallback<ProblemCollectionDetails[]>() {
                    @Override
                    public void onFailure(Throwable throwable) {
                        GWT.log("MyCollectionsActivity: error retrieving collections list");
                    }

                    @Override
                    public void onSuccess(ProblemCollectionDetails[] collectionDetails) {
                        GWT.log("MyCollectionsActivity: retrieved collections list");
                        eventBus.fireEvent(new ListProblemCollectionsEvent(null, collectionDetails));
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
        AsyncCallback<Void> callback = new AsyncCallback<Void>() {
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
        };
        if (event.getType() == KifuDetails.KifuType.PROBLEM) {
            problemService.addExistingKifuToProblemCollection(sessionInformation.getSessionId(), event.getKifuId(),
                    event.getCollectionId(), callback);
        } else {
            kifuService.addExistingKifuToCollection(sessionInformation.getSessionId(), event.getKifuId(),
                    event.getCollectionId(), callback);
        }
    }

}
