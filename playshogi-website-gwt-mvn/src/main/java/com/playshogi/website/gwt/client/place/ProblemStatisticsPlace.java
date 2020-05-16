package com.playshogi.website.gwt.client.place;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

public class ProblemStatisticsPlace extends Place {

    public ProblemStatisticsPlace() {
    }

    @Prefix("ProblemStatistics")
    public static class Tokenizer implements PlaceTokenizer<ProblemStatisticsPlace> {

        @Override
        public String getToken(final ProblemStatisticsPlace place) {
            return null;
        }

        @Override
        public ProblemStatisticsPlace getPlace(final String token) {
            return new ProblemStatisticsPlace();
        }

    }

}
