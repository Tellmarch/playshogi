package com.playshogi.website.gwt.client.place;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;
import com.playshogi.library.shogi.models.formats.sfen.SfenConverter;
import com.playshogi.library.shogi.models.shogivariant.ShogiInitialPositionFactory;

/**
 * A board with free editing, that can be shared with a unique URL
 */
public class OpeningsPlace extends Place {

    private static final String DEFAULT_SFEN =
            SfenConverter.toSFEN(ShogiInitialPositionFactory.createInitialPosition());

    private final String sfen;

    public OpeningsPlace() {
        this(DEFAULT_SFEN);
    }

    public OpeningsPlace(final String token) {
        this.sfen = token;
    }

    public String getSfen() {
        return sfen;
    }

    @Prefix("Openings")
    public static class Tokenizer implements PlaceTokenizer<OpeningsPlace> {

        @Override
        public String getToken(final OpeningsPlace place) {
            return place.getSfen();
        }

        @Override
        public OpeningsPlace getPlace(final String token) {
            return new OpeningsPlace(token);
        }

    }
}
