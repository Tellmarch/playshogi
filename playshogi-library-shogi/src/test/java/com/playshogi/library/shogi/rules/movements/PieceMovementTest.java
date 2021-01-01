package com.playshogi.library.shogi.rules.movements;

import com.playshogi.library.shogi.models.formats.sfen.SfenConverter;
import com.playshogi.library.shogi.models.position.ShogiBoardState;
import com.playshogi.library.shogi.models.position.ShogiPosition;
import com.playshogi.library.shogi.models.position.Square;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(Parameterized.class)
public class PieceMovementTest {

    private PieceMovement pieceMovement;

    public PieceMovementTest(PieceMovement pieceMovement) {
        this.pieceMovement = pieceMovement;
    }


    @Parameterized.Parameters
    public static Collection<Object[]> primeNumbers() {
        return Arrays.asList(new Object[][] {
                { new PawnMovement() },
                { new LanceMovement() },
                { new KnightMovement() },
                { new SilverMovement() },
                { new GoldMovement() },
                { new BishopMovement() },
                { new RookMovement() },
                { new PromotedBishopMovement() },
                { new PromotedRookMovement() }
        });
    }

    @Test
    public void isMoveDxDyValid() {
        String sfen = "lnsg3nl/2k2gr2/ppbp1p1pp/2p1P4/4s1S2/5B3/PPPP1P1PP/2S1GGR2/LN4KNL b 2Pp";
        ShogiPosition position = SfenConverter.fromSFEN(sfen);

        ShogiBoardState boardState = position.getShogiBoardState();

        for (Square from : position.getAllSquares()) {
            List<Square> possibleDest = pieceMovement.getPossibleMoves(boardState, from);
            List<Square> invalidDest = position.getAllSquares();
            invalidDest.removeAll(possibleDest);
            for(Square to: possibleDest) {
                assertTrue("Move from " + from + " to " + to + " is valid", pieceMovement.isMoveDxDyValid(boardState,from, to));
            }
            for(Square to: invalidDest) {
                // isMoveDxDyValid doesn't check that there is no sente piece at to
                if (!(position.hasBlackPieceAt(to))) {
                    assertFalse("Move from " + from + " to " + to + " is invalid",
                            pieceMovement.isMoveDxDyValid(boardState, from, to));
                }
            }
        }
    }
}