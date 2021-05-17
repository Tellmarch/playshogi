package com.playshogi.website.gwt.client.place;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

public class ViewKifuPlace extends Place {

    private final String kifuId;
    private final int move;
    private boolean inverted;

    public ViewKifuPlace(final String kifuId, final int move) {
        this(kifuId, move, false);
    }

    public ViewKifuPlace(final String kifuId, final int move, final boolean inverted) {
        this.kifuId = kifuId;
        this.move = move;
        this.inverted = inverted;
    }

    public String getKifuId() {
        return kifuId;
    }

    public int getMove() {
        return move;
    }

    public boolean isInverted() {
        return inverted;
    }

    @Prefix("ViewKifu")
    public static class Tokenizer implements PlaceTokenizer<ViewKifuPlace> {

        @Override
        public String getToken(final ViewKifuPlace place) {
            return place.getKifuId() + ":" + place.getMove() + (place.isInverted() ? ":i" : "");
        }

        @Override
        public ViewKifuPlace getPlace(final String token) {
            String[] split = token.split(":");
            boolean inverted = split.length == 3 && "i".equals(split[2]);
            return new ViewKifuPlace(split[0], Integer.parseInt(split[1]), inverted);
        }

    }

}
