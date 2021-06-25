package com.playshogi.website.gwt.client.place;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

public class ViewLessonPlace extends Place {

    private final String kifuId;
    private final int move;
    private final boolean inverted;

    public ViewLessonPlace(final String kifuId, final int move) {
        this(kifuId, move, false);
    }

    public ViewLessonPlace(final String kifuId, final int move, final boolean inverted) {
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

    @Prefix("ViewLesson")
    public static class Tokenizer implements PlaceTokenizer<ViewLessonPlace> {

        @Override
        public String getToken(final ViewLessonPlace place) {
            return place.getKifuId() + ":" + place.getMove() + (place.isInverted() ? ":i" : "");
        }

        @Override
        public ViewLessonPlace getPlace(final String token) {
            String[] split = token.split(":");
            boolean inverted = split.length == 3 && "i".equals(split[2]);
            return new ViewLessonPlace(split[0], Integer.parseInt(split[1]), inverted);
        }

    }

}
