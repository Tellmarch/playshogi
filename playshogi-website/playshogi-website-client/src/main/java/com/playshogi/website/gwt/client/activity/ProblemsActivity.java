package com.playshogi.website.gwt.client.activity;

import com.google.gwt.core.client.Duration;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.binder.EventBinder;
import com.google.web.bindery.event.shared.binder.EventHandler;
import com.playshogi.library.shogi.models.formats.usf.UsfFormat;
import com.playshogi.library.shogi.models.record.GameRecord;
import com.playshogi.website.gwt.client.SessionInformation;
import com.playshogi.website.gwt.client.controller.ProblemController;
import com.playshogi.website.gwt.client.events.collections.ListCollectionProblemsEvent;
import com.playshogi.website.gwt.client.events.gametree.GameTreeChangedEvent;
import com.playshogi.website.gwt.client.events.kifu.GameInformationChangedEvent;
import com.playshogi.website.gwt.client.events.puzzles.*;
import com.playshogi.website.gwt.client.place.ProblemsPlace;
import com.playshogi.website.gwt.client.ui.ProblemsView;
import com.playshogi.website.gwt.client.util.FireAndForgetCallback;
import com.playshogi.website.gwt.shared.models.ProblemCollectionDetails;
import com.playshogi.website.gwt.shared.models.ProblemCollectionDetailsAndProblems;
import com.playshogi.website.gwt.shared.models.ProblemDetails;
import com.playshogi.website.gwt.shared.services.KifuService;
import com.playshogi.website.gwt.shared.services.KifuServiceAsync;
import com.playshogi.website.gwt.shared.services.ProblemsService;
import com.playshogi.website.gwt.shared.services.ProblemsServiceAsync;

import java.util.Arrays;

public class ProblemsActivity extends MyAbstractActivity {

    public enum ProblemStatus {
        CURRENT,
        SOLVED,
        FAILED,
        UNSOLVED
    }

    interface MyEventBinder extends EventBinder<ProblemsActivity> {
    }

    private final MyEventBinder eventBinder = GWT.create(MyEventBinder.class);

    private final ProblemsServiceAsync problemsService = GWT.create(ProblemsService.class);
    private final KifuServiceAsync kifuService = GWT.create(KifuService.class);
    private final ProblemsView problemsView;
    private final SessionInformation sessionInformation;
    private final ProblemController problemController = new ProblemController();
    private EventBus eventBus;

    private final String collectionId;

    private int problemIndex;
    private ProblemCollectionDetails details;
    private ProblemDetails[] problems;
    private ProblemStatus[] statuses;

    private Duration duration;
    private Timer activityTimer;

    public ProblemsActivity(final ProblemsPlace place, final ProblemsView problemsView,
                            final SessionInformation sessionInformation) {
        this.problemsView = problemsView;
        this.collectionId = place.getCollectionId();
        this.problemIndex = place.getProblemIndex();
        this.sessionInformation = sessionInformation;
    }

    @Override
    public void start(final AcceptsOneWidget containerWidget, final EventBus eventBus) {
        GWT.log("Starting problems activity");
        this.eventBus = eventBus;
        eventBinder.bindEventHandlers(this, eventBus);
        problemsView.activate(eventBus);
        problemController.activate(eventBus);
        containerWidget.setWidget(problemsView.asWidget());

        loadCollection();
    }

    private void loadCollection() {
        GWT.log("Querying for collection problems");
        problemsService.getProblemCollection(sessionInformation.getSessionId(), collectionId,
                new AsyncCallback<ProblemCollectionDetailsAndProblems>() {
                    @Override
                    public void onFailure(final Throwable throwable) {
                        GWT.log("ProblemsActivity: error retrieving collection problems");
                    }

                    @Override
                    public void onSuccess(final ProblemCollectionDetailsAndProblems result) {
                        GWT.log("ProblemsActivity: retrieved collection problems");
                        problems = result.getProblems();
                        statuses = new ProblemStatus[problems.length];
                        Arrays.fill(statuses, ProblemStatus.UNSOLVED);
                        details = result.getDetails();
                        loadProblem();
                        eventBus.fireEvent(new ListCollectionProblemsEvent(result.getProblems(), result.getDetails()));
                    }
                });
    }

