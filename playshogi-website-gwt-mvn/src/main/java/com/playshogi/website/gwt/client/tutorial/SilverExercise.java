package com.playshogi.website.gwt.client.tutorial;

import com.google.web.bindery.event.shared.EventBus;
import com.playshogi.library.models.Square;
import com.playshogi.library.shogi.models.Piece;
import com.playshogi.library.shogi.models.formats.sfen.SfenConverter;
import com.playshogi.library.shogi.models.moves.CaptureMove;
import com.playshogi.library.shogi.models.moves.NormalMove;
import com.playshogi.library.shogi.models.moves.ShogiMove;
import com.playshogi.library.shogi.models.position.ShogiPosition;
import com.playshogi.library.shogi.rules.ShogiRulesEngine;
import com.playshogi.website.gwt.client.events.gametree.MovePlayedEvent;
import com.playshogi.website.gwt.client.events.tutorial.ChangeTutorialTextEvent;
import com.playshogi.website.gwt.client.i18n.TutorialMessages;
import com.playshogi.website.gwt.client.widget.board.ShogiBoard;

public class SilverExercise implements Tutorial {

    private static final Square TARGET_SQUARE = Square.of(1, 7);

    private final ShogiRulesEngine rulesEngine = new ShogiRulesEngine();
    private final ShogiBoard shogiBoard;
    private final TutorialMessages tutorialMessages;
    private EventBus eventBus;
    private ShogiPosition position;

    SilverExercise(ShogiBoard shogiBoard, TutorialMessages tutorialMessages) {
        this.shogiBoard = shogiBoard;
        this.tutorialMessages = tutorialMessages;
    }

    @Override
    public String getTutorialTitle() {
        return tutorialMessages.silverTitle();
    }

    @Override
    public void activate(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    @Override
    public void setup() {
        shogiBoard.getSelectionController().unlockSelection();
        position = SfenConverter.fromSFEN("9/9/9/3G1G3/1P1P1P3/P1P1P1P1P/2G1K2P+p/2R4G1/S1B6 b -");

        shogiBoard.setPosition(position);
        shogiBoard.getBoardConfiguration().setAllowIllegalMoves(false);
        shogiBoard.getBoardConfiguration().setPlayBlackMoves(true);
        shogiBoard.getBoardConfiguration().setPlayWhiteMoves(false);

        eventBus.fireEvent(new ChangeTutorialTextEvent(tutorialMessages.silverPractice()));

        shogiBoard.getSelectionController().selectSquare(TARGET_SQUARE);
        shogiBoard.getSelectionController().selectSquare(Square.of(9, 9));
    }

    @Override
    public void onMovePlayed(final MovePlayedEvent movePlayedEvent) {
        ShogiMove move = movePlayedEvent.getMove();

        if (move instanceof CaptureMove && ((CaptureMove) move).getPiece() == Piece.SENTE_SILVER) {
            rulesEngine.playMoveInPosition(position, move);
            shogiBoard.displayPosition();
            eventBus.fireEvent(new ChangeTutorialTextEvent(tutorialMessages.silverPracticeSuccess()));
            return;
        }

        if (!(move instanceof NormalMove) || ((NormalMove) move).getPiece() != Piece.SENTE_SILVER) {
            eventBus.fireEvent(new ChangeTutorialTextEvent(tutorialMessages.silverPracticeFailed()));
            return;
        }

        rulesEngine.playMoveInPosition(position, move);
        shogiBoard.displayPosition();

        position.incrementMoveCount();
    }

}
