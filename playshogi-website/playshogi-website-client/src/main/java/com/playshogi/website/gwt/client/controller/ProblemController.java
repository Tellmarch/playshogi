package com.playshogi.website.gwt.client.controller;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.binder.EventBinder;
import com.google.web.bindery.event.shared.binder.EventHandler;
import com.playshogi.library.shogi.models.Piece;
import com.playshogi.library.shogi.models.formats.sfen.SfenConverter;
import com.playshogi.library.shogi.models.formats.usf.UsfMoveConverter;
import com.playshogi.library.shogi.models.moves.NormalMove;
import com.playshogi.library.shogi.models.moves.ShogiMove;
import com.playshogi.library.shogi.models.position.ShogiPosition;
import com.playshogi.library.shogi.models.position.Square;
import com.playshogi.library.shogi.rules.ShogiRulesEngine;
import com.playshogi.website.gwt.client.SessionInformation;
import com.playshogi.website.gwt.client.events.gametree.EndOfVariationReachedEvent;
import com.playshogi.website.gwt.client.events.gametree.HighlightMoveEvent;
import com.playshogi.website.gwt.client.events.gametree.NewVariationPlayedEvent;
import com.playshogi.website.gwt.client.events.kifu.RequestPositionEvaluationEvent;
import com.playshogi.website.gwt.client.events.puzzles.UserFinishedProblemEvent;
import com.playshogi.website.gwt.shared.models.PositionEvaluationDetails;
import com.playshogi.website.gwt.shared.services.KifuService;
import com.playshogi.website.gwt.shared.services.KifuServiceAsync;

import java.util.function.Supplier;

public class ProblemController {

    private final KifuServiceAsync kifuService = GWT.create(KifuService.class);

    interface MyEventBinder extends EventBinder<ProblemController> {
    }

    private final MyEventBinder eventBinder = GWT.create(MyEventBinder.class);

    private final ShogiRulesEngine shogiRulesEngine = new ShogiRulesEngine();

    private EventBus eventBus;

    private String problemId = null;

    private Supplier<ShogiPosition> positionSupplier;
    private SessionInformation sessionInformation;

    public ProblemController(Supplier<ShogiPosition> positionSupplier, SessionInformation sessionInformation) {
        this.positionSupplier = positionSupplier;
        this.sessionInformation = sessionInformation;
    }

    public void activate(final EventBus eventBus) {
        GWT.log("Activating Problem controller");
        this.eventBus = eventBus;
        eventBinder.bindEventHandlers(this, eventBus);
    }

    public void setProblemId(String problemId) {
        GWT.log("ProblemController: setting problemId to " + problemId);
        this.problemId = problemId;
    }

    private void handleNewMove(final ShogiPosition newPosition) {
        boolean positionCheckmate = shogiRulesEngine.isPositionCheckmate(newPosition);

        if (positionCheckmate) {
            GWT.log("Problem controller: player found alternative checkmate");
            eventBus.fireEvent(new UserFinishedProblemEvent(true, problemId));
        } else {
            eventBus.fireEvent(new UserFinishedProblemEvent(false, problemId));
        }
    }

    @EventHandler
    public void onNewVariation(final NewVariationPlayedEvent event) {
        GWT.log("Problem controller: handle new variation played event");
        ShogiPosition newPosition = event.getNewPosition();
        handleNewMove(newPosition);
    }

    @EventHandler
    public void onEndOfVariation(final EndOfVariationReachedEvent event) {
        GWT.log("ProblemController: handle end of variation reached event");

        if (event.isNewNode()) {
            handleNewMove(event.getPosition());
        } else {
            eventBus.fireEvent(new UserFinishedProblemEvent(true, problemId));
        }
    }
    @EventHandler
    public void onRequestPositionEvaluationEvent(final RequestPositionEvaluationEvent event) {
        GWT.log("Tsume Activity Handling RequestPositionEvaluationEvent");
        String sfen = SfenConverter.toSFEN(positionSupplier.get());
        kifuService.analysePosition(sessionInformation.getSessionId(), sfen,
                new AsyncCallback<PositionEvaluationDetails>() {
                    @Override
                    public void onFailure(Throwable throwable) {
                        GWT.log("TsumeActivity - ERROR GETTING POSITION EVALUATION");
                    }

                    @Override
                    public void onSuccess(PositionEvaluationDetails result) {
                        GWT.log("TsumeActivity - received position evaluation\n" + result);
                        processPositionEvaluationResult(result);
                    }
                });
    }
    private void processPositionEvaluationResult(final PositionEvaluationDetails result) {
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
                        positionSupplier.get());
                eventBus.fireEvent(new HighlightMoveEvent(move));
                Scheduler.get().scheduleFixedDelay(() -> {
                    String message;
                    if (move instanceof NormalMove && ((NormalMove) move).getPiece() == Piece.GOTE_KING) {
                        Square toSquare = ((NormalMove) move).getToSquare();
                        message = "If Gote escapes to " + toSquare + ", there is no mate.";
                    } else {
                        message =
                                "If Gote plays the highlighted move (" +
                                        sessionInformation.getUserPreferences().getMoveNotationAccordingToPreferences(move, true)
                                        + "), there is no mate.";
                    }
                    Window.alert(message);
                    return false;
                }, 100);
                break;
        }
    }
}
