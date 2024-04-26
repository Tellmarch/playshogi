package com.playshogi.website.gwt.client.place;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

public class ProblemsPlace extends Place {
    private final String collectionId;
    private final int problemIndex;
    private final String lessonId;
    private final boolean practiceFromGameCollection;

    public ProblemsPlace(final String collectionId, final int problemIndex) {
        this(collectionId, problemIndex, null, false);
    }

    public ProblemsPlace(final String collectionId, final int problemIndex, final String lessonId) {
        this(collectionId, problemIndex, lessonId, false);
    }

    public ProblemsPlace(final String collectionId, final int problemIndex, boolean practiceFromGameCollection) {
        this(collectionId, problemIndex, null, practiceFromGameCollection);
    }

    public ProblemsPlace(final String collectionId, final int problemIndex, final String lessonId,
                         boolean practiceFromGameCollection) {
        this.collectionId = collectionId;
        this.problemIndex = problemIndex;
        this.lessonId = lessonId;
        this.practiceFromGameCollection = practiceFromGameCollection;
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

    public boolean isPracticeFromGameCollection() {
        return practiceFromGameCollection;
    }

    @Prefix("Problems")
    public static class Tokenizer implements PlaceTokenizer<ProblemsPlace> {

        @Override
        public String getToken(final ProblemsPlace place) {
            return place.getCollectionId() + ":" + place.getProblemIndex() + ":" + place.getLessonId() + ":" + place.isPracticeFromGameCollection();
        }

        @Override
        public ProblemsPlace getPlace(final String token) {
            String[] split = token.split(":");

            if (split.length == 2) {
                return new ProblemsPlace(split[0], Integer.parseInt(split[1]));
            } else if (split.length == 3) {
                return new ProblemsPlace(split[0], Integer.parseInt(split[1]), "null".equals(split[2]) ? null :
                        split[2], false);
            } else {
                return new ProblemsPlace(split[0], Integer.parseInt(split[1]), "null".equals(split[2]) ? null :
                        split[2], Boolean.parseBoolean(split[3]));
            }
        }

    }
}
