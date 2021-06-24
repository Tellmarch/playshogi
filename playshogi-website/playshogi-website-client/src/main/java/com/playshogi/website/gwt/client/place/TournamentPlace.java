package com.playshogi.website.gwt.client.place;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

public class TournamentPlace extends Place {

    public TournamentPlace() {
    }

    @Prefix("Tournament")
    public static class Tokenizer implements PlaceTokenizer<TournamentPlace> {

        @Override
        public String getToken(final TournamentPlace place) {
            return null;
        }

        @Override
        public TournamentPlace getPlace(final String token) {
            return new TournamentPlace();
        }

    }

}
