package com.playshogi.website.gwt.client.tutorial;

import com.playshogi.website.gwt.client.SessionInformation;
import com.playshogi.website.gwt.client.i18n.TutorialMessages;
import com.playshogi.website.gwt.client.widget.board.ShogiBoard;
import com.playshogi.website.gwt.shared.services.ComputerServiceAsync;

public class GoldExercise extends PositionVsComputerTutorial {

    private static final String SFEN = "9/9/9/4k4/9/5K3/9/9/9 b 4G";
    private final TutorialMessages tutorialMessages;

    public GoldExercise(final ShogiBoard shogiBoard, final ComputerServiceAsync computerService,
                        final SessionInformation sessionInformation, final TutorialMessages tutorialMessages) {
        super(shogiBoard, computerService, sessionInformation, SFEN);
        this.tutorialMessages = tutorialMessages;
    }

    @Override
    public String getTutorialTitle() {
        return tutorialMessages.goldTitle();
    }

    @Override
    String getIntroMessage() {
        return tutorialMessages.goldPractice();
    }

    @Override
    String getSuccessMessage() {
        return tutorialMessages.goldPracticeSuccess();
    }
}
