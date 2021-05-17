package com.playshogi.library.shogi.models.position;

import com.playshogi.library.shogi.models.formats.sfen.SfenConverter;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ShogiPositionTest {

    @Test
    public void fillGoteKomadaiWithMissingPieces() {
        ShogiPosition position = SfenConverter.fromSFEN("9/9/9/3G1G3/1P1P1P3/P1P1P1P1P/2G1K2P+p/2R4G1/S1B6 b -");
        position.fillGoteKomadaiWithMissingPieces();
        assertEquals("9/9/9/3G1G3/1P1P1P3/P1P1P1P1P/2G1K2P+p/2R4G1/S1B6 b rb3s4n4l8p", SfenConverter.toSFEN(position));
    }

    @Test
    public void opposite() {
        ShogiPosition position = SfenConverter.fromSFEN("9/9/9/3G1G3/1P1P1P3/P1P1P1P1P/2G1K2P+p/2R4G1/S1B6 b " +
                "rb3s4n4l8p");
        assertEquals("6b1s/1g4r2/+Pp2k1g2/p1p1p1p1p/3p1p1p1/3g1g3/9/9/9 w RB3S4N4L8P",
                SfenConverter.toSFEN(position.opposite()));
    }
}