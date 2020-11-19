package com.playshogi.website.gwt.client.tutorial;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.event.shared.EventBus;
import com.playshogi.library.shogi.models.formats.sfen.SfenConverter;
import com.playshogi.library.shogi.models.formats.usf.UsfMoveConverter;
import com.playshogi.library.shogi.models.position.ShogiPosition;
import com.playshogi.library.shogi.models.shogivariant.Handicap;
import com.playshogi.library.shogi.models.shogivariant.ShogiInitialPositionFactory;
import com.playshogi.library.shogi.rules.ShogiRulesEngine;
import com.playshogi.website.gwt.client.SessionInformation;
import com.playshogi.website.gwt.client.events.gametree.MovePlayedEvent;
import com.playshogi.website.gwt.client.events.tutorial.ChangeTutorialTextEvent;
import com.playshogi.website.gwt.client.i18n.TutorialMessages;
import com.playshogi.website.gwt.client.widget.board.ShogiBoard;
import com.playshogi.website.gwt.shared.services.ComputerServiceAsync;

public class PracticeTutorial implements Tutorial {

    private static final Handicap[] HANDICAPS = new Handicap[]{Handicap.NAKED_KING, Handicap.TEN_PIECES,
            Handicap.THREE_PAWNS, Handicap.EIGHT_PIECES};

    private final ShogiBoard shogiBoard;
    private final TutorialMessages tutorialMessages;
    private final ComputerServiceAsync computerService;
    private final SessionInformation sessionInformation;
    private final ShogiRulesEngine shogiRulesEngine = new ShogiRulesEngine();
    private final int level;
    private EventBus eventBus;

    PracticeTutorial(final ShogiBoard shogiBoard, final TutorialMessages tutorialMessages,
                     final ComputerServiceAsync computerService, final SessionInformation sessionInformation,
                     final int level) {
        this.shogiBoard = shogiBoard;
        this.tutorialMessages = tutorialMessages;
        this.computerService = computerService;
        this.sessionInformation = sessionInformation;
        this.level = level;
    }

    @Override
    public String getTutorialTitle() {
        return tutorialMessages.practiceTitle();
    }

    @Override
    public void setup() {
        shogiBoard.getSelectionController().unlockSelection();
        ShogiPosition shogiPosition = ShogiInitialPositionFactory.createInitialPosition(HANDICAPS[level]);

        shogiBoard.getBoardConfiguration().setAllowIllegalMoves(false);
        shogiBoard.getBoardConfiguration().setPlaySenteMoves(true);
        shogiBoard.getBoardConfiguration().setPlayGoteMoves(false);

        shogiBoard.setPosition(shogiPosition);
        if (level == 0) {
            eventBus.fireEvent(new ChangeTutorialTextEvent(tutorialMessages.practiceIntro1()));
        } else if (level == 1) {
            eventBus.fireEvent(new ChangeTutorialTextEvent(tutorialMessages.practiceIntro2()));
        } else if (level == 2) {
            eventBus.fireEvent(new ChangeTutorialTextEvent(tutorialMessages.practiceIntro3()));
        } else {
            eventBus.fireEvent(new ChangeTutorialTextEvent(tutorialMessages.practiceIntro4()));
        }
        getComputerMove();
    }

    @Override
    public void onMovePlayed(MovePlayedEvent movePlayedEvent) {
        shogiRulesEngine.playMoveInPosition(shogiBoard.getPosition(), movePlayedEvent.getMove());
        shogiBoard.displayPosition();

        getComputerMove();
    }

    private void getComputerMove() {
        computerService.getComputerMove(sessionInformation.getSessionId(),
                SfenConverter.toSFEN(shogiBoard.getPosition()),
                new AsyncCallback<String>() {
                    @Override
                    public void onFailure(final Throwable throwable) {
                        GWT.log("getComputerMove failed");
                    }

                    @Override
                    public void onSuccess(final String move) {
                        GWT.log("getComputerMove success: " + move);
                        shogiRulesEngine.playMoveInPosition(shogiBoard.getPosition(),
                                UsfMoveConverter.fromUsfString(move, shogiBoard.getPosition()));
                        shogiBoard.displayPosition();
                        if ("RSGN".equals(move)) {
                            if (level == HANDICAPS.length - 1) {
                                eventBus.fireEvent(new ChangeTutorialTextEvent(tutorialMessages.practiceSuccess()));
                            } else {
                                eventBus.fireEvent(new ChangeTutorialTextEvent(tutorialMessages.practiceNextLevel()));
                            }
                        }
                    }
                });
    }

    @Override
    public void activate(EventBus eventBus) {
        this.eventBus = eventBus;
    }
}
