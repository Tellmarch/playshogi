package com.playshogi.website.gwt.client.tutorial;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.event.shared.EventBus;
import com.playshogi.library.shogi.models.formats.sfen.SfenConverter;
import com.playshogi.library.shogi.models.formats.usf.UsfMoveConverter;
import com.playshogi.library.shogi.rules.ShogiRulesEngine;
import com.playshogi.website.gwt.client.SessionInformation;
import com.playshogi.website.gwt.client.events.gametree.MovePlayedEvent;
import com.playshogi.website.gwt.client.events.tutorial.ChangeTutorialTextEvent;
import com.playshogi.website.gwt.client.events.tutorial.ChangeTutorialTitleEvent;
import com.playshogi.website.gwt.client.widget.board.ShogiBoard;
import com.playshogi.website.gwt.shared.services.ComputerServiceAsync;

public class PositionVsComputerTutorial implements Tutorial {

    private final ShogiBoard shogiBoard;
    private final ComputerServiceAsync computerService;
    private final SessionInformation sessionInformation;
    private final Messages messages;
    private final String positionSfen;
    private final ShogiRulesEngine shogiRulesEngine = new ShogiRulesEngine();
    private EventBus eventBus;

    PositionVsComputerTutorial(final ShogiBoard shogiBoard, final ComputerServiceAsync computerService,
                               final SessionInformation sessionInformation, final Messages messages,
                               final String positionSfen) {
        this.shogiBoard = shogiBoard;
        this.computerService = computerService;
        this.sessionInformation = sessionInformation;
        this.messages = messages;
        this.positionSfen = positionSfen;
    }

    @Override
    public void setup() {
        shogiBoard.getSelectionController().unlockSelection();
        shogiBoard.setPosition(SfenConverter.fromSFEN(positionSfen));
        shogiBoard.getBoardConfiguration().setAllowIllegalMoves(false);
        shogiBoard.getBoardConfiguration().setPlaySenteMoves(true);
        shogiBoard.getBoardConfiguration().setPlayGoteMoves(false);
        shogiBoard.getBoardConfiguration().setAllowPromotion(true);

        eventBus.fireEvent(new ChangeTutorialTextEvent(messages.getIntroMessage()));
        eventBus.fireEvent(new ChangeTutorialTitleEvent(messages.getTitle()));
    }

    @Override
    public void onMovePlayed(final MovePlayedEvent movePlayedEvent) {
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
                        if ("RSGN".equals(move)) {
                            eventBus.fireEvent(new ChangeTutorialTextEvent(messages.getSuccessMessage()));
                        }
                    }
                });
    }

    @Override
    public void activate(final EventBus eventBus) {

        this.eventBus = eventBus;
    }

    static class Messages {
        private final String title;
        private final String introMessage;
        private final String successMessage;
        private final String failureMessage;

        public Messages(final String title, final String introMessage, final String successMessage,
                        final String failureMessage) {
            this.title = title;
            this.introMessage = introMessage;
            this.successMessage = successMessage;
            this.failureMessage = failureMessage;
        }

        public String getTitle() {
            return title;
        }

        public String getIntroMessage() {
            return introMessage;
        }

        public String getSuccessMessage() {
            return successMessage;
        }

        public String getFailureMessage() {
            return failureMessage;
        }
    }
}
