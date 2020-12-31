package com.playshogi.website.gwt.client.tutorial;

import com.google.gwt.user.client.Timer;
import com.google.web.bindery.event.shared.EventBus;
import com.playshogi.library.models.Square;
import com.playshogi.library.shogi.models.Piece;
import com.playshogi.library.shogi.models.moves.CaptureMove;
import com.playshogi.library.shogi.models.moves.NormalMove;
import com.playshogi.library.shogi.models.moves.ShogiMove;
import com.playshogi.library.shogi.models.position.ShogiPosition;
import com.playshogi.library.shogi.rules.ShogiRulesEngine;
import com.playshogi.website.gwt.client.events.gametree.MovePlayedEvent;
import com.playshogi.website.gwt.client.events.tutorial.ChangeTutorialTextEvent;
import com.playshogi.website.gwt.client.i18n.TutorialMessages;
import com.playshogi.website.gwt.client.widget.board.ShogiBoard;

public class KingExercise implements Tutorial {

    private final ShogiRulesEngine rulesEngine = new ShogiRulesEngine();
    private final ShogiBoard shogiBoard;
    private final TutorialMessages tutorialMessages;
    private int i;
    private EventBus eventBus;
    private ShogiPosition position;

    KingExercise(ShogiBoard shogiBoard, TutorialMessages tutorialMessages) {
        this.shogiBoard = shogiBoard;
        this.tutorialMessages = tutorialMessages;
    }

    @Override
    public String getTutorialTitle() {
        return tutorialMessages.kingTitle();
    }

    @Override
    public void activate(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    @Override
    public void setup() {
        shogiBoard.getSelectionController().unlockSelection();
        position = new ShogiPosition();
        position.getMutableShogiBoardState().setPieceAt(5, 9, Piece.SENTE_KING);
        position.getMutableShogiBoardState().setPieceAt(2, 2, Piece.GOTE_PAWN);
        i = 2;
        shogiBoard.setPosition(position);
        shogiBoard.getBoardConfiguration().setAllowIllegalMoves(false);
        shogiBoard.getBoardConfiguration().setPlayBlackMoves(true);
        shogiBoard.getBoardConfiguration().setPlayWhiteMoves(false);

        eventBus.fireEvent(new ChangeTutorialTextEvent(tutorialMessages.kingPractice()));
    }

    @Override
    public void onMovePlayed(final MovePlayedEvent movePlayedEvent) {
        ShogiMove move = movePlayedEvent.getMove();
        rulesEngine.playMoveInPosition(position, move);
        shogiBoard.displayPosition();

        if(move instanceof CaptureMove) {
            eventBus.fireEvent(new ChangeTutorialTextEvent(tutorialMessages.kingPracticeSuccess()));
        } else if(((NormalMove) move).getToSquare().equals(Square.of(2, i+1))) {
            eventBus.fireEvent(new ChangeTutorialTextEvent(tutorialMessages.kingPracticeFailed()));
        } else {
            Timer timer = new Timer() {
                @Override
                public void run() {
                    rulesEngine.playMoveInPosition(position, new NormalMove(Piece.GOTE_PAWN, Square.of(2, i), Square.of(2, ++i)));
                    shogiBoard.displayPosition();
                }
            };
            timer.schedule(500);
        }
    }

}
