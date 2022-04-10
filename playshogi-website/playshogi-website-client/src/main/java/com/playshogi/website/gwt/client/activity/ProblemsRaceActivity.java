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
import com.playshogi.website.gwt.client.events.races.JoinRaceEvent;
import com.playshogi.website.gwt.client.events.races.RaceEvent;
import com.playshogi.website.gwt.client.events.races.StartRaceEvent;
import com.playshogi.website.gwt.client.events.races.WithdrawFromRaceEvent;
import com.playshogi.website.gwt.client.events.user.UserLoggedInEvent;
import com.playshogi.website.gwt.client.models.ProblemStatus;
import com.playshogi.website.gwt.client.place.ProblemsRacePlace;
import com.playshogi.website.gwt.client.ui.ProblemsRaceView;
import com.playshogi.website.gwt.client.util.FireAndForgetCallback;
import com.playshogi.website.gwt.shared.models.ProblemCollectionDetails;
import com.playshogi.website.gwt.shared.models.ProblemCollectionDetailsAndProblems;
import com.playshogi.website.gwt.shared.models.ProblemDetails;
import com.playshogi.website.gwt.shared.models.RaceDetails;
import com.playshogi.website.gwt.shared.services.*;

import java.util.Arrays;

public class ProblemsRaceActivity extends MyAbstractActivity {

    interface MyEventBinder extends EventBinder<ProblemsRaceActivity> {
    }

    private final MyEventBinder eventBinder = GWT.create(MyEventBinder.class);

    private final ProblemsServiceAsync problemsService = GWT.create(ProblemsService.class);
    private final KifuServiceAsync kifuService = GWT.create(KifuService.class);
    private final UserServiceAsync userService = GWT.create(UserService.class);

    private final ProblemsRaceView view;
    private final SessionInformation sessionInformation;
    private final ProblemController problemController;
    private EventBus eventBus;

    private final String collectionId;
    private String raceId;

    private int problemIndex;
    private ProblemCollectionDetails details;
    private ProblemDetails[] problems;
    private ProblemStatus[] statuses;

    private Duration duration = new Duration();
    private int offSetMs = 0;

    private Timer activityTimer;
    private Timer updatesTimer;
    private volatile boolean isStopped = false;

    public ProblemsRaceActivity(final ProblemsRacePlace place, final ProblemsRaceView view,
                                final SessionInformation sessionInformation) {
        this.view = view;
        this.collectionId = place.getCollectionId();
        this.raceId = place.getRaceId();
        this.problemIndex = place.getProblemIndex();
        this.sessionInformation = sessionInformation;
        this.problemController = new ProblemController(view::getCurrentPosition, sessionInformation);
    }

    @Override
    public void start(final AcceptsOneWidget containerWidget, final EventBus eventBus) {
        GWT.log("Starting problems race activity");
        this.eventBus = eventBus;
        eventBinder.bindEventHandlers(this, eventBus);
        view.activate(eventBus);
        problemController.activate(eventBus);
        containerWidget.setWidget(view.asWidget());

        refresh();
    }

    private void refresh() {
        if (!sessionInformation.isLoggedIn()) {
            return;
        }
        if (raceId == null) {
            createRace();
        } else {
            getRaceDetails();
            listenToRaceUpdates();
        }
        loadCollection();
    }

    private void createRace() {
        GWT.log("Creating problems race");
        problemsService.createRace(sessionInformation.getSessionId(), collectionId, RaceDetails.RaceType.TO_THE_END,
                new AsyncCallback<String>() {
                    @Override
                    public void onFailure(final Throwable throwable) {
                        GWT.log("ProblemsRaceActivity: error creating the race");
                    }

                    @Override
                    public void onSuccess(final String raceId) {
                        GWT.log("ProblemsRaceActivity: successfully created race " + raceId);
                        ProblemsRaceActivity.this.raceId = raceId;
                        History.replaceItem("ProblemsRace:" + new ProblemsRacePlace.Tokenizer().getToken(getPlace()),
                                false);
                        getRaceDetails();
                        listenToRaceUpdates();
                    }
                });
    }

    private void getRaceDetails() {
        problemsService.getRaceDetails(sessionInformation.getSessionId(), raceId,
                new AsyncCallback<RaceDetails>() {
                    @Override
                    public void onFailure(final Throwable throwable) {
                        GWT.log("ProblemsRaceActivity: error getting race details");
                    }

                    @Override
                    public void onSuccess(final RaceDetails raceDetails) {
                        GWT.log("ProblemsRaceActivity: received race details " + raceDetails);
                        eventBus.fireEvent(new RaceEvent(raceDetails));
                    }
                });
    }


    private void listenToRaceUpdates() {
        problemsService.waitForRaceUpdate(sessionInformation.getSessionId(), raceId,
                new AsyncCallback<RaceDetails>() {
                    @Override
                    public void onFailure(final Throwable throwable) {
                        GWT.log("ProblemsRaceActivity: error getting race update");
                    }

                    @Override
                    public void onSuccess(final RaceDetails raceDetails) {
                        GWT.log("ProblemsRaceActivity: received race update " + raceDetails);
                        if (!raceDetails.getId().equals(raceId) || isStopped) {
                            return;
                        }
                        eventBus.fireEvent(new RaceEvent(raceDetails));
                        listenToRaceUpdates();
                    }
                });
    }

    private void loadCollection() {
        GWT.log("Querying for collection problems");
        problemsService.getProblemCollection(sessionInformation.getSessionId(), collectionId, false,
                new AsyncCallback<ProblemCollectionDetailsAndProblems>() {
                    @Override
                    public void onFailure(final Throwable throwable) {
                        GWT.log("ProblemsRaceActivity: error retrieving collection problems");
                    }

                    @Override
                    public void onSuccess(final ProblemCollectionDetailsAndProblems result) {
                        GWT.log("ProblemsRaceActivity: retrieved collection problems");
                        problems = result.getProblems();
                        statuses = new ProblemStatus[problems.length];
                        Arrays.fill(statuses, ProblemStatus.UNSOLVED);
                        details = result.getDetails();
                        loadProblem();
                        eventBus.fireEvent(new ListCollectionProblemsEvent(result.getProblems(), result.getDetails()));
                    }
                });
    }

    private void startRace() {
        GWT.log("Starting race");
        problemsService.startRace(sessionInformation.getSessionId(), raceId, new AsyncCallback<Void>() {
            @Override
            public void onFailure(final Throwable throwable) {
                GWT.log("ProblemsRaceActivity: error starting the race");
            }

            @Override
            public void onSuccess(final Void unused) {
                GWT.log("ProblemsRaceActivity: successfully started the race");
            }
        });
    }

    private void joinRace() {
        GWT.log("Joining race");
        problemsService.joinRace(sessionInformation.getSessionId(), raceId, new AsyncCallback<Void>() {
            @Override
            public void onFailure(final Throwable throwable) {
                GWT.log("ProblemsRaceActivity: error joining the race");
            }

            @Override
            public void onSuccess(final Void unused) {
                GWT.log("ProblemsRaceActivity: successfully joined the race");
            }
        });
    }

    private void withdrawFromRace() {
        GWT.log("Withdrawing from race");
        problemsService.withdrawFromRace(sessionInformation.getSessionId(), raceId, new AsyncCallback<Void>() {
            @Override
            public void onFailure(final Throwable throwable) {
                GWT.log("ProblemsRaceActivity: error withdrawing from the race");
            }

            @Override
            public void onSuccess(final Void unused) {
                GWT.log("ProblemsRaceActivity: successfully withdrew from the race");
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
                            History.newItem("ProblemsRace:" + new ProblemsRacePlace.Tokenizer().getToken(getPlace()),
                                    false);
                            if (problemIndex < statuses.length) {
                                statuses[problemIndex] = ProblemStatus.CURRENT;
                                eventBus.fireEvent(new ProblemCollectionProgressEvent(problemIndex, statuses));
                            }
                        }
                    }
                });

        problemsService.reportUserProgressInRace(sessionInformation.getSessionId(), raceId,
                problems[problemIndex].getId(), RaceDetails.ProblemStatus.ATTEMPTING, new FireAndForgetCallback());
    }

    private ProblemsRacePlace getPlace() {
        return new ProblemsRacePlace(collectionId, problemIndex, raceId);
    }

    private void initTimer() {
        activityTimer = new Timer() {
            @Override
            public void run() {
                eventBus.fireEvent(new ActivityTimerEvent(duration.elapsedMillis() + offSetMs, false));
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

        if (problemIndex >= problems.length) {
            stopTimer();
            int time = duration.elapsedMillis();
            eventBus.fireEvent(new ActivityTimerEvent(time, false));
            Window.alert("Congratulations, you have solved all the problems!");
            if (sessionInformation.isLoggedIn()) {
                saveTime(time);
            }

            return;
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
        isStopped = true;
    }

    private void stopTimers() {
        if (activityTimer != null) {
            activityTimer.cancel();
            activityTimer = null;
        }
        if (updatesTimer != null) {
            updatesTimer.cancel();
            updatesTimer = null;
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
        loadNextProblem();
        if (event.isSuccess()) {
            problemsService.reportUserProgressInRace(sessionInformation.getSessionId(), raceId,
                    problems[problemIndex].getId(), RaceDetails.ProblemStatus.SOLVED, new FireAndForgetCallback());
        } else {
            problemsService.reportUserProgressInRace(sessionInformation.getSessionId(), raceId,
                    problems[problemIndex].getId(), RaceDetails.ProblemStatus.FAILED, new FireAndForgetCallback());
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
    public void onUserLoggedIn(final UserLoggedInEvent event) {
        refresh();
    }

    @EventHandler
    public void onJoinRaceEvent(final JoinRaceEvent event) {
        joinRace();
    }

    @EventHandler
    public void onWithdrawFromRaceEvent(final WithdrawFromRaceEvent event) {
        withdrawFromRace();
    }

    @EventHandler
    public void onStartRaceEvent(final StartRaceEvent event) {
        startRace();
    }

    @EventHandler
    public void onRaceEvent(final RaceEvent event) {
        com.google.gwt.core.shared.GWT.log("ProblemsRaceView: handle RaceEvent");
        RaceDetails raceDetails = event.getRaceDetails();
        if (raceDetails.getRaceStatus() == RaceDetails.RaceStatus.IN_PROGRESS) {
            offSetMs = raceDetails.getElapsedTimeMs();
            duration = new Duration();
            if (!isTimerRunning()) {
                initTimer();
            }
        }
    }
}
