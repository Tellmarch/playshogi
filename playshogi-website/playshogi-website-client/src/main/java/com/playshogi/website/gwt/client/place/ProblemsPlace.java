package com.playshogi.website.gwt.client.place;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

public class ProblemsPlace extends Place {
    private final String collectionId;
    private final int problemIndex;
    private final String lessonId;

    public ProblemsPlace(final String collectionId, final int problemIndex) {
        this(collectionId, problemIndex, null);
    }

    public ProblemsPlace(final String collectionId, final int problemIndex, final String lessonId) {
        this.collectionId = collectionId;
        this.problemIndex = problemIndex;
        this.lessonId = lessonId;
    }

    public String getCollectionId() {
        return collectionId;
    }

    public int getProblemIndex() {
        return problemIndex;
    }

    public String getLessonId() {
        return lessonId;
    }

    @Prefix("Problems")
    public static class Tokenizer implements PlaceTokenizer<ProblemsPlace> {

        @Override
        public String getToken(final ProblemsPlace place) {
            return place.getCollectionId() + ":" + place.getProblemIndex() + ":" + place.getLessonId();
        }

        @Override
        public ProblemsPlace getPlace(final String token) {
            String[] split = token.split(":");

            if (split.length == 2) {
                return new ProblemsPlace(split[0], Integer.parseInt(split[1]));
            } else {
                return new ProblemsPlace(split[0], Integer.parseInt(split[1]), "null".equals(split[2]) ? null :
                        split[2]);
            }
        }

    }
}
