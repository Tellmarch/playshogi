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

    public ByoYomiPlace() {
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
