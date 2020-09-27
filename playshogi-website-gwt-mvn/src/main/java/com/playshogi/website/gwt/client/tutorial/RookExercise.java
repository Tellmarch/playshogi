package com.playshogi.website.gwt.client.tutorial;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Timer;
import com.google.web.bindery.event.shared.EventBus;
import com.playshogi.library.models.Move;
import com.playshogi.library.models.Square;
import com.playshogi.library.shogi.models.Piece;
import com.playshogi.library.shogi.models.moves.NormalMove;
import com.playshogi.library.shogi.models.moves.ShogiMove;
import com.playshogi.library.shogi.models.position.ShogiPosition;
import com.playshogi.library.shogi.rules.ShogiRulesEngine;
import com.playshogi.website.gwt.client.events.gametree.MovePlayedEvent;
import com.playshogi.website.gwt.client.events.tutorial.ChangeTutorialTextEvent;
import com.playshogi.website.gwt.client.events.tutorial.ChangeTutorialTitleEvent;
import com.playshogi.website.gwt.client.widget.board.ShogiBoard;

import java.util.Optional;

public class RookExercise implements Tutorial {

    private final ShogiRulesEngine rulesEngine = new ShogiRulesEngine();
    private final ShogiBoard shogiBoard;
    private final TutorialMessages tutorialMessages;
    private EventBus eventBus;
    private ShogiPosition position;

    RookExercise(ShogiBoard shogiBoard, TutorialMessages tutorialMessages) {
        this.shogiBoard = shogiBoard;
        this.tutorialMessages = tutorialMessages;
    }

    @Override
    public void activate(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    @Override
    public void setup() {
        position = new ShogiPosition();
        position.getShogiBoardState().setPieceAt(5, 9, Piece.SENTE_ROOK);
        for (int i = 1; i <= 9; i++) {
            position.getShogiBoardState().setPieceAt(i, 3, Piece.GOTE_PAWN);
        }
        shogiBoard.setPosition(position);
        shogiBoard.getBoardConfiguration().setAllowIllegalMoves(false);
        shogiBoard.getBoardConfiguration().setPlaySenteMoves(true);
        shogiBoard.getBoardConfiguration().setPlayGoteMoves(false);
        shogiBoard.getBoardConfiguration().setAllowPromotion(false);

        eventBus.fireEvent(new ChangeTutorialTextEvent(tutorialMessages.rookPractice()));
        eventBus.fireEvent(new ChangeTutorialTitleEvent(tutorialMessages.rookTitle()));
    }

    @Override
    public void onMovePlayed(final MovePlayedEvent movePlayedEvent) {
        ShogiMove senteMove = movePlayedEvent.getMove();
        rulesEngine.playMoveInPosition(position, senteMove);
        shogiBoard.displayPosition();

        Optional<Square> aboveToSquare = ((NormalMove) senteMove).getToSquare().above();
        if (aboveToSquare.isPresent() && position.hasWhitePieceAt(aboveToSquare.get())) {
            eventBus.fireEvent(new ChangeTutorialTextEvent(tutorialMessages.rookPracticeFailed()));
            return;
        }

        NormalMove move = getGoteMove();

        if(move == null) {
            eventBus.fireEvent(new ChangeTutorialTextEvent(tutorialMessages.success()));
        } else if (move.getToSquare().getRow() == 9) {
            eventBus.fireEvent(new ChangeTutorialTextEvent(tutorialMessages.rookPracticeFailed2()));
        } else {
            final Move finalMove = move;
            Timer timer = new Timer() {
                @Override
                public void run() {
                    rulesEngine.playMoveInPosition(position, finalMove);
                    shogiBoard.displayPosition();
                }
            };
            timer.schedule(500);
        }
    }

    private NormalMove getGoteMove() {
        for (int row = 1; row <= 9; row++) {
            for (int col = 1; col <= 9; col++) {
                if (position.hasWhitePieceAt(Square.of(col, row))) {
                    GWT.log("col: " + col + " row: " + row);
                    return new NormalMove(Piece.GOTE_PAWN, Square.of(col, row), Square.of(col, row + 1));
                }
            }
        }
        return null;
    }

}
