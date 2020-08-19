package com.playshogi.website.gwt.client.tutorial;

import com.google.web.bindery.event.shared.EventBus;
import com.playshogi.library.models.Square;
import com.playshogi.library.shogi.models.Piece;
import com.playshogi.library.shogi.models.position.ShogiPosition;
import com.playshogi.library.shogi.rules.ShogiRulesEngine;
import com.playshogi.website.gwt.client.events.ChangeTutorialTextEvent;
import com.playshogi.website.gwt.client.events.ChangeTutorialTitleEvent;
import com.playshogi.website.gwt.client.events.MovePlayedEvent;
import com.playshogi.website.gwt.client.widget.board.ShogiBoard;

import java.util.List;

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
        ShogiPosition shogiPosition = new ShogiPosition();
        shogiPosition.getShogiBoardState().setPieceAt(5, 5, piece);

        shogiBoard.setPosition(shogiPosition);
        List<Square> possibleTargets =
                shogiRulesEngine.getPossibleTargetSquares(shogiPosition,
                        Square.of(5, 5));
        for (Square square : possibleTargets) {
            shogiBoard.selectSquare(square);
        }

        eventBus.fireEvent(new ChangeTutorialTextEvent(getTutorialText()));
        eventBus.fireEvent(new ChangeTutorialTitleEvent(getTutorialTitle()));
    }

    @Override
    public void onMovePlayed(MovePlayedEvent movePlayedEvent) {
    }

    private String getTutorialText() {
        switch(piece.getPieceType()) {
            case PAWN:
                return tutorialMessages.kingIntro();
            case LANCE:
                return tutorialMessages.kingIntro();
            case KNIGHT:
                return tutorialMessages.kingIntro();
            case SILVER:
                return tutorialMessages.kingIntro();
            case GOLD:
                return tutorialMessages.kingIntro();
            case BISHOP:
                return tutorialMessages.kingIntro();
            case ROOK:
                return tutorialMessages.kingIntro();
            case KING:
                return tutorialMessages.kingIntro();
        }
        return "";
    }

    private String getTutorialTitle() {
        switch(piece.getPieceType()) {
            case PAWN:
                return tutorialMessages.kingTitle();
            case LANCE:
                return tutorialMessages.kingTitle();
            case KNIGHT:
                return tutorialMessages.kingTitle();
            case SILVER:
                return tutorialMessages.kingTitle();
            case GOLD:
                return tutorialMessages.kingTitle();
            case BISHOP:
                return tutorialMessages.kingTitle();
            case ROOK:
                return tutorialMessages.kingTitle();
            case KING:
                return tutorialMessages.kingTitle();
        }
        return "";
    }
}
