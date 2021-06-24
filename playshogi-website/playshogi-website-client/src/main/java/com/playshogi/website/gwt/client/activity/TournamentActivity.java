package com.playshogi.website.gwt.client.activity;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.binder.EventBinder;
import com.playshogi.website.gwt.client.SessionInformation;
import com.playshogi.website.gwt.client.events.collections.TournamentDetailsEvent;
import com.playshogi.website.gwt.client.events.tutorial.LessonsListEvent;
import com.playshogi.website.gwt.client.place.LessonsPlace;
import com.playshogi.website.gwt.client.place.TournamentPlace;
import com.playshogi.website.gwt.client.ui.LessonsView;
import com.playshogi.website.gwt.client.ui.TournamentView;
import com.playshogi.website.gwt.shared.models.LessonDetails;
import com.playshogi.website.gwt.shared.models.TournamentDetails;
import com.playshogi.website.gwt.shared.services.KifuService;
import com.playshogi.website.gwt.shared.services.KifuServiceAsync;

public class TournamentActivity extends MyAbstractActivity {

    interface MyEventBinder extends EventBinder<TournamentActivity> {
    }

    private final MyEventBinder eventBinder = GWT.create(MyEventBinder.class);

    private final KifuServiceAsync kifuService = GWT.create(KifuService.class);

    private final TournamentView tournamentView;
    private final SessionInformation sessionInformation;

    private EventBus eventBus;

    public TournamentActivity(final TournamentPlace place, final TournamentView tournamentView,
                              final SessionInformation sessionInformation) {
        this.tournamentView = tournamentView;
        this.sessionInformation = sessionInformation;
    }

    @Override
    public void start(final AcceptsOneWidget containerWidget, final EventBus eventBus) {
        GWT.log("Starting tournament activity");
        this.eventBus = eventBus;
        eventBinder.bindEventHandlers(this, eventBus);

        tournamentView.activate(eventBus);
        containerWidget.setWidget(tournamentView.asWidget());

        kifuService.getTournament(sessionInformation.getSessionId(), "TTSeries", new AsyncCallback<TournamentDetails>() {
            @Override
            public void onFailure(final Throwable throwable) {
                GWT.log("TournamentActivity - RPC failure: getAllTournaments " + throwable);
            }

            @Override
            public void onSuccess(final TournamentDetails tournamentDetails) {
                GWT.log("TournamentActivity - RPC success: getAllTournaments");
                eventBus.fireEvent(new TournamentDetailsEvent(tournamentDetails));
            }
        });
    }

}
