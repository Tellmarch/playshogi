package com.playshogi.website.gwt.shared.models;

import java.io.Serializable;

public class ProblemOptions implements Serializable {
    private int numMoves = 0;
    private boolean random = true;
    private boolean includeTsume = true;
    private boolean includeTwoKings = false;
    private boolean includeHisshi = false;
    private boolean includeRealGame = false;
    private String previousProblemId = null;

    public ProblemOptions() {
    }

    public int getNumMoves() {
        return numMoves;
    }

    public void setNumMoves(final int numMoves) {
        this.numMoves = numMoves;
    }

    public boolean isRandom() {
        return random;
    }

    public void setRandom(final boolean random) {
        this.random = random;
    }

    public boolean isIncludeTsume() {
        return includeTsume;
    }

    public void setIncludeTsume(final boolean includeTsume) {
        this.includeTsume = includeTsume;
    }

    public boolean isIncludeTwoKings() {
        return includeTwoKings;
    }

    public void setIncludeTwoKings(final boolean includeTwoKings) {
        this.includeTwoKings = includeTwoKings;
    }

    public boolean isIncludeHisshi() {
        return includeHisshi;
    }

    public void setIncludeHisshi(final boolean includeHisshi) {
        this.includeHisshi = includeHisshi;
    }

    public boolean isIncludeRealGame() {
        return includeRealGame;
    }

    public void setIncludeRealGame(final boolean includeRealGame) {
        this.includeRealGame = includeRealGame;
    }

    public String getPreviousProblemId() {
        return previousProblemId;
    }

    public void setPreviousProblemId(final String previousProblemId) {
        this.previousProblemId = previousProblemId;
    }

    @Override
    public String toString() {
        return "ProblemOptions{" +
                "numMoves=" + numMoves +
                ", random=" + random +
                ", includeTsume=" + includeTsume +
                ", includeTwoKings=" + includeTwoKings +
                ", includeHisshi=" + includeHisshi +
                ", includeRealGame=" + includeRealGame +
                ", previousProblemId='" + previousProblemId + '\'' +
                '}';
    }
}
