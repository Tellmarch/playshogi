package com.playshogi.website.gwt.client.tutorial;

import com.google.web.bindery.event.shared.EventBus;
import com.playshogi.library.models.Square;
import com.playshogi.library.shogi.models.formats.sfen.SfenConverter;
import com.playshogi.library.shogi.models.moves.ShogiMove;
import com.playshogi.library.shogi.models.position.ShogiPosition;
import com.playshogi.library.shogi.rules.ShogiRulesEngine;
import com.playshogi.website.gwt.client.events.gametree.MovePlayedEvent;
import com.playshogi.website.gwt.client.events.tutorial.ChangeTutorialTextEvent;
import com.playshogi.website.gwt.client.i18n.TutorialMessages;
import com.playshogi.website.gwt.client.widget.board.ShogiBoard;

public class LanceExercise implements Tutorial {

    private final ShogiRulesEngine rulesEngine = new ShogiRulesEngine();
    private final ShogiBoard shogiBoard;
    private final TutorialMessages tutorialMessages;
    private EventBus eventBus;
    private ShogiPosition position;

    LanceExercise(ShogiBoard shogiBoard, TutorialMessages tutorialMessages) {
        this.shogiBoard = shogiBoard;
        this.tutorialMessages = tutorialMessages;
    }

    @Override
    public String getTutorialTitle() {
        return tutorialMessages.lanceTitle();
    }

    @Override
    public void activate(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    @Override
    public void setup() {
        shogiBoard.getSelectionController().unlockSelection();
        position = SfenConverter.fromSFEN("9/2k6/9/9/9/2b6/9/9/9 b L");
        shogiBoard.setPosition(position);
        shogiBoard.getBoardConfiguration().setAllowIllegalMoves(false);
        shogiBoard.getBoardConfiguration().setPlayBlackMoves(true);
        shogiBoard.getBoardConfiguration().setPlayWhiteMoves(false);

        eventBus.fireEvent(new ChangeTutorialTextEvent(tutorialMessages.lanceExercise()));
    }

    @Override
    public void onMovePlayed(final MovePlayedEvent movePlayedEvent) {
        ShogiMove move = movePlayedEvent.getMove();

        if ("L*7g".equals(move.toString()) || "L*7h".equals(move.toString()) || "L*7i".equals(move.toString())) {
            rulesEngine.playMoveInPosition(position, move);
            shogiBoard.displayPosition();
            shogiBoard.getSelectionController().selectSquare(Square.of(7, 2));
            shogiBoard.getSelectionController().selectSquare(Square.of(7, 6));
            shogiBoard.getSelectionController().lockSelection();
            eventBus.fireEvent(new ChangeTutorialTextEvent(tutorialMessages.lancePracticeSuccess()));
        } else {
            eventBus.fireEvent(new ChangeTutorialTextEvent(tutorialMessages.lancePracticeFailed()));
            rulesEngine.playMoveInPosition(position, move);
            shogiBoard.displayPosition();
        }
    }

}
