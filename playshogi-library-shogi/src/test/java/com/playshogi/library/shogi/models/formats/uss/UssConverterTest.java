package com.playshogi.library.shogi.models.formats.uss;

import com.playshogi.library.shogi.models.formats.sfen.SfenConverter;
import com.playshogi.library.shogi.models.position.ShogiPosition;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class UssConverterTest {

    private static final String pos1 = "White in hand: R2 G4 S N3 L4 P17 \n" +
            "  9  8  7  6  5  4  3  2  1\n" +
            "+---------------------------+\n" +
            "| *  *  *  *  * wB+wK  *  * |a\n" +
            "| *  *  *  *  *  *  *  * bB+|b\n" +
            "| *  *  *  *  * bP+wS  *  * |c\n" +
            "| *  *  *  *  *  *  *  *  * |d\n" +
            "| *  *  *  *  *  *  *  *  * |e\n" +
            "| *  *  *  *  *  *  *  *  * |f\n" +
            "| *  *  *  *  *  *  *  *  * |g\n" +
            "| *  *  *  *  *  *  *  *  * |h\n" +
            "| *  *  *  *  *  *  *  *  * |i\n" +
            "+---------------------------+\n" +
            "Black in hand: S2 N ";

    private static final String pos2 = "  9  8  7  6  5  4  3  2  1\n" +
            "+---------------------------+\n" +
            "| *  *  *  *  *  *  * wN wK |a\n" +
            "| *  *  *  *  *  *  *  * wL |b\n" +
            "| *  *  *  *  *  * bR  *  * |c\n" +
            "| *  *  *  *  * bB  *  *  * |d\n" +
            "| *  *  *  *  *  *  *  *  * |e\n" +
            "| *  *  *  *  *  *  *  *  * |f\n" +
            "| *  *  *  *  *  *  *  *  * |g\n" +
            "| *  *  *  *  *  *  *  *  * |h\n" +
            "| *  *  *  *  *  *  *  *  * |i\n" +
            "+---------------------------+\n" +
            "Black in hand: nothing";

    @Test
    public void fromUSS() {
        ShogiPosition pos = UssConverter.fromUSS(pos1);
        assertEquals("5+bk2/8+B/5+Ps2/9/9/9/9/9/9 b 2SN2r4gs3n4l17p", SfenConverter.toSFEN(pos));
    }

    @Test
    public void fromUSS2() {
        ShogiPosition pos = UssConverter.fromUSS(pos2);
        assertEquals("7nk/8l/6R2/5B3/9/9/9/9/9 b -", SfenConverter.toSFEN(pos));
    }

}