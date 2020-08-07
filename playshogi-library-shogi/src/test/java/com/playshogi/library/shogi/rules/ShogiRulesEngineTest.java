package com.playshogi.library.shogi.rules;

import com.playshogi.library.models.Square;
import com.playshogi.library.shogi.models.PieceType;
import com.playshogi.library.shogi.models.formats.sfen.SfenConverter;
import com.playshogi.library.shogi.models.moves.DropMove;
import com.playshogi.library.shogi.models.moves.ShogiMove;
import com.playshogi.library.shogi.models.position.ShogiPosition;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class ShogiRulesEngineTest {

    @Test
    public void isMoveLegalInPosition() {
        ShogiRulesEngine engine = new ShogiRulesEngine();
        String sfen = "lnsg3nl/2k2gr2/ppbp1p1pp/2p1P4/4s1S2/5B3/PPPP1P1PP/2S1GGR2/LN4KNL b 2Pp";
        ShogiPosition position = SfenConverter.fromSFEN(sfen);
        assertTrue(engine.isMoveLegalInPosition(position, new DropMove(true, PieceType.PAWN, Square.of(3, 6))));
        assertFalse(engine.isMoveLegalInPosition(position, new DropMove(true, PieceType.PAWN, Square.of(5, 6))));
        assertFalse(engine.isMoveLegalInPosition(position, new DropMove(true, PieceType.PAWN, Square.of(5, 3))));
    }

    @Test
    public void getAllPossibleDropMoves() {
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
    public void getAllPossibleNormalAndCaptureMoves() {
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
    public void isPositionCheck() {
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

    @Test
    public void isPositionCheckmate() {
        ShogiRulesEngine engine = new ShogiRulesEngine();
        String sfen = "lnsg3nl/2k2gr2/ppbp1p1pp/2p1P4/4s1S2/5B3/PPPP1P1PP/2S1GGR2/LN4KNL b 2Pp";
        String sfenCheckmate = "4k4/4G4/4P4/9/9/9/9/9/9 w 2r2b3g4s4n4l17p";
        String sfenOnlyCheck = "7k1/7G1/7P1/9/9/9/2b6/9/1K7 w -";
        String sfenOnlyCheck2 = "7pk/6+R2/9/5B3/9/9/9/9/1K7 w b";
        String sfenOnlyCheck3 = "k8/2+R6/9/9/9/9/9/9/1K7 w -";
        String sfenOnlyCheck4 = "k8/1B1+R5/9/9/9/9/9/9/1K7 w L";
        String sfenGoteCheckmate = "k8/9/9/9/9/9/1+r7/l8/K8 b L";
        String sfenGoteOnlyCheck = "k8/9/9/9/9/9/1+r7/lP7/K8 b L";
        ShogiPosition position = SfenConverter.fromSFEN(sfen);
        ShogiPosition position2 = SfenConverter.fromSFEN(sfenCheckmate);
        ShogiPosition position3 = SfenConverter.fromSFEN(sfenOnlyCheck);
        ShogiPosition position4 = SfenConverter.fromSFEN(sfenOnlyCheck2);
        ShogiPosition position5 = SfenConverter.fromSFEN(sfenOnlyCheck3);
        ShogiPosition position6 = SfenConverter.fromSFEN(sfenOnlyCheck4);
        ShogiPosition position7 = SfenConverter.fromSFEN(sfenGoteCheckmate);
        ShogiPosition position8 = SfenConverter.fromSFEN(sfenGoteOnlyCheck);
        System.out.println(position);
        System.out.println(position2);
        System.out.println(position3);
        System.out.println(position4);
        System.out.println(position5);
        System.out.println(position6);
        System.out.println(position7);
        System.out.println(position8);
        assertFalse(engine.isPositionCheckmate(position, true));
        assertTrue(engine.isPositionCheckmate(position2, true));
        assertFalse(engine.isPositionCheckmate(position2, false));
        assertFalse(engine.isPositionCheckmate(position3, true));
        assertFalse(engine.isPositionCheckmate(position4, true));
        assertFalse(engine.isPositionCheckmate(position5, true)); //stalemate
        assertFalse(engine.isPositionCheckmate(position6, true));
        assertTrue(engine.isPositionCheckmate(position7, false));
        assertFalse(engine.isPositionCheckmate(position8, false));
    }
}