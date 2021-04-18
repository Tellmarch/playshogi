package com.playshogi.website.gwt.client.tutorial;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.event.shared.EventBus;
import com.playshogi.library.shogi.models.formats.sfen.SfenConverter;
import com.playshogi.library.shogi.models.formats.usf.UsfMoveConverter;
import com.playshogi.library.shogi.models.position.ShogiPosition;
import com.playshogi.library.shogi.models.shogivariant.ShogiInitialPositionFactory;
import com.playshogi.library.shogi.rules.ShogiRulesEngine;
import com.playshogi.website.gwt.client.SessionInformation;
import com.playshogi.website.gwt.client.events.gametree.MovePlayedEvent;
import com.playshogi.website.gwt.client.events.tutorial.ChangeTutorialTextEvent;
import com.playshogi.website.gwt.client.i18n.TutorialMessages;
import com.playshogi.website.gwt.client.widget.board.ShogiBoard;
import com.playshogi.website.gwt.shared.services.ComputerServiceAsync;

public class Introduction implements Tutorial {

    private final ShogiBoard shogiBoard;
    private final TutorialMessages tutorialMessages;
    private final ComputerServiceAsync computerService;
    private final SessionInformation sessionInformation;
    private final ShogiRulesEngine shogiRulesEngine = new ShogiRulesEngine();
    private EventBus eventBus;

    Introduction(final ShogiBoard shogiBoard, final TutorialMessages tutorialMessages,
                 final ComputerServiceAsync computerService, final SessionInformation sessionInformation) {
        this.shogiBoard = shogiBoard;
        this.tutorialMessages = tutorialMessages;
        this.computerService = computerService;
        this.sessionInformation = sessionInformation;
    }

    @Override
    public String getTutorialTitle() {
        return tutorialMessages.introTitle();
    }

    @Override
    public void setup() {
        shogiBoard.getSelectionController().unlockSelection();
        ShogiPosition shogiPosition = ShogiInitialPositionFactory.createInitialPosition();

        shogiBoard.setPosition(shogiPosition);

        eventBus.fireEvent(new ChangeTutorialTextEvent(tutorialMessages.intro()));
    }

    @Override
    public void onMovePlayed(MovePlayedEvent movePlayedEvent) {
        shogiRulesEngine.playMoveInPosition(shogiBoard.getPosition(), movePlayedEvent.getMove());
        shogiBoard.displayPosition();

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
                    }
                });
    }

    @Override
    public void activate(EventBus eventBus) {
        this.eventBus = eventBus;
    }
}
