package com.playshogi.website.gwt.client.activity;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.binder.EventBinder;
import com.google.web.bindery.event.shared.binder.EventHandler;
import com.playshogi.website.gwt.client.SessionInformation;
import com.playshogi.website.gwt.client.events.collections.ListProblemCollectionsEvent;
import com.playshogi.website.gwt.client.events.user.UserLoggedInEvent;
import com.playshogi.website.gwt.client.place.ProblemCollectionsPlace;
import com.playshogi.website.gwt.client.ui.ProblemCollectionsView;
import com.playshogi.website.gwt.shared.models.ProblemCollectionDetails;
import com.playshogi.website.gwt.shared.services.ProblemsService;
import com.playshogi.website.gwt.shared.services.ProblemsServiceAsync;

public class ProblemCollectionsActivity extends MyAbstractActivity {

    private final ProblemsServiceAsync problemService = GWT.create(ProblemsService.class);
    private EventBus eventBus;

    interface MyEventBinder extends EventBinder<ProblemCollectionsActivity> {
    }

    private final MyEventBinder eventBinder = GWT.create(MyEventBinder.class);

    private final ProblemCollectionsPlace place;
    private final ProblemCollectionsView view;
    private final SessionInformation sessionInformation;

    public ProblemCollectionsActivity(final ProblemCollectionsPlace place, final ProblemCollectionsView view,
                                      final SessionInformation sessionInformation) {
        this.place = place;
        this.view = view;
        this.sessionInformation = sessionInformation;
    }

    @Override
    public void start(final AcceptsOneWidget containerWidget, final EventBus eventBus) {
        GWT.log("Starting problem collections activity");
        this.eventBus = eventBus;
        eventBinder.bindEventHandlers(this, eventBus);
        view.activate(eventBus);

        fetchData();

        containerWidget.setWidget(view.asWidget());
    }

    private void fetchData() {
        problemService.getPublicProblemCollections(sessionInformation.getSessionId(),
                new AsyncCallback<ProblemCollectionDetails[]>() {
                    @Override
                    public void onFailure(Throwable throwable) {
                        GWT.log("ProblemCollectionsActivity: error retrieving collections list");
                    }

                    @Override
                    public void onSuccess(ProblemCollectionDetails[] details) {
                        GWT.log("ProblemCollectionsActivity: retrieved collections list");
                        eventBus.fireEvent(new ListProblemCollectionsEvent(details));
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
