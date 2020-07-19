package com.playshogi.website.gwt.client.activity;

import com.google.gwt.core.client.Duration;
import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.binder.EventBinder;
import com.google.web.bindery.event.shared.binder.EventHandler;
import com.playshogi.library.models.record.GameRecord;
import com.playshogi.library.shogi.models.formats.usf.UsfFormat;
import com.playshogi.website.gwt.client.SessionInformation;
import com.playshogi.website.gwt.client.controller.ProblemController;
import com.playshogi.website.gwt.client.events.*;
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
    private final ProblemController problemController = new ProblemController();
    private EventBus eventBus;

    private String tsumeId;
    private int numMoves = 3;
    private int solved = 0;
    private int failed = 0;
    private Duration problemDuration = new Duration();
    private Duration byoYomiDuration = new Duration();
    private Duration moveDuration = new Duration();

    private Timer moveTimer = null;
    private Timer activityTimer = null;
    private ByoYomiPlace place;

    private boolean stopped = false;

    public ByoYomiActivity(final ByoYomiPlace place, final ByoYomiView byoYomiView,
                           final PlaceController placeController,
                           final SessionInformation sessionInformation) {
        this.place = place;
        this.byoYomiView = byoYomiView;
        this.tsumeId = null;
        this.placeController = placeController;
        this.sessionInformation = sessionInformation;
    }

    private void setTsumeId(String tsumeId) {
        this.tsumeId = tsumeId;
        problemController.setProblemId(tsumeId);
    }

    @Override
    public void start(final AcceptsOneWidget containerWidget, final EventBus eventBus) {
        GWT.log("Starting byo yomi activity");
        stopped = false;
        this.eventBus = eventBus;
        eventBinder.bindEventHandlers(this, eventBus);
        byoYomiView.activate(eventBus);
        byoYomiView.initUi(place);
        problemController.activate(eventBus);
        loadNextProblem();
        containerWidget.setWidget(byoYomiView.asWidget());

        if (place.getTimePerMove() != 0) {
            moveTimer = new Timer() {
                @Override
                public void run() {
                    updateMoveTime();
                }
            };

            moveTimer.scheduleRepeating(1000);
        }

        if (place.getMaxTimeSec() != 0) {
            activityTimer = new Timer() {
                @Override
                public void run() {
                    updateActivityTime();
                }
            };

            activityTimer.scheduleRepeating(1000);
        }
    }

    private void updateActivityTime() {
        if (place.getMaxTimeSec() > 0) {
            int timeLeftMs = place.getMaxTimeSec() * 1000 - byoYomiDuration.elapsedMillis();
            if (timeLeftMs > 0) {
                eventBus.fireEvent(new ActivityTimerEvent(timeLeftMs, true));
            } else {
                eventBus.fireEvent(new ActivityTimerEvent(0, true));
                stop();
            }
        } else {
            eventBus.fireEvent(new ActivityTimerEvent(byoYomiDuration.elapsedMillis(), false));
        }
    }

    private void updateMoveTime() {
        if (place.getTimePerMove() > 0) {
            int timeLeftMs = place.getTimePerMove() * 1000 - moveDuration.elapsedMillis();
            if (timeLeftMs > 0) {
                eventBus.fireEvent(new MoveTimerEvent(timeLeftMs, true));
            } else {
                eventBus.fireEvent(new MoveTimerEvent(0, true));
                eventBus.fireEvent(new UserFinishedProblemEvent(false, tsumeId));
            }
        } else {
            eventBus.fireEvent(new MoveTimerEvent(moveDuration.elapsedMillis(), false));
        }
    }

    private void stop() {
        int timeSec = byoYomiDuration.elapsedMillis() / 1000;
        if (place.getMaxTimeSec() > 0 && timeSec > place.getMaxTimeSec()) {
            timeSec = place.getMaxTimeSec();
        }
        if (place.isDefault()) {
            String username = sessionInformation.getUsername();
            if (username == null) {
                username = Window.prompt("What is your name?", "Guest");
            }
            problemsService.saveHighScore(username, solved, new FireAndForgetCallback());
        }
        eventBus.fireEvent(new ByoYomiSurvivalFinishedEvent(solved, solved, failed, timeSec));
        stopTimers();
        stopped = true;
    }

    @Override
    public void onStop() {
        GWT.log("Stopping byo yomi activity");
        super.onStop();
        stopTimers();
    }

    private void stopTimers() {
        if (activityTimer != null) {
            activityTimer.cancel();
            activityTimer = null;
        }
        if (moveTimer != null) {
            moveTimer.cancel();
            moveTimer = null;
        }
    }

    @EventHandler
    void onUserFinishedProblemEvent(final UserFinishedProblemEvent event) {
        GWT.log("Finished problem. Success: " + event.isSuccess());
        problemsService.saveUserProblemAttempt(sessionInformation.getSessionId(), tsumeId, event.isSuccess(),
                problemDuration.elapsedMillis(), new FireAndForgetCallback("saveUserProblemAttempt"));
        if (stopped) {
            return;
        }
        if (event.isSuccess()) {
            solved++;
            if (solved % 5 == 0 && numMoves < 13) {
                numMoves += 2;
            }
        } else {
            failed++;
        }
        if (failed < place.getMaxFailures()) {
            loadNextProblem();
        } else {
            stop();
        }
    }

    private void loadNextProblem() {
        problemsService.getRandomProblem(numMoves, getProblemRequestCallback());
    }

    private void initProblemTimer() {
        problemDuration = new Duration();
    }

    private void initMoveTimer() {
        moveDuration = new Duration();
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
                    initProblemTimer();
                    initMoveTimer();
                }
            }

            @Override
            public void onFailure(final Throwable caught) {
                GWT.log("Remote called failed for problem request");
            }
        };
    }
}