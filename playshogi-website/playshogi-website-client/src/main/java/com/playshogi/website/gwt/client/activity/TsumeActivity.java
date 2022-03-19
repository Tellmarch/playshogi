package com.playshogi.website.gwt.client.activity;

import com.google.gwt.core.client.Duration;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.binder.EventBinder;
import com.google.web.bindery.event.shared.binder.EventHandler;
import com.playshogi.library.shogi.models.Piece;
import com.playshogi.library.shogi.models.formats.sfen.SfenConverter;
import com.playshogi.library.shogi.models.formats.usf.UsfFormat;
import com.playshogi.library.shogi.models.formats.usf.UsfMoveConverter;
import com.playshogi.library.shogi.models.moves.NormalMove;
import com.playshogi.library.shogi.models.moves.ShogiMove;
import com.playshogi.library.shogi.models.position.Square;
import com.playshogi.library.shogi.models.record.GameRecord;
import com.playshogi.website.gwt.client.SessionInformation;
import com.playshogi.website.gwt.client.controller.ProblemController;
import com.playshogi.website.gwt.client.events.gametree.GameTreeChangedEvent;
import com.playshogi.website.gwt.client.events.gametree.HighlightMoveEvent;
import com.playshogi.website.gwt.client.events.kifu.RequestPositionEvaluationEvent;
import com.playshogi.website.gwt.client.events.puzzles.*;
import com.playshogi.website.gwt.client.place.TsumePlace;
import com.playshogi.website.gwt.client.ui.TsumeView;
import com.playshogi.website.gwt.client.util.FireAndForgetCallback;
import com.playshogi.website.gwt.shared.models.PositionEvaluationDetails;
import com.playshogi.website.gwt.shared.models.ProblemDetails;
import com.playshogi.website.gwt.shared.models.ProblemOptions;
import com.playshogi.website.gwt.shared.services.KifuService;
import com.playshogi.website.gwt.shared.services.KifuServiceAsync;
import com.playshogi.website.gwt.shared.services.ProblemsService;
import com.playshogi.website.gwt.shared.services.ProblemsServiceAsync;

public class TsumeActivity extends MyAbstractActivity {

    interface MyEventBinder extends EventBinder<TsumeActivity> {
    }

    private final MyEventBinder eventBinder = GWT.create(MyEventBinder.class);

    private final ProblemsServiceAsync problemsService = GWT.create(ProblemsService.class);
    private final TsumeView tsumeView;
    private final SessionInformation sessionInformation;
    private final ProblemController problemController;
    private EventBus eventBus;

    private String tsumeId;
    private final ProblemOptions options = new ProblemOptions();

    private Duration duration = new Duration();

    public TsumeActivity(final TsumePlace place, final TsumeView tsumeView,
                         final SessionInformation sessionInformation) {
        this.tsumeView = tsumeView;
        this.tsumeId = place.getTsumeId();
        this.sessionInformation = sessionInformation;
        this.problemController = new ProblemController(tsumeView::getCurrentPosition, sessionInformation);
    }

    private void setTsumeId(String tsumeId) {
        this.tsumeId = tsumeId;
    }

    @Override
    public void start(final AcceptsOneWidget containerWidget, final EventBus eventBus) {
        GWT.log("Starting tsume activity");
        this.eventBus = eventBus;
        eventBinder.bindEventHandlers(this, eventBus);
        tsumeView.activate(eventBus);
        problemController.activate(eventBus);
        loadTsume(tsumeId);
        containerWidget.setWidget(tsumeView.asWidget());
    }

    @Override
    public void onStop() {
        GWT.log("Stopping tsume activity");
        super.onStop();
    }

    @EventHandler
    void onUserSkippedProblem(final UserSkippedProblemEvent event) {
        options.setPreviousProblemId(tsumeId);
        loadTsume(null);
    }

    @EventHandler
    void onUserFinishedProblemEvent(final UserFinishedProblemEvent event) {
        GWT.log("Finished problem. Success: " + event.isSuccess());
        problemsService.saveUserProblemAttempt(sessionInformation.getSessionId(), tsumeId, event.isSuccess(),
                duration.elapsedMillis(),
                new FireAndForgetCallback(
                        "saveUserProblemAttempt"));
    }

    @EventHandler
    void onProblemNumMovesSelectedEvent(final ProblemNumMovesSelectedEvent event) {
        GWT.log("Setting number of moves: " + event.getNumMoves());
        options.setNumMoves(event.getNumMoves());
    }

    @EventHandler
    void onProblemsOrderSelectedEvent(final ProblemsOrderSelectedEvent event) {
        GWT.log("Setting random order: " + event.getRandom());
        options.setRandom(event.getRandom());
    }

    @EventHandler
    void onProblemTypesSelectedEvent(final ProblemTypesSelectedEvent event) {
        GWT.log("Setting problem types: " + event);
        options.setIncludeTsume(event.isIncludeTsume());
        options.setIncludeTwoKings(event.isIncludeTwoKings());
        options.setIncludeHisshi(event.isIncludeHisshi());
        options.setIncludeRealGame(event.isIncludeRealGame());
    }

    private void loadTsume(final String tsumeId) {
        if (tsumeId == null || tsumeId.equalsIgnoreCase("null")) {
            problemsService.getProblem(options, getProblemRequestCallback(null));
        } else {
            problemsService.getProblem(tsumeId, getProblemRequestCallback(tsumeId));
        }
    }

    private void initTimer() {
        duration = new Duration();
    }

    private AsyncCallback<ProblemDetails> getProblemRequestCallback(final String tsumeId) {
        return new AsyncCallback<ProblemDetails>() {

            @Override
            public void onSuccess(final ProblemDetails result) {
                if (result == null) {
                    GWT.log("Got null usf from server for problem request: " + tsumeId);
                } else {
                    GWT.log("Got problem details problem request: " + tsumeId + " : " + result);
                    String resultUsf = result.getUsf();
                    GameRecord gameRecord = UsfFormat.INSTANCE.readSingle(resultUsf);
                    GWT.log("Updating game navigator...");
                    setTsumeId(result.getId());
                    History.newItem("Tsume:" + new TsumePlace.Tokenizer().getToken(getPlace()), false);
                    eventBus.fireEvent(new GameTreeChangedEvent(gameRecord.getGameTree()));
                    initTimer();
                }
            }

            @Override
            public void onFailure(final Throwable caught) {
                GWT.log("Remote called failed for problem request: " + tsumeId);
            }
        };
    }

    private TsumePlace getPlace() {
        return new TsumePlace(tsumeId);
    }

}