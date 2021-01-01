package com.playshogi.library.shogi.models.formats.sfen;

import com.playshogi.library.shogi.models.position.ShogiPosition;
import com.playshogi.library.shogi.models.shogivariant.ShogiInitialPositionFactory;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

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

    @Test
    public void testFromSFENAndToSFENWithMoveCountInitial() {
        assertEquals("lnsgkgsnl/1r5b1/ppppppppp/9/9/9/PPPPPPPPP/1B5R1/LNSGKGSNL b - 1",
                SfenConverter.toSFENWithMoveCount(ShogiInitialPositionFactory.READ_ONLY_INITIAL_POSITION));
    }
}
