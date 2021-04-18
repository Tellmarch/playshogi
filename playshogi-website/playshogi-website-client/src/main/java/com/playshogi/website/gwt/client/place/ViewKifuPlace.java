package com.playshogi.website.gwt.client.place;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

public class ViewKifuPlace extends Place {

    private final String kifuId;
    private final int move;

    public ViewKifuPlace(final String kifuId, final int move) {
        this.kifuId = kifuId;
        this.move = move;
    }

    public String getKifuId() {
        return kifuId;
    }

    public int getMove() {
        return move;
    }

    @Prefix("ViewKifu")
    public static class Tokenizer implements PlaceTokenizer<ViewKifuPlace> {

        @Override
        public String getToken(final ViewKifuPlace place) {
            return place.getKifuId() + ":" + place.getMove();
        }

        @Override
        public ViewKifuPlace getPlace(final String token) {
            String[] split = token.split(":");
            return new ViewKifuPlace(split[0], Integer.parseInt(split[1]));
        }

    }

}
