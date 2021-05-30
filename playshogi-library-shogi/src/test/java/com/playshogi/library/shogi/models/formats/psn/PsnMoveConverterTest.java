package com.playshogi.library.shogi.models.formats.psn;

import com.playshogi.library.shogi.models.Piece;
import com.playshogi.library.shogi.models.PieceType;
import com.playshogi.library.shogi.models.Player;
import com.playshogi.library.shogi.models.moves.CaptureMove;
import com.playshogi.library.shogi.models.moves.DropMove;
import com.playshogi.library.shogi.models.moves.NormalMove;
import com.playshogi.library.shogi.models.position.Square;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PsnMoveConverterTest {

    @Test
    public void toPsnStringShort() {
        assertEquals("P*2f", PsnMoveConverter.toPsnStringShort(new DropMove(Player.BLACK, PieceType.PAWN, Square.of(2
                , 6)), null));
        assertEquals("Gx5g", PsnMoveConverter.toPsnStringShort(new CaptureMove(Piece.SENTE_GOLD, Square.of(4, 8),
                Square.of(5, 7), Piece.GOTE_PAWN), null));
        assertEquals("Gx", PsnMoveConverter.toPsnStringShort(new CaptureMove(Piece.SENTE_GOLD, Square.of(4, 8),
                Square.of(5, 7), Piece.GOTE_PAWN), new DropMove(Player.WHITE, PieceType.PAWN, Square.of(5, 7))));
        assertEquals("G-5g", PsnMoveConverter.toPsnStringShort(new NormalMove(Piece.SENTE_GOLD, Square.of(4, 8),
                Square.of(5, 7)), null));
        assertEquals("S-5c+", PsnMoveConverter.toPsnStringShort(new NormalMove(Piece.SENTE_SILVER, Square.of(4, 4),
                Square.of(5, 3), true), null));
    }

}