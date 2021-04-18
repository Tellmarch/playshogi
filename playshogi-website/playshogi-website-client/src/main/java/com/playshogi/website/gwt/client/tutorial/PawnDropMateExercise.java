package com.playshogi.website.gwt.client.tutorial;

import com.playshogi.website.gwt.client.SessionInformation;
import com.playshogi.website.gwt.client.i18n.TutorialMessages;
import com.playshogi.website.gwt.client.widget.board.ShogiBoard;
import com.playshogi.website.gwt.shared.services.ComputerServiceAsync;

public class PawnDropMateExercise extends PositionVsComputerTutorial {

    private static final String SFEN = "6nk1/9/7G1/9/9/9/9/9/4K4 b LP2r2b3g4s3n3l17p";

    private final TutorialMessages tutorialMessages;

    PawnDropMateExercise(final ShogiBoard shogiBoard, final ComputerServiceAsync computerService,
                         final SessionInformation sessionInformation, final TutorialMessages tutorialMessages) {
        super(shogiBoard, computerService, sessionInformation, SFEN);
        this.tutorialMessages = tutorialMessages;
    }

    @Override
    public String getTutorialTitle() {
        return tutorialMessages.pawnDropMateTitle();
    }

    @Override
    String getIntroMessage() {
        return tutorialMessages.pawnDropMateIntro();
    }

    @Override
    String getSuccessMessage() {
        return tutorialMessages.success();
    }

}
