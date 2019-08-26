package com.playshogi.website.gwt.client.place;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

public class TsumePlace extends Place {
    private final String tsumeId;

    public TsumePlace() {
        this(null);
    }

    public TsumePlace(final String tsumeId) {
        this.tsumeId = tsumeId;
    }

    public String getTsumeId() {
        return tsumeId;
    }

    @Prefix("Tsume")
    public static class Tokenizer implements PlaceTokenizer<TsumePlace> {

        @Override
        public String getToken(final TsumePlace place) {
            return place.getTsumeId();
        }

        @Override
        public TsumePlace getPlace(final String token) {
            return new TsumePlace(token);
        }

    }
}