    private void loadProblem() {
        if (problemIndex >= problems.length) {
            return;
        }
        String id = problems[problemIndex].getKifuId();

        kifuService.getKifuUsf(sessionInformation.getSessionId(), id,
                new AsyncCallback<String>() {
                    @Override
                    public void onFailure(final Throwable throwable) {
                        GWT.log("Remote called failed for problem request: " + id);
                    }

                    @Override
                    public void onSuccess(final String usf) {
                        GWT.log("Received problem USF: " + usf);
                        GameRecord gameRecord = UsfFormat.INSTANCE.readSingle(usf);
                        eventBus.fireEvent(new GameTreeChangedEvent(gameRecord.getGameTree()));
                        eventBus.fireEvent(new GameInformationChangedEvent(gameRecord.getGameInformation()));
                        if (collectionId != null) {
                            History.newItem("Problems:" + new ProblemsPlace.Tokenizer().getToken(getPlace()), false);
                            if (problemIndex < statuses.length) {
                                statuses[problemIndex] = ProblemStatus.CURRENT;
                                eventBus.fireEvent(new ProblemCollectionProgressEvent(problemIndex, statuses));
                            }
                        }
                    }
                });
    }

    private ProblemsPlace getPlace() {
        return new ProblemsPlace(collectionId, problemIndex);
    }

    private void initTimer() {
        duration = new Duration();

        activityTimer = new Timer() {
            @Override
            public void run() {
                eventBus.fireEvent(new ActivityTimerEvent(duration.elapsedMillis(), false));
            }
        };

        activityTimer.scheduleRepeating(100);
    }

    private void stopTimer() {
        activityTimer.cancel();
    }

    private boolean isTimerRunning() {
        return !(activityTimer == null) && activityTimer.isRunning();
    }

    private void loadNextProblem() {
        if (isTimerRunning()) {
            problemIndex++;
            if (problemIndex == statuses.length) problemIndex = 0;
            boolean firstPass = true;
            while (statuses[problemIndex] == ProblemStatus.SOLVED) {
                problemIndex++;
                if (problemIndex == statuses.length) {
                    if (firstPass) {
                        firstPass = false;
                        problemIndex = 0;
                    } else {
                        break;
                    }
                }
            }
        } else {
            problemIndex++;
        }

        if (problemIndex >= problems.length) {
            if (isTimerRunning()) {
                stopTimer();
                int time = duration.elapsedMillis();
                eventBus.fireEvent(new ActivityTimerEvent(time, false));
                Window.alert("Congratulations, you have solved all the problems!");
                if (sessionInformation.isLoggedIn()) {
                    saveTime(time);
                }
                return;
            } else {
                Window.alert("You reached the last problem in the collection!");
                return;
            }
        }

        loadProblem();
    }

    private void saveTime(final int time) {
        problemsService.saveCollectionTime(sessionInformation.getSessionId(), collectionId,
                time, true, statuses.length, new FireAndForgetCallback());
    }

    @Override
    public void onStop() {
        GWT.log("Stopping tsume activity");
        super.onStop();
        stopTimers();
    }

    private void stopTimers() {
        if (activityTimer != null) {
            activityTimer.cancel();
            activityTimer = null;
        }
    }

    @EventHandler
    void onUserSkippedProblem(final UserSkippedProblemEvent event) {
        if (problemIndex < statuses.length && statuses[problemIndex] == ProblemStatus.CURRENT) {
            statuses[problemIndex] = ProblemStatus.UNSOLVED;
        }
        loadNextProblem();
    }

    @EventHandler
    void onUserFinishedProblem(final UserFinishedProblemEvent event) {
        GWT.log("Finished problem. Success: " + event.isSuccess());
        if (problemIndex < statuses.length) {
            statuses[problemIndex] = event.isSuccess() ? ProblemStatus.SOLVED : ProblemStatus.FAILED;
            eventBus.fireEvent(new ProblemCollectionProgressEvent(problemIndex, statuses));
        }
        if (isTimerRunning()) {
            loadNextProblem();
        }
    }

    @EventHandler
    void onUserJumpedToProblem(final UserJumpedToProblemEvent event) {
        if (problemIndex < statuses.length && statuses[problemIndex] == ProblemStatus.CURRENT) {
            statuses[problemIndex] = ProblemStatus.UNSOLVED;
        }
        problemIndex = event.getProblemIndex();
        loadProblem();
    }

    @EventHandler
    void onStartTimedRun(final StartTimedRunEvent event) {
        GWT.log("Start timed run");
        Arrays.fill(statuses, ProblemStatus.UNSOLVED);
        problemIndex = 0;
        eventBus.fireEvent(new ProblemCollectionProgressEvent(problemIndex, statuses));
        loadProblem();
        initTimer();
    }

    @EventHandler
    void onStopTimedRun(final StopTimedRunEvent event) {
        GWT.log("Stop timed run");
        stopTimer();
    }

}
