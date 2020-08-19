package com.playshogi.website.gwt.client.tutorial;

import com.google.gwt.core.client.GWT;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;
import com.playshogi.library.shogi.models.Piece;
import com.playshogi.website.gwt.client.ui.TutorialView;
import com.playshogi.website.gwt.client.widget.board.ShogiBoard;

@Singleton
public class Tutorials {

    private final Tutorial[] tutorials;

    @Inject
    public Tutorials(final TutorialView tutorialView) {
        ShogiBoard shogiBoard = tutorialView.getShogiBoard();
        TutorialMessages tutorialMessages = GWT.create(TutorialMessages.class);
        tutorials = new Tutorial[]{
                new Introduction(shogiBoard, tutorialMessages),
                new PieceMovementTutorial(shogiBoard, Piece.SENTE_KING, tutorialMessages),
                new KingExercise(shogiBoard, tutorialMessages),
                new PieceMovementTutorial(shogiBoard, Piece.SENTE_ROOK, tutorialMessages),
                new RookExercise(shogiBoard, tutorialMessages)
        };
    }

    public Tutorial getChapter(int chapter) {
        if(hasChapter(chapter)) {
            return tutorials[chapter - 1];
        } else {
            return null;
        }
    }

    public boolean hasChapter(int chapter) {
        return chapter>0 && chapter <= tutorials.length;
    }

    public void activate(final EventBus eventBus) {
        GWT.log("Activating tutorials");
        for (Tutorial tutorial : tutorials) {
            tutorial.activate(eventBus);
        }
    }
}
