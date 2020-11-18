package com.playshogi.website.gwt.client.tutorial;

import com.google.web.bindery.event.shared.EventBus;
import com.playshogi.library.models.Square;
import com.playshogi.library.shogi.models.formats.sfen.SfenConverter;
import com.playshogi.library.shogi.models.moves.ShogiMove;
import com.playshogi.library.shogi.models.position.ShogiPosition;
import com.playshogi.library.shogi.rules.ShogiRulesEngine;
import com.playshogi.website.gwt.client.events.gametree.MovePlayedEvent;
import com.playshogi.website.gwt.client.events.tutorial.ChangeTutorialTextEvent;
import com.playshogi.website.gwt.client.events.tutorial.ChangeTutorialTitleEvent;
import com.playshogi.website.gwt.client.i18n.TutorialMessages;
import com.playshogi.website.gwt.client.widget.board.ShogiBoard;

public class KnightExercise implements Tutorial {

    private final ShogiRulesEngine rulesEngine = new ShogiRulesEngine();
    private final ShogiBoard shogiBoard;
    private final TutorialMessages tutorialMessages;
    private EventBus eventBus;
    private ShogiPosition position;

    KnightExercise(ShogiBoard shogiBoard, TutorialMessages tutorialMessages) {
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
        position = SfenConverter.fromSFEN("lngg3nl/1ks4r1/1p1pspbp1/p1p1p1p1p/9/PPPP1PP1P/1SNGPSNP1/1KGB5/L6RL b -");

        shogiBoard.setPosition(position);
        shogiBoard.getBoardConfiguration().setAllowIllegalMoves(false);
        shogiBoard.getBoardConfiguration().setPlaySenteMoves(true);
        shogiBoard.getBoardConfiguration().setPlayGoteMoves(false);

        eventBus.fireEvent(new ChangeTutorialTextEvent(tutorialMessages.knightPractice()));
        eventBus.fireEvent(new ChangeTutorialTitleEvent(tutorialMessages.knightTitle()));

    }

    @Override
    public void onMovePlayed(final MovePlayedEvent movePlayedEvent) {
        ShogiMove move = movePlayedEvent.getMove();

        if ("3g4e".equals(move.toString())) {
            rulesEngine.playMoveInPosition(position, move);
            shogiBoard.displayPosition();
            shogiBoard.getSelectionController().selectSquare(Square.of(3, 3));
            shogiBoard.getSelectionController().selectSquare(Square.of(5, 3));
            shogiBoard.getSelectionController().lockSelection();
            eventBus.fireEvent(new ChangeTutorialTextEvent(tutorialMessages.knightPracticeSuccess()));
        } else {
            eventBus.fireEvent(new ChangeTutorialTextEvent(tutorialMessages.knightPracticeFailed()));
        }

        rulesEngine.playMoveInPosition(position, move);
        shogiBoard.displayPosition();

        position.incrementMoveCount();
    }

}
