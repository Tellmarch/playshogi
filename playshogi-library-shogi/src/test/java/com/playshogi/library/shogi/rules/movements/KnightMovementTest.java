package com.playshogi.library.shogi.rules.movements;

import com.playshogi.library.shogi.models.formats.sfen.SfenConverter;
import com.playshogi.library.shogi.models.position.ShogiPosition;
import com.playshogi.library.shogi.models.position.Square;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class KnightMovementTest {

    @Test
    public void testGetPossibleMoves() {

        String sfen = "lnsg3nl/2k2gr2/ppbp1p1pp/2p1P4/4s1S2/5B3/PPPP1P1PP/2S1GGR2/LN4KNL b 2Pp";
        ShogiPosition position = SfenConverter.fromSFEN(sfen);

        List<Square> possibleMoves = new KnightMovement().getPossibleMoves(position.getShogiBoardState(), Square.of(1, 3));
        System.out.println(possibleMoves);
        assertEquals(1, possibleMoves.size());
    }
}