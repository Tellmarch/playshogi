package com.playshogi.website.gwt.client.place;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;
import com.playshogi.website.gwt.shared.models.KifuDetails.KifuType;

public class KifuEditorPlace extends Place {

    private final String kifuId;
    private final KifuType type;
    private final String collectionId;

    public KifuEditorPlace() {
        this(null, null, null);
    }

    public KifuEditorPlace(final String kifuId, final KifuType type, final String collectionId) {
        this.kifuId = kifuId;
        this.type = type;
        this.collectionId = collectionId;
    }

    public String getKifuId() {
        return kifuId;
    }

    public KifuType getType() {
        return type;
    }

    public String getCollectionId() {
        return collectionId;
    }

    @Prefix("KifuEditor")
    public static class Tokenizer implements PlaceTokenizer<KifuEditorPlace> {

        @Override
        public String getToken(final KifuEditorPlace place) {
            return place.kifuId + ":" + place.getType() + ":" + place.getCollectionId();
        }

        @Override
        public KifuEditorPlace getPlace(final String token) {
            String[] split = token.split(":");
            if (split.length < 3) {
                return new KifuEditorPlace();
            }
            String kifuId = "null".equals(split[0]) ? null : split[0];
            KifuType kifuType = "null".equals(split[1]) ? null : KifuType.valueOf(split[1]);
            String collectionId = "null".equals(split[2]) ? null : split[2];
            return new KifuEditorPlace(kifuId, kifuType, collectionId);
        }

    }

}
