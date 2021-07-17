package com.playshogi.website.gwt.client.place;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

public class ViewLessonPlace extends Place {

    private final String lessonId;
    private final String kifuId;
    private final int move;
    private final boolean inverted;

    public ViewLessonPlace(final String kifuId, final int move) {
        this(null, kifuId, move, false);
    }

    public ViewLessonPlace(final String lessonId, final String kifuId, final int move) {
        this(lessonId, kifuId, move, false);
    }

    private ViewLessonPlace(final String lessonId, final String kifuId, final int move, final boolean inverted) {
        this.lessonId = lessonId;
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

    public String getLessonId() {
        return lessonId;
    }

    public ViewLessonPlace withMove(final int move) {
        return new ViewLessonPlace(lessonId, kifuId, move, inverted);
    }

    @Prefix("ViewLesson")
    public static class Tokenizer implements PlaceTokenizer<ViewLessonPlace> {

        @Override
        public String getToken(final ViewLessonPlace place) {
            return place.getLessonId() + ":" + place.getKifuId() + ":" + place.getMove() + (place.isInverted() ?
                    ":i" : ":n");
        }

        @Override
        public ViewLessonPlace getPlace(final String token) {
            String[] split = token.split(":");
            if (split.length != 4) {
                throw new IllegalStateException("Error parsing the URL: " + token);
            }
            boolean inverted = "i".equals(split[3]);
            return new ViewLessonPlace(split[0], split[1], Integer.parseInt(split[2]), inverted);
        }

    }

}
