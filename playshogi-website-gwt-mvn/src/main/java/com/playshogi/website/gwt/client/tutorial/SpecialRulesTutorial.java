package com.playshogi.website.gwt.client.tutorial;

import com.google.web.bindery.event.shared.EventBus;
import com.playshogi.library.shogi.models.formats.sfen.SfenConverter;
import com.playshogi.library.shogi.models.position.ShogiPosition;
import com.playshogi.website.gwt.client.events.gametree.MovePlayedEvent;
import com.playshogi.website.gwt.client.events.tutorial.ChangeTutorialTextEvent;
import com.playshogi.website.gwt.client.i18n.TutorialMessages;
import com.playshogi.website.gwt.client.widget.board.ShogiBoard;

public class SpecialRulesTutorial implements Tutorial {

    private final ShogiBoard shogiBoard;
    private final TutorialMessages tutorialMessages;
    private EventBus eventBus;
    private ShogiPosition position;

    SpecialRulesTutorial(ShogiBoard shogiBoard, TutorialMessages tutorialMessages) {
        this.shogiBoard = shogiBoard;
        this.tutorialMessages = tutorialMessages;
    }

    @Override
    public String getTutorialTitle() {
        return tutorialMessages.specialTitle();
    }

    @Override
    public void activate(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    @Override
    public void setup() {
        shogiBoard.getSelectionController().unlockSelection();
        position = SfenConverter.fromSFEN("3k5/9/ppppppppp/9/9/9/PPPPPPP1P/9/5K3 b P");

        shogiBoard.setPosition(position);
        shogiBoard.getBoardConfiguration().setAllowIllegalMoves(false);
        shogiBoard.getBoardConfiguration().setPlayBlackMoves(false);
        shogiBoard.getBoardConfiguration().setPlayWhiteMoves(false);

        eventBus.fireEvent(new ChangeTutorialTextEvent(tutorialMessages.specialIntro()));
    }

    @Override
    public void onMovePlayed(final MovePlayedEvent movePlayedEvent) {
    }

}
