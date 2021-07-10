package com.playshogi.library.shogi.models.shogivariant;

import com.playshogi.library.shogi.models.formats.sfen.SfenConverter;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ShogiInitialPositionFactoryTest {

    @Test
    public void createInitialPosition() {
        assertEquals("lnsgkgsnl/1r5b1/ppppppppp/9/9/9/PPPPPPPPP/1B5R1/LNSGKGSNL b - 1",
                SfenConverter.toSFENWithMoveCount(ShogiInitialPositionFactory.createInitialPosition()));
        assertEquals("lnsgkgsnl/1r5b1/ppppppppp/9/9/9/PPPPPPPPP/1B5R1/LNSGKGSNL b - 1",
                SfenConverter.toSFENWithMoveCount(ShogiInitialPositionFactory.createInitialPosition(Handicap.EVEN)));
        assertEquals("lnsgkgsnl/1r5b1/ppppppppp/9/9/9/PPPPPPPPP/1B5R1/LNSGKGSNL b - 1",
                SfenConverter.toSFENWithMoveCount(ShogiInitialPositionFactory.createInitialPosition(Handicap.SENTE)));
        assertEquals("lnsgkgsnl/9/ppppppppp/9/9/9/PPPPPPPPP/1B5R1/LNSGKGSNL w - 1",
                SfenConverter.toSFENWithMoveCount(ShogiInitialPositionFactory.createInitialPosition(Handicap.TWO_PIECES)));
        assertEquals("1nsgkgsn1/9/ppppppppp/9/9/9/PPPPPPPPP/1B5R1/LNSGKGSNL w - 1",
                SfenConverter.toSFENWithMoveCount(ShogiInitialPositionFactory.createInitialPosition(Handicap.FOUR_PIECES)));
        assertEquals("2sgkgs2/9/ppppppppp/9/9/9/PPPPPPPPP/1B5R1/LNSGKGSNL w - 1",
                SfenConverter.toSFENWithMoveCount(ShogiInitialPositionFactory.createInitialPosition(Handicap.SIX_PIECES)));
        assertEquals("3gkg3/9/ppppppppp/9/9/9/PPPPPPPPP/1B5R1/LNSGKGSNL w - 1",
                SfenConverter.toSFENWithMoveCount(ShogiInitialPositionFactory.createInitialPosition(Handicap.EIGHT_PIECES)));
        assertEquals("3gk4/9/ppppppppp/9/9/9/PPPPPPPPP/1B5R1/LNSGKGSNL w - 1",
                SfenConverter.toSFENWithMoveCount(ShogiInitialPositionFactory.createInitialPosition(Handicap.NINE_PIECES)));
        assertEquals("4k4/9/ppppppppp/9/9/9/PPPPPPPPP/1B5R1/LNSGKGSNL w - 1",
                SfenConverter.toSFENWithMoveCount(ShogiInitialPositionFactory.createInitialPosition(Handicap.TEN_PIECES)));
        assertEquals("4k4/9/9/9/9/9/PPPPPPPPP/1B5R1/LNSGKGSNL w - 1",
                SfenConverter.toSFENWithMoveCount(ShogiInitialPositionFactory.createInitialPosition(Handicap.NAKED_KING)));
        assertEquals("4k4/9/9/9/9/9/PPPPPPPPP/1B5R1/LNSGKGSNL w 3p 1",
                SfenConverter.toSFENWithMoveCount(ShogiInitialPositionFactory.createInitialPosition(Handicap.THREE_PAWNS)));
        assertEquals("lnsgkgsnl/7b1/ppppppppp/9/9/9/PPPPPPPPP/1B5R1/LNSGKGSNL w - 1",
                SfenConverter.toSFENWithMoveCount(ShogiInitialPositionFactory.createInitialPosition(Handicap.ROOK)));
        assertEquals("lnsgkgsn1/7b1/ppppppppp/9/9/9/PPPPPPPPP/1B5R1/LNSGKGSNL w - 1",
                SfenConverter.toSFENWithMoveCount(ShogiInitialPositionFactory.createInitialPosition(Handicap.ROOK_LANCE)));
        assertEquals("lnsgkgsn1/1r5b1/ppppppppp/9/9/9/PPPPPPPPP/1B5R1/LNSGKGSNL w - 1",
                SfenConverter.toSFENWithMoveCount(ShogiInitialPositionFactory.createInitialPosition(Handicap.LANCE)));
        assertEquals("lnsgkgsnl/1r7/ppppppppp/9/9/9/PPPPPPPPP/1B5R1/LNSGKGSNL w - 1",
                SfenConverter.toSFENWithMoveCount(ShogiInitialPositionFactory.createInitialPosition(Handicap.BISHOP)));
    }
}