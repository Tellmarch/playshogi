package com.playshogi.website.gwt.client.tutorial;

import com.google.web.bindery.event.shared.EventBus;
import com.playshogi.library.models.Square;
import com.playshogi.library.shogi.models.Piece;
import com.playshogi.library.shogi.models.PieceType;
import com.playshogi.library.shogi.models.moves.DropMove;
import com.playshogi.library.shogi.models.moves.ShogiMove;
import com.playshogi.library.shogi.models.position.ShogiPosition;
import com.playshogi.library.shogi.rules.ShogiRulesEngine;
import com.playshogi.website.gwt.client.events.gametree.MovePlayedEvent;
import com.playshogi.website.gwt.client.events.tutorial.ChangeTutorialTextEvent;
import com.playshogi.website.gwt.client.events.tutorial.ChangeTutorialTitleEvent;
import com.playshogi.website.gwt.client.widget.board.ShogiBoard;

public class BishopExercise implements Tutorial {

    private final ShogiRulesEngine rulesEngine = new ShogiRulesEngine();
    private final ShogiBoard shogiBoard;
    private final TutorialMessages tutorialMessages;
    private int i;
    private EventBus eventBus;
    private ShogiPosition position;
    private ShogiMove move;

    BishopExercise(ShogiBoard shogiBoard, TutorialMessages tutorialMessages) {
        this.shogiBoard = shogiBoard;
        this.tutorialMessages = tutorialMessages;
    }

    @Override
    public void activate(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    @Override
    public void setup() {
        shogiBoard.getSelectionController().unlockSelection();
        position = new ShogiPosition();
        position.getShogiBoardState().setPieceAt(4, 2, Piece.GOTE_KING);
        position.getShogiBoardState().setPieceAt(8, 2, Piece.GOTE_ROOK);
        position.getShogiBoardState().setPieceAt(2, 5, Piece.GOTE_ROOK);
        position.getSenteKomadai().addPiece(PieceType.BISHOP);
        i = 2;
        shogiBoard.setPosition(position);
        shogiBoard.getBoardConfiguration().setAllowIllegalMoves(false);
        shogiBoard.getBoardConfiguration().setPlaySenteMoves(true);
        shogiBoard.getBoardConfiguration().setPlayGoteMoves(false);

        eventBus.fireEvent(new ChangeTutorialTextEvent(tutorialMessages.bishopPractice()));
        eventBus.fireEvent(new ChangeTutorialTitleEvent(tutorialMessages.bishopTitle()));
    }

    @Override
    public void onMovePlayed(final MovePlayedEvent movePlayedEvent) {
        move = movePlayedEvent.getMove();
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
