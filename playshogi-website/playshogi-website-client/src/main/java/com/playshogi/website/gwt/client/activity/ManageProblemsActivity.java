package com.playshogi.website.gwt.client.activity;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.binder.EventBinder;
import com.google.web.bindery.event.shared.binder.EventHandler;
import com.playshogi.website.gwt.client.SessionInformation;
import com.playshogi.website.gwt.client.events.user.UserLoggedInEvent;
import com.playshogi.website.gwt.client.place.ManageProblemsPlace;
import com.playshogi.website.gwt.client.ui.ManageProblemsView;
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
    }

    private void refresh() {
        fetchData();
    }

    @EventHandler
    public void onUserLoggedIn(final UserLoggedInEvent event) {
        refresh();
    }

}
