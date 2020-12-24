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
import com.playshogi.library.models.Square;
import com.playshogi.library.models.record.GameRecord;
import com.playshogi.library.shogi.models.Piece;
import com.playshogi.library.shogi.models.formats.kif.KifMoveConverter;
import com.playshogi.library.shogi.models.formats.sfen.SfenConverter;
import com.playshogi.library.shogi.models.formats.usf.UsfFormat;
import com.playshogi.library.shogi.models.formats.usf.UsfMoveConverter;
import com.playshogi.library.shogi.models.moves.NormalMove;
import com.playshogi.library.shogi.models.moves.ShogiMove;
import com.playshogi.website.gwt.client.SessionInformation;
import com.playshogi.website.gwt.client.controller.ProblemController;
import com.playshogi.website.gwt.client.events.gametree.GameTreeChangedEvent;
import com.playshogi.website.gwt.client.events.gametree.HighlightMoveEvent;
import com.playshogi.website.gwt.client.events.kifu.RequestPositionEvaluationEvent;
import com.playshogi.website.gwt.client.events.puzzles.ProblemNumMovesSelectedEvent;
import com.playshogi.website.gwt.client.events.puzzles.UserFinishedProblemEvent;
import com.playshogi.website.gwt.client.events.puzzles.UserSkippedProblemEvent;
import com.playshogi.website.gwt.client.place.TsumePlace;
import com.playshogi.website.gwt.client.ui.TsumeView;
import com.playshogi.website.gwt.client.util.FireAndForgetCallback;
import com.playshogi.website.gwt.shared.models.PositionEvaluationDetails;
import com.playshogi.website.gwt.shared.models.ProblemDetails;
import com.playshogi.website.gwt.shared.services.KifuService;
import com.playshogi.website.gwt.shared.services.KifuServiceAsync;
import com.playshogi.website.gwt.shared.services.ProblemsService;
import com.playshogi.website.gwt.shared.services.ProblemsServiceAsync;

public class TsumeActivity extends MyAbstractActivity {

    interface MyEventBinder extends EventBinder<TsumeActivity> {
    }

    private final MyEventBinder eventBinder = GWT.create(MyEventBinder.class);

    private final ProblemsServiceAsync problemsService = GWT.create(ProblemsService.class);
    private final KifuServiceAsync kifuService = GWT.create(KifuService.class);
    private final TsumeView tsumeView;
    private final SessionInformation sessionInformation;
    private final ProblemController problemController = new ProblemController();
    private EventBus eventBus;

    private String tsumeId;
    private int numMoves = 0;
    private Duration duration = new Duration();

    public TsumeActivity(final TsumePlace place, final TsumeView tsumeView,
                         final SessionInformation sessionInformation) {
        this.tsumeView = tsumeView;
        this.tsumeId = place.getTsumeId();
        this.sessionInformation = sessionInformation;
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
        numMoves = event.getNumMoves();
    }

    private void loadTsume(final String tsumeId) {
        if (tsumeId == null || tsumeId.equalsIgnoreCase("null")) {
            if (numMoves == 0) {
                requestRandomTsume();
            } else {
                requestRandomTsume(numMoves);
            }
        } else {
            requestTsume(tsumeId);
        }
    }

    private void requestRandomTsume() {
        problemsService.getRandomProblem(getProblemRequestCallback(null));
    }

    private void requestRandomTsume(int numMoves) {
        problemsService.getRandomProblem(numMoves, getProblemRequestCallback(null));
    }


    private void requestTsume(final String tsumeId) {
        problemsService.getProblem(tsumeId, getProblemRequestCallback(tsumeId));
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

    @EventHandler
    public void onRequestPositionEvaluationEvent(final RequestPositionEvaluationEvent event) {
        GWT.log("Tsume Activity Handling RequestPositionEvaluationEvent");
        String sfen = SfenConverter.toSFEN(tsumeView.getCurrentPosition());
        kifuService.analysePosition(sessionInformation.getSessionId(), sfen,
                new AsyncCallback<PositionEvaluationDetails>() {
                    @Override
                    public void onFailure(Throwable throwable) {
                        GWT.log("TsumeActivity - ERROR GETTING POSITION EVALUATION");
                    }

                    @Override
                    public void onSuccess(PositionEvaluationDetails result) {
                        GWT.log("TsumeActivity - received position evaluation\n" + result);
                        switch (result.getTsumeAnalysis().getResult()) {
                            case TSUME:
                                Window.alert("Gote is still in Tsume - Your solution may be longer, or the puzzle has" +
                                        " multiple solutions.");
                                break;
                            case NOT_CHECK:
                                Window.alert("Gote is not in check - To solve a Tsume problem, every move needs to be" +
                                        " a check.");
                                break;
                            case ESCAPE:
                                String escapeMove = result.getTsumeAnalysis().getEscapeMove();
                                ShogiMove move = UsfMoveConverter.fromUsfString(escapeMove,
                                        tsumeView.getCurrentPosition());
                                eventBus.fireEvent(new HighlightMoveEvent(move));
                                Scheduler.get().scheduleFixedDelay(() -> {
                                    String message;
                                    if (move instanceof NormalMove && ((NormalMove) move).getPiece() == Piece.GOTE_KING) {
                                        Square toSquare = ((NormalMove) move).getToSquare();
                                        message = "If Gote escapes to " + toSquare + ", there is no mate.";
                                    } else {
                                        message =
                                                "If Gote plays the highlighted move (" + KifMoveConverter.toKifStringShort(move) + "), there is no mate.";
                                    }
                                    Window.alert(message);
                                    return false;
                                }, 100);
                                break;
                        }

                    }
                });
    }
}