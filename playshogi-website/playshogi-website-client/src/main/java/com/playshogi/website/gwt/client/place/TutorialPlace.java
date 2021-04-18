package com.playshogi.website.gwt.client.place;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

public class TutorialPlace extends Place {
    private final int chapter;

    public TutorialPlace() {
        this(1);
    }

    public TutorialPlace(final int chapter) {
        this.chapter = chapter;
    }

    public int getChapter() {
        return chapter;
    }

    @Prefix("Tutorial")
    public static class Tokenizer implements PlaceTokenizer<TutorialPlace> {

        @Override
        public String getToken(final TutorialPlace place) {
            return String.valueOf(place.getChapter());
        }

        @Override
        public TutorialPlace getPlace(final String token) {
            return new TutorialPlace(Integer.parseInt(token));
        }

    }
}
