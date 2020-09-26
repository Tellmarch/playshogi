package com.playshogi.website.gwt.client.events.puzzles;

import com.google.web.bindery.event.shared.binder.GenericEvent;
import com.playshogi.website.gwt.shared.models.SurvivalHighScore;

public class HighScoreListEvent extends GenericEvent {

    private final SurvivalHighScore[] highScores;

    public HighScoreListEvent(SurvivalHighScore[] highScores) {
        this.highScores = highScores;
    }

    public SurvivalHighScore[] getHighScores() {
        return highScores;
    }

}
