package com.playshogi.website.gwt.client.tutorial;

import com.google.web.bindery.event.shared.EventBus;
import com.playshogi.library.models.Square;
import com.playshogi.library.shogi.models.position.ShogiPosition;
import com.playshogi.website.gwt.client.events.gametree.MovePlayedEvent;
import com.playshogi.website.gwt.client.events.tutorial.ChangeTutorialTextEvent;
import com.playshogi.website.gwt.client.i18n.TutorialMessages;
import com.playshogi.website.gwt.client.widget.board.ShogiBoard;

public class PromotionTutorial implements Tutorial {

    private final ShogiBoard shogiBoard;
    private final TutorialMessages tutorialMessages;
    private EventBus eventBus;

    public PromotionTutorial(final ShogiBoard shogiBoard, final TutorialMessages tutorialMessages) {
        this.shogiBoard = shogiBoard;
        this.tutorialMessages = tutorialMessages;
    }

    @Override
    public void setup() {
        shogiBoard.getSelectionController().unlockSelection();
        ShogiPosition shogiPosition = new ShogiPosition();

        shogiBoard.getBoardConfiguration().setAllowIllegalMoves(false);
        shogiBoard.getBoardConfiguration().setPlaySenteMoves(true);
        shogiBoard.getBoardConfiguration().setPlayGoteMoves(false);

        shogiBoard.setPosition(shogiPosition);
        for (int column = 1; column <= 9; column++) {
            for (int row = 1; row <= 3; row++) {
                shogiBoard.getSelectionController().selectSquare(Square.of(column, row));
            }
            for (int row = 7; row <= 9; row++) {
                shogiBoard.getSelectionController().selectSquare(Square.of(column, row));
            }
        }

        eventBus.fireEvent(new ChangeTutorialTextEvent(tutorialMessages.promotionIntro()));
    }

    @Override
    public String getTutorialTitle() {
        return tutorialMessages.promotionTitle();
    }

    @Override
    public void onMovePlayed(final MovePlayedEvent movePlayedEvent) {
    }

    @Override
    public void activate(final EventBus eventBus) {
        this.eventBus = eventBus;
    }
}
