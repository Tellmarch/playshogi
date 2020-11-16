package com.playshogi.website.gwt.client.tutorial;

import com.google.web.bindery.event.shared.EventBus;
import com.playshogi.library.models.Square;
import com.playshogi.library.shogi.models.Piece;
import com.playshogi.library.shogi.models.PieceType;
import com.playshogi.library.shogi.models.Player;
import com.playshogi.library.shogi.models.position.ShogiPosition;
import com.playshogi.library.shogi.rules.ShogiRulesEngine;
import com.playshogi.website.gwt.client.events.gametree.MovePlayedEvent;
import com.playshogi.website.gwt.client.events.tutorial.ChangeTutorialTextEvent;
import com.playshogi.website.gwt.client.events.tutorial.ChangeTutorialTitleEvent;
import com.playshogi.website.gwt.client.widget.board.ShogiBoard;

public class PieceMovementTutorial implements Tutorial {

    private final ShogiBoard shogiBoard;
    private final Piece piece;
    private final TutorialMessages tutorialMessages;
    private final ShogiRulesEngine shogiRulesEngine = new ShogiRulesEngine();
    private EventBus eventBus;

    PieceMovementTutorial(ShogiBoard shogiBoard, Piece piece, TutorialMessages tutorialMessages) {
        this.shogiBoard = shogiBoard;
        this.piece = piece;
        this.tutorialMessages = tutorialMessages;
    }

    @Override
    public void activate(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    @Override
    public void setup() {
        shogiBoard.getSelectionController().unlockSelection();
        ShogiPosition shogiPosition = new ShogiPosition();

        int row = 5;
        if (piece.getPieceType() == PieceType.KNIGHT || piece.getPieceType() == PieceType.LANCE || piece.getPieceType() == PieceType.PAWN) {
            row = 7;
        }

        shogiPosition.getShogiBoardState().setPieceAt(5, row, piece);

        shogiBoard.setPosition(shogiPosition);
        shogiBoard.getSelectionController().selectPossibleMoves(Square.of(5, row), shogiPosition);
        shogiBoard.getSelectionController().selectSquare(Square.of(5, row));

        eventBus.fireEvent(new ChangeTutorialTextEvent(getTutorialText()));
        eventBus.fireEvent(new ChangeTutorialTitleEvent(getTutorialTitle()));
    }

    @Override
    public void onMovePlayed(MovePlayedEvent movePlayedEvent) {
        shogiRulesEngine.playMoveInPosition(shogiBoard.getPosition(), movePlayedEvent.getMove());
        shogiBoard.getPosition().setPlayerToMove(Player.BLACK);
        shogiBoard.displayPosition();
    }

    private String getTutorialText() {
        switch (piece.getPieceType()) {
            case PAWN:
                return tutorialMessages.inProgress();
            case LANCE:
                return tutorialMessages.inProgress();
            case KNIGHT:
                return tutorialMessages.inProgress();
            case SILVER:
                return tutorialMessages.inProgress();
            case GOLD:
                return tutorialMessages.inProgress();
            case BISHOP:
                return tutorialMessages.inProgress();
            case ROOK:
                return tutorialMessages.rookIntro();
            case KING:
                return tutorialMessages.kingIntro();
        }
        return "";
    }

    public String getTutorialTitle() {
        switch (piece.getPieceType()) {
            case PAWN:
                return tutorialMessages.pawnTitle();
            case LANCE:
                return tutorialMessages.lanceTitle();
            case KNIGHT:
                return tutorialMessages.knightTitle();
            case SILVER:
                return tutorialMessages.silverTitle();
            case GOLD:
                return tutorialMessages.goldTitle();
            case BISHOP:
                return tutorialMessages.bishopTitle();
            case ROOK:
                return tutorialMessages.rookTitle();
            case KING:
                return tutorialMessages.kingTitle();
        }
        return "";
    }
}
