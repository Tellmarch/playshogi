package com.playshogi.website.gwt.client.activity;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.binder.EventBinder;
import com.google.web.bindery.event.shared.binder.EventHandler;
import com.playshogi.website.gwt.client.SessionInformation;
import com.playshogi.website.gwt.client.events.collections.*;
import com.playshogi.website.gwt.client.events.user.UserLoggedInEvent;
import com.playshogi.website.gwt.client.place.PublicCollectionsPlace;
import com.playshogi.website.gwt.client.ui.PublicCollectionsView;
import com.playshogi.website.gwt.shared.models.GameCollectionDetails;
import com.playshogi.website.gwt.shared.models.GameCollectionDetailsList;
import com.playshogi.website.gwt.shared.services.KifuService;
import com.playshogi.website.gwt.shared.services.KifuServiceAsync;

public class PublicCollectionsActivity extends MyAbstractActivity {

    private final KifuServiceAsync kifuService = GWT.create(KifuService.class);
    private EventBus eventBus;

    interface MyEventBinder extends EventBinder<PublicCollectionsActivity> {
    }

    private final MyEventBinder eventBinder = GWT.create(MyEventBinder.class);

    private final PublicCollectionsPlace place;
    private final PublicCollectionsView view;
    private final SessionInformation sessionInformation;

    public PublicCollectionsActivity(final PublicCollectionsPlace place, final PublicCollectionsView view,
                                     final SessionInformation sessionInformation) {
        this.place = place;
        this.view = view;
        this.sessionInformation = sessionInformation;
    }

    @Override
    public void start(final AcceptsOneWidget containerWidget, final EventBus eventBus) {
        GWT.log("Starting public collections activity");
        this.eventBus = eventBus;
        eventBinder.bindEventHandlers(this, eventBus);
        view.activate(eventBus);

        fetchData();

        containerWidget.setWidget(view.asWidget());
    }

    private void fetchData() {
        kifuService.getGameCollections(sessionInformation.getSessionId(),
                new AsyncCallback<GameCollectionDetailsList>() {
                    @Override
                    public void onFailure(Throwable throwable) {
                        GWT.log("PublicCollectionsActivity: error retrieving collections list");
                    }

                    @Override
                    public void onSuccess(GameCollectionDetailsList gameCollectionDetails) {
                        GWT.log("PublicCollectionsActivity: retrieved collections list");
                        GameCollectionDetails[] publicCollections = gameCollectionDetails.getPublicCollections();
                        for (int i = 0; i < publicCollections.length; i++) {
                            publicCollections[i].setRow(i+1);
                        }
                        eventBus.fireEvent(new ListGameCollectionsEvent(gameCollectionDetails.getMyCollections(),
                                publicCollections));
                    }
                });
    }

    @EventHandler
    public void onUserLoggedIn(final UserLoggedInEvent event) {
        refresh();
    }

    private void refresh() {
        fetchData();
    }

}
