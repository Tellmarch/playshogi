package com.playshogi.website.gwt.client.tutorial;

import com.google.web.bindery.event.shared.EventBus;
import com.playshogi.library.shogi.models.Piece;
import com.playshogi.library.shogi.models.moves.DropMove;
import com.playshogi.library.shogi.models.position.ShogiPosition;
import com.playshogi.library.shogi.rules.ShogiRulesEngine;
import com.playshogi.website.gwt.client.events.gametree.MovePlayedEvent;
import com.playshogi.website.gwt.client.events.tutorial.ChangeTutorialTextEvent;
import com.playshogi.website.gwt.client.i18n.TutorialMessages;
import com.playshogi.website.gwt.client.widget.board.ShogiBoard;

public class CaptureTutorial implements Tutorial {

    private final ShogiBoard shogiBoard;
    private final TutorialMessages tutorialMessages;
    private final ShogiRulesEngine shogiRulesEngine = new ShogiRulesEngine();
    private EventBus eventBus;

    public CaptureTutorial(final ShogiBoard shogiBoard, final TutorialMessages tutorialMessages) {
        this.shogiBoard = shogiBoard;
        this.tutorialMessages = tutorialMessages;
    }

    @Override
    public void setup() {
        shogiBoard.getSelectionController().unlockSelection();
        ShogiPosition position = new ShogiPosition();
        position.getShogiBoardState().setPieceAt(5, 5, Piece.SENTE_KING);
        position.getShogiBoardState().setPieceAt(4, 4, Piece.GOTE_PAWN);
        shogiBoard.setPosition(position);
        shogiBoard.getBoardConfiguration().setAllowIllegalMoves(false);
        shogiBoard.getBoardConfiguration().setPlayBlackMoves(true);
        shogiBoard.getBoardConfiguration().setPlayWhiteMoves(false);

        eventBus.fireEvent(new ChangeTutorialTextEvent(tutorialMessages.captureAndDropsIntro()));
    }

    @Override
    public void onMovePlayed(final MovePlayedEvent movePlayedEvent) {
        shogiRulesEngine.playMoveInPosition(shogiBoard.getPosition(), movePlayedEvent.getMove());
        shogiBoard.getPosition().incrementMoveCount();
        shogiBoard.displayPosition();

        if (movePlayedEvent.getMove() instanceof DropMove) {
            eventBus.fireEvent(new ChangeTutorialTextEvent(tutorialMessages.success()));
        }
    }

    @Override
    public void activate(final EventBus eventBus) {
        this.eventBus = eventBus;
    }

    @Override
    public String getTutorialTitle() {
        return tutorialMessages.captureAndDropsTitle();
    }
}
