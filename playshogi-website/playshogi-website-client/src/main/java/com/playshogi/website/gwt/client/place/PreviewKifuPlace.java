package com.playshogi.website.gwt.client.place;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

public class PreviewKifuPlace extends Place {

    private final String kifuUsf;
    private final int move;

    public PreviewKifuPlace(final String kifuUsf, final int move) {
        this.kifuUsf = kifuUsf;
        this.move = move;
    }

    public String getKifuUsf() {
        return kifuUsf;
    }

    public int getMove() {
        return move;
    }

    @Prefix("PreviewKifu")
    public static class Tokenizer implements PlaceTokenizer<PreviewKifuPlace> {

        @Override
        public String getToken(final PreviewKifuPlace place) {
            return "";
        }

        @Override
        public PreviewKifuPlace getPlace(final String token) {
            return new PreviewKifuPlace("USF:1.0\n^*:", 0);
        }

    }

}
