package com.playshogi.library.shogi.rules;

import com.playshogi.library.models.Square;
import com.playshogi.library.shogi.models.PieceType;
import com.playshogi.library.shogi.models.formats.sfen.SfenConverter;
import com.playshogi.library.shogi.models.moves.DropMove;
import com.playshogi.library.shogi.models.moves.ShogiMove;
import com.playshogi.library.shogi.models.position.ShogiPosition;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ShogiRulesEngineTest {

    @Test
    void isMoveLegalInPosition() {
        ShogiRulesEngine engine = new ShogiRulesEngine();
        String sfen = "lnsg3nl/2k2gr2/ppbp1p1pp/2p1P4/4s1S2/5B3/PPPP1P1PP/2S1GGR2/LN4KNL b 2Pp";
        ShogiPosition position = SfenConverter.fromSFEN(sfen);
        assertTrue(engine.isMoveLegalInPosition(position, new DropMove(true, PieceType.PAWN, Square.of(3, 6))));
        assertFalse(engine.isMoveLegalInPosition(position, new DropMove(true, PieceType.PAWN, Square.of(5, 6))));
        assertFalse(engine.isMoveLegalInPosition(position, new DropMove(true, PieceType.PAWN, Square.of(5, 3))));
    }

    @Test
    void getAllPossibleDropMoves() {
        ShogiRulesEngine engine = new ShogiRulesEngine();
        String sfen = "lnsg3nl/2k2gr2/ppbp1p1pp/2p1P4/4s1S2/5B3/PPPP1P1PP/2S1GGR2/LN4KNL b 2Pp";
        List<ShogiMove> allPossibleDropMoves = engine.getAllPossibleDropMoves(SfenConverter.fromSFEN(sfen), true);
        List<ShogiMove> allPossibleDropMoves2 = engine.getAllPossibleDropMoves(SfenConverter.fromSFEN(sfen), false);
        System.out.println(allPossibleDropMoves);
        System.out.println(allPossibleDropMoves2);
        assertEquals(allPossibleDropMoves2.size(),10);
        assertEquals(allPossibleDropMoves.size(),4);
    }

    @Test
    void getAllPossibleNormalAndCaptureMoves() {
        ShogiRulesEngine engine = new ShogiRulesEngine();
        String sfen = "lnsg3nl/2k2gr2/ppbp1p1pp/2p1P4/4s1S2/5B3/PPPP1P1PP/2S1GGR2/LN4KNL b 2Pp";
        List<ShogiMove> allPossibleNormalAndCaptureMoves = engine.getAllPossibleNormalAndCaptureMoves(SfenConverter.fromSFEN(sfen), true);
        List<ShogiMove> allPossibleNormalAndCaptureMoves2 = engine.getAllPossibleNormalAndCaptureMoves(SfenConverter.fromSFEN(sfen), false);
        System.out.println(allPossibleNormalAndCaptureMoves);
        System.out.println(allPossibleNormalAndCaptureMoves2);
        assertEquals(allPossibleNormalAndCaptureMoves.size(),34);
        assertEquals(allPossibleNormalAndCaptureMoves2.size(),38);
    }

    @Test
    void isPositionCheck() {
        ShogiRulesEngine engine = new ShogiRulesEngine();
        String sfen = "lnsg3nl/2k2gr2/ppbp1p1pp/2p1P4/4s1S2/5B3/PPPP1P1PP/2S1GGR2/LN4KNL b 2Pp";
        String sfenCheckmate = "4k4/4G4/4P4/9/9/9/9/9/9 w 2r2b3g4s4n4l17p";
        ShogiPosition position = SfenConverter.fromSFEN(sfen);
        ShogiPosition position2 = SfenConverter.fromSFEN(sfenCheckmate);
        System.out.println(position);
        System.out.println(position2);
        assertFalse(engine.isPositionCheck(position, true));
        assertTrue(engine.isPositionCheck(position2, true));

    }
}