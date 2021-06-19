package com.playshogi.website.gwt.client.activity;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.binder.EventBinder;
import com.playshogi.website.gwt.client.SessionInformation;
import com.playshogi.website.gwt.client.events.collections.ListGameCollectionsEvent;
import com.playshogi.website.gwt.client.events.collections.ListProblemCollectionsEvent;
import com.playshogi.website.gwt.client.place.PublicCollectionsPlace;
import com.playshogi.website.gwt.client.ui.PublicCollectionsView;
import com.playshogi.website.gwt.shared.models.GameCollectionDetails;
import com.playshogi.website.gwt.shared.models.ProblemCollectionDetails;
import com.playshogi.website.gwt.shared.services.KifuService;
import com.playshogi.website.gwt.shared.services.KifuServiceAsync;
import com.playshogi.website.gwt.shared.services.ProblemsService;
import com.playshogi.website.gwt.shared.services.ProblemsServiceAsync;

public class PublicCollectionsActivity extends MyAbstractActivity {

    private final KifuServiceAsync kifuService = GWT.create(KifuService.class);
    private final ProblemsServiceAsync problemsService = GWT.create(ProblemsService.class);

    private EventBus eventBus;

    interface MyEventBinder extends EventBinder<PublicCollectionsActivity> {
    }

    private final MyEventBinder eventBinder = GWT.create(MyEventBinder.class);

    private final PublicCollectionsView view;
    private final SessionInformation sessionInformation;

    public PublicCollectionsActivity(final PublicCollectionsPlace place, final PublicCollectionsView view,
                                     final SessionInformation sessionInformation) {
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
        kifuService.getPublicGameCollections(sessionInformation.getSessionId(),
                new AsyncCallback<GameCollectionDetails[]>() {
                    @Override
                    public void onFailure(Throwable throwable) {
                        GWT.log("PublicCollectionsActivity: error retrieving game collections list");
                    }

                    @Override
                    public void onSuccess(GameCollectionDetails[] gameCollectionDetails) {
                        GWT.log("PublicCollectionsActivity: retrieved game collections list");
                        eventBus.fireEvent(new ListGameCollectionsEvent(null,
                                gameCollectionDetails));
                    }
                });
        problemsService.getPublicProblemCollections(sessionInformation.getSessionId(),
                new AsyncCallback<ProblemCollectionDetails[]>() {
                    @Override
                    public void onFailure(final Throwable throwable) {
                        GWT.log("PublicCollectionsActivity: error retrieving problem collections list");
                    }

                    @Override
                    public void onSuccess(final ProblemCollectionDetails[] problemCollectionDetails) {
                        GWT.log("PublicCollectionsActivity: retrieved problem collections list");
                        eventBus.fireEvent(new ListProblemCollectionsEvent(problemCollectionDetails, null));
                    }
                });

    }
}
