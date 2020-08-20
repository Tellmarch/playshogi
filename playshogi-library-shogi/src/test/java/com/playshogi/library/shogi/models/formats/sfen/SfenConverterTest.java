package com.playshogi.library.shogi.models.formats.sfen;

import com.playshogi.library.shogi.models.position.ShogiPosition;
import org.junit.Test;

import static org.junit.Assert.*;

public class SfenConverterTest {

    private String sfen = "lnsg3nl/2k2gr2/ppbp1p1pp/2p1P4/4s1S2/5B3/PPPP1P1PP/2S1GGR2/LN4KNL b 2Pp";
    private String sfenWithMoveCount = "lnsg3nl/2k2gr2/ppbp1p1pp/2p1P4/4s1S2/5B3/PPPP1P1PP/2S1GGR2/LN4KNL b 2Pp 1";

    @Test
    public void testFromSFENAndToSFEN() {
        ShogiPosition position = SfenConverter.fromSFEN(sfen);
        assertEquals(sfen, SfenConverter.toSFEN(position));
    }

    @Test
    public void testFromSFENAndToSFENWithMoveCount() {
        ShogiPosition position = SfenConverter.fromSFEN(sfen);
        assertEquals(sfenWithMoveCount, SfenConverter.toSFENWithMoveCount(position));
    }
}
