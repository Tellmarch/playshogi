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

    public static final String DEFAULT_SFEN =
            SfenConverter.toSFEN(ShogiInitialPositionFactory.createInitialPosition());

    private final String sfen;
    private final String gameSetId;

    public OpeningsPlace() {
        this(DEFAULT_SFEN);
    }

    public OpeningsPlace(final String sfen) {
        this(sfen, null);
    }

    public OpeningsPlace(final String sfen, final String gameSetId) {
        this.sfen = sfen;
        this.gameSetId = gameSetId;
    }

    public String getSfen() {
        return sfen;
    }

    public String getGameSetId() {
        return gameSetId;
    }

    @Prefix("Openings")
    public static class Tokenizer implements PlaceTokenizer<OpeningsPlace> {

        @Override
        public String getToken(final OpeningsPlace place) {
            return place.getGameSetId() == null ? place.getSfen() : place.getGameSetId() + ":" + place.getSfen();
        }

        @Override
        public OpeningsPlace getPlace(final String token) {
            if (token.contains(":")) {
                String[] split = token.split(":", 2);
                return new OpeningsPlace(split[1], split[0]);
            } else {
                return new OpeningsPlace(token);
            }
        }
    }
}
