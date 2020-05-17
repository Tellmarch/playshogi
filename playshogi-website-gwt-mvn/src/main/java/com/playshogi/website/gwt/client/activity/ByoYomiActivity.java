package com.playshogi.website.gwt.client.activity;

import com.google.gwt.core.client.Duration;
import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.binder.EventBinder;
import com.google.web.bindery.event.shared.binder.EventHandler;
import com.playshogi.library.models.record.GameRecord;
import com.playshogi.library.shogi.models.formats.usf.UsfFormat;
import com.playshogi.website.gwt.client.SessionInformation;
import com.playshogi.website.gwt.client.events.ByoYomiSurvivalFinishedEvent;
import com.playshogi.website.gwt.client.events.GameTreeChangedEvent;
import com.playshogi.website.gwt.client.events.UserFinishedProblemEvent;
import com.playshogi.website.gwt.client.place.ByoYomiPlace;
import com.playshogi.website.gwt.client.ui.ByoYomiView;
import com.playshogi.website.gwt.client.util.FireAndForgetCallback;
import com.playshogi.website.gwt.shared.models.ProblemDetails;
import com.playshogi.website.gwt.shared.services.ProblemsService;
import com.playshogi.website.gwt.shared.services.ProblemsServiceAsync;

public class ByoYomiActivity extends MyAbstractActivity {

    interface MyEventBinder extends EventBinder<ByoYomiActivity> {
    }

    private final MyEventBinder eventBinder = GWT.create(MyEventBinder.class);

    private final ProblemsServiceAsync problemsService = GWT.create(ProblemsService.class);
    private final PlaceController placeController;
    private final ByoYomiView byoYomiView;
    private final SessionInformation sessionInformation;
    private EventBus eventBus;

    private String tsumeId;
    private int numMoves = 3;
    private int solved = 0;
    private int failed = 0;
    private Duration problemDuration = new Duration();
    private Duration byoYomiDuration = new Duration();

    public ByoYomiActivity(final ByoYomiPlace place, final ByoYomiView byoYomiView,
                           final PlaceController placeController,
                           final SessionInformation sessionInformation) {
        this.byoYomiView = byoYomiView;
        this.tsumeId = null;
        this.placeController = placeController;
        this.sessionInformation = sessionInformation;
    }

    private void setTsumeId(String tsumeId) {
        this.tsumeId = tsumeId;
    }

    @Override
    public void start(final AcceptsOneWidget containerWidget, final EventBus eventBus) {
        GWT.log("Starting byo yomi activity");
        this.eventBus = eventBus;
        eventBinder.bindEventHandlers(this, eventBus);
        byoYomiView.activate(eventBus);
        loadNextProblem();
        containerWidget.setWidget(byoYomiView.asWidget());
    }

    @Override
    public void onStop() {
        GWT.log("Stopping byo yomi activity");
        super.onStop();
    }

    @EventHandler
    void onUserFinishedProblemEvent(final UserFinishedProblemEvent event) {
        GWT.log("Finished problem. Success: " + event.isSuccess());
        problemsService.saveUserProblemAttempt(sessionInformation.getSessionId(), tsumeId, event.isSuccess(),
                problemDuration.elapsedMillis(), new FireAndForgetCallback("saveUserProblemAttempt"));
        if (event.isSuccess()) {
            solved++;
            if (solved % 5 == 0 && numMoves < 13) {
                numMoves += 2;
            }
        } else {
            failed++;
        }
        if (failed < 3) {
            loadNextProblem();
        } else {
            eventBus.fireEvent(new ByoYomiSurvivalFinishedEvent(solved, solved, failed,
                    byoYomiDuration.elapsedMillis() / 1000));
        }
    }

    private void loadNextProblem() {
        problemsService.getRandomProblem(numMoves, getProblemRequestCallback());
    }

    private void initTimer() {
        problemDuration = new Duration();
    }

    private AsyncCallback<ProblemDetails> getProblemRequestCallback() {
        return new AsyncCallback<ProblemDetails>() {

            @Override
            public void onSuccess(final ProblemDetails result) {
                if (result == null) {
                    GWT.log("Got null usf from server for random problem request");
                } else {
                    GWT.log("Got problem details for random problem request: " + result);
                    String resultUsf = result.getUsf();
                    GameRecord gameRecord = UsfFormat.INSTANCE.read(resultUsf);
                    GWT.log("Updating game navigator...");
                    setTsumeId(result.getId());
                    eventBus.fireEvent(new GameTreeChangedEvent(gameRecord.getGameTree()));
                    initTimer();
                }
            }

            @Override
            public void onFailure(final Throwable caught) {
                GWT.log("Remote called failed for problem request");
            }
        };
    }
}