package com.playshogi.website.gwt.shared.models;

import java.io.Serializable;
import java.util.Arrays;

public class GameInsightsDetails implements Serializable {
    private int blackAvgCentipawnLoss;
    private int whiteAvgCentipawnLoss;
    private MistakeDetails[] blackMistakes;
    private MistakeDetails[] whiteMistakes;

    public GameInsightsDetails() {
    }

    public int getBlackAvgCentipawnLoss() {
        return blackAvgCentipawnLoss;
    }

    public void setBlackAvgCentipawnLoss(final int blackAvgCentipawnLoss) {
        this.blackAvgCentipawnLoss = blackAvgCentipawnLoss;
    }

    public int getWhiteAvgCentipawnLoss() {
        return whiteAvgCentipawnLoss;
    }

    public void setWhiteAvgCentipawnLoss(final int whiteAvgCentipawnLoss) {
        this.whiteAvgCentipawnLoss = whiteAvgCentipawnLoss;
    }

    public MistakeDetails[] getBlackMistakes() {
        return blackMistakes;
    }

    public void setBlackMistakes(final MistakeDetails[] blackMistakes) {
        this.blackMistakes = blackMistakes;
    }

    public MistakeDetails[] getWhiteMistakes() {
        return whiteMistakes;
    }

    public void setWhiteMistakes(final MistakeDetails[] whiteMistakes) {
        this.whiteMistakes = whiteMistakes;
    }

    @Override
    public String toString() {
        return "GameInsightsDetails{" +
                "blackAvgCentipawnLoss=" + blackAvgCentipawnLoss +
                ", whiteAvgCentipawnLoss=" + whiteAvgCentipawnLoss +
                ", blackMistakes=" + Arrays.toString(blackMistakes) +
                ", whiteMistakes=" + Arrays.toString(whiteMistakes) +
                '}';
    }
}
