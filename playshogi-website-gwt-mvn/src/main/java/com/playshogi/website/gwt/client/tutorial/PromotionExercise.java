package com.playshogi.website.gwt.client.tutorial;

import com.playshogi.website.gwt.client.SessionInformation;
import com.playshogi.website.gwt.client.i18n.TutorialMessages;
import com.playshogi.website.gwt.client.widget.board.ShogiBoard;
import com.playshogi.website.gwt.shared.services.ComputerServiceAsync;

public class PromotionExercise extends PositionVsComputerTutorial {

    private static final String SFEN = "9/k8/2K6/1P7/9/9/9/9/9 b -";
    private final TutorialMessages tutorialMessages;

    PromotionExercise(final ShogiBoard shogiBoard, final ComputerServiceAsync computerService,
                      final SessionInformation sessionInformation, final TutorialMessages tutorialMessages) {
        super(shogiBoard, computerService, sessionInformation, SFEN);
        this.tutorialMessages = tutorialMessages;
    }

    @Override
    String getIntroMessage() {
        return tutorialMessages.promotionPractice();
    }

    @Override
    String getSuccessMessage() {
        return tutorialMessages.success();
    }

    @Override
    public String getTutorialTitle() {
        return tutorialMessages.promotionTitle();
    }
}
