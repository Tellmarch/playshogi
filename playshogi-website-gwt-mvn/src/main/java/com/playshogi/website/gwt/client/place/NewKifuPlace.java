package com.playshogi.website.gwt.client.place;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

public class NewKifuPlace extends Place {

    public NewKifuPlace() {
    }

    @Prefix("NewKifu")
    public static class Tokenizer implements PlaceTokenizer<NewKifuPlace> {

        @Override
        public String getToken(final NewKifuPlace place) {
            return null;
        }

        @Override
        public NewKifuPlace getPlace(final String token) {
            return new NewKifuPlace();
        }

    }

}
