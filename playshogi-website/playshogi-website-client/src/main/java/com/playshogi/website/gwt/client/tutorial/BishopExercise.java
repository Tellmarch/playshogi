package com.playshogi.website.gwt.client.tutorial;

import com.google.web.bindery.event.shared.EventBus;
import com.playshogi.library.shogi.models.Piece;
import com.playshogi.library.shogi.models.PieceType;
import com.playshogi.library.shogi.models.moves.DropMove;
import com.playshogi.library.shogi.models.moves.ShogiMove;
import com.playshogi.library.shogi.models.position.ShogiPosition;
import com.playshogi.library.shogi.models.position.Square;
import com.playshogi.library.shogi.rules.ShogiRulesEngine;
import com.playshogi.website.gwt.client.events.gametree.MovePlayedEvent;
import com.playshogi.website.gwt.client.events.tutorial.ChangeTutorialTextEvent;
import com.playshogi.website.gwt.client.i18n.TutorialMessages;
import com.playshogi.website.gwt.client.widget.board.ShogiBoard;

public class BishopExercise implements Tutorial {

    private final ShogiRulesEngine rulesEngine = new ShogiRulesEngine();
    private final ShogiBoard shogiBoard;
    private final TutorialMessages tutorialMessages;
    private EventBus eventBus;
    private ShogiPosition position;

    BishopExercise(ShogiBoard shogiBoard, TutorialMessages tutorialMessages) {
        this.shogiBoard = shogiBoard;
        this.tutorialMessages = tutorialMessages;
    }

    @Override
    public String getTutorialTitle() {
        return tutorialMessages.bishopTitle();
    }

    @Override
    public void activate(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    @Override
    public void setup() {
        shogiBoard.getSelectionController().unlockSelection();
        position = new ShogiPosition();
        position.getMutableShogiBoardState().setPieceAt(4, 2, Piece.GOTE_KING);
        position.getMutableShogiBoardState().setPieceAt(8, 2, Piece.GOTE_ROOK);
        position.getMutableShogiBoardState().setPieceAt(2, 5, Piece.GOTE_ROOK);
        position.getMutableSenteKomadai().addPiece(PieceType.BISHOP);
        shogiBoard.setPosition(position);
        shogiBoard.getBoardConfiguration().setAllowIllegalMoves(false);
        shogiBoard.getBoardConfiguration().setPlayBlackMoves(true);
        shogiBoard.getBoardConfiguration().setPlayWhiteMoves(false);

        eventBus.fireEvent(new ChangeTutorialTextEvent(tutorialMessages.bishopPractice()));
    }

    @Override
    public void onMovePlayed(final MovePlayedEvent movePlayedEvent) {
        ShogiMove move = movePlayedEvent.getMove();
        rulesEngine.playMoveInPosition(position, move);
        shogiBoard.displayPosition();

        if (movePlayedEvent.getMove() instanceof DropMove) {
            DropMove dropMove = (DropMove) movePlayedEvent.getMove();

            shogiBoard.getSelectionController().selectPossibleMoves(dropMove.getToSquare(), position);
            shogiBoard.getSelectionController().lockSelection();

            if (dropMove.getToSquare().equals(Square.of(6, 4))) {
                eventBus.fireEvent(new ChangeTutorialTextEvent(tutorialMessages.bishopPracticeSuccess()));
            } else {
                eventBus.fireEvent(new ChangeTutorialTextEvent(tutorialMessages.bishopPracticeFailed()));
            }
        }
    }

}
