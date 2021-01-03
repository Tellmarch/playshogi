package com.playshogi.website.gwt.client.place;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

public class ProblemPlace extends Place {
    private final String kifuId;

    public ProblemPlace(final String kifuId) {
        this.kifuId = kifuId;
    }

    public String getKifuId() {
        return kifuId;
    }

    @Prefix("Problem")
    public static class Tokenizer implements PlaceTokenizer<ProblemPlace> {

        @Override
        public String getToken(final ProblemPlace place) {
            return place.getKifuId();
        }

        @Override
        public ProblemPlace getPlace(final String token) {
            return new ProblemPlace(token);
        }

    }
}
