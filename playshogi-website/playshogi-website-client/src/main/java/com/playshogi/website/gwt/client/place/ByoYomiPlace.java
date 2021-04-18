package com.playshogi.website.gwt.client.place;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

public class ByoYomiPlace extends Place {

    private int maxFailures = 3;
    private int raiseDifficultyEveryN = 5;
    private int maxTimeSec = 300;
    private boolean minusForBadAnswers = false;
    private int timePerMove = 0;
    private int numberOfMoves = 0;

    public ByoYomiPlace() {
    }

    public ByoYomiPlace(int maxFailures, int raiseDifficultyEveryN, int maxTimeSec, boolean minusForBadAnswers,
                        int timePerMove, int numberOfMoves) {
        this.maxFailures = maxFailures;
        this.raiseDifficultyEveryN = raiseDifficultyEveryN;
        this.maxTimeSec = maxTimeSec;
        this.minusForBadAnswers = minusForBadAnswers;
        this.timePerMove = timePerMove;
        this.numberOfMoves = numberOfMoves;
    }

    public int getMaxFailures() {
        return maxFailures;
    }

    public int getRaiseDifficultyEveryN() {
        return raiseDifficultyEveryN;
    }

    public int getMaxTimeSec() {
        return maxTimeSec;
    }

    public boolean isMinusForBadAnswers() {
        return minusForBadAnswers;
    }

    public int getTimePerMove() {
        return timePerMove;
    }

    public boolean isDefault() {
        return maxFailures == 3 && raiseDifficultyEveryN == 5 && maxTimeSec == 300 && !minusForBadAnswers && timePerMove == 0 && numberOfMoves == 0;
    }

    public int getNumberOfMoves() {
        return numberOfMoves;
    }

    @Prefix("ByoYomi")
    public static class Tokenizer implements PlaceTokenizer<ByoYomiPlace> {

        @Override
        public String getToken(final ByoYomiPlace place) {
            return null;
        }

        @Override
        public ByoYomiPlace getPlace(final String token) {
            return new ByoYomiPlace();
        }

    }
}
