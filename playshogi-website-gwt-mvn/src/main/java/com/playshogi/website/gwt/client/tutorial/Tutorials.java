package com.playshogi.website.gwt.client.tutorial;

import com.google.gwt.core.client.GWT;
import com.google.web.bindery.event.shared.EventBus;
import com.playshogi.library.shogi.models.Piece;
import com.playshogi.website.gwt.client.ui.TutorialView;
import com.playshogi.website.gwt.client.widget.board.ShogiBoard;

public class Tutorials {

    private final Tutorial[] tutorials;

    public Tutorials(final TutorialView tutorialView) {
        ShogiBoard shogiBoard = tutorialView.getShogiBoard();
        TutorialMessages tutorialMessages = GWT.create(TutorialMessages.class);
        tutorials = new Tutorial[]{
                new Introduction(shogiBoard, tutorialMessages),
                new PieceMovementTutorial(shogiBoard, Piece.SENTE_KING, tutorialMessages),
                new KingExercise(shogiBoard, tutorialMessages),
                new PieceMovementTutorial(shogiBoard, Piece.SENTE_ROOK, tutorialMessages),
                new RookExercise(shogiBoard, tutorialMessages),
                new PieceMovementTutorial(shogiBoard, Piece.SENTE_BISHOP, tutorialMessages),
                new BishopExercise(shogiBoard, tutorialMessages),
                new PieceMovementTutorial(shogiBoard, Piece.SENTE_GOLD, tutorialMessages),
                new PieceMovementTutorial(shogiBoard, Piece.SENTE_SILVER, tutorialMessages),
                new PieceMovementTutorial(shogiBoard, Piece.SENTE_KNIGHT, tutorialMessages),
                new PieceMovementTutorial(shogiBoard, Piece.SENTE_LANCE, tutorialMessages),
                new PieceMovementTutorial(shogiBoard, Piece.SENTE_PAWN, tutorialMessages),
        };
    }

    public Tutorial[] getTutorials() {
        return tutorials;
    }

    public Tutorial getChapter(int chapter) {
        if (hasChapter(chapter)) {
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
