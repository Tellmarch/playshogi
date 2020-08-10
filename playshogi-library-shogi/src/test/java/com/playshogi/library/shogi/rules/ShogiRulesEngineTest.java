package com.playshogi.library.shogi.rules;

import com.playshogi.library.models.Square;
import com.playshogi.library.shogi.models.PieceType;
import com.playshogi.library.shogi.models.formats.sfen.SfenConverter;
import com.playshogi.library.shogi.models.moves.DropMove;
import com.playshogi.library.shogi.models.moves.ShogiMove;
import com.playshogi.library.shogi.models.position.ShogiPosition;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class ShogiRulesEngineTest {

    private ShogiRulesEngine engine;

    @Before
    public void setUp() {
        engine = new ShogiRulesEngine();
    }

    @Test
    public void isPawnDropLegalInPosition() {
        String sfen = "lnsg1gsnl/7b1/prppppppp/kp7/9/2P6/NPBPPPPPP/7R1/L1SGKGSNL b P";
        ShogiPosition position = SfenConverter.fromSFEN(sfen);
        System.out.println(position);
        assertFalse("pawn drop checkmate", engine.isMoveLegalInPosition(position, new DropMove(true, PieceType.PAWN, Square.of(9, 5))));
    }

    @Test
    public void isMoveLegalInPosition() {
        String sfen = "lnsg3nl/2k2gr2/ppbp1p1pp/2p1P4/4s1S2/5B3/PPPP1P1PP/2S1GGR2/LN4KNL b 2Pp";
        ShogiPosition position = SfenConverter.fromSFEN(sfen);
        System.out.println(position);
        assertTrue(engine.isMoveLegalInPosition(position, new DropMove(true, PieceType.PAWN, Square.of(3, 6))));
        assertFalse(engine.isMoveLegalInPosition(position, new DropMove(true, PieceType.PAWN, Square.of(5, 6))));
        assertFalse(engine.isMoveLegalInPosition(position, new DropMove(true, PieceType.PAWN, Square.of(5, 3))));
    }

    @Test
    public void getAllPossibleDropMovesForSente() {
        String sfen = "lnsg3nl/2k2gr2/ppbp1p1pp/2p1P4/4s1S2/5B3/PPPP1P1PP/2S1GGR2/LN4KNL b 2Pp";
        ShogiPosition position = SfenConverter.fromSFEN(sfen);
        System.out.println(position);
        List<ShogiMove> allPossibleDropMoves = engine.getAllPossibleDropMoves(position, true);
        System.out.println("Drops: " + allPossibleDropMoves);
        assertEquals(4, allPossibleDropMoves.size());
    }

    @Test
    public void getAllPossibleDropMovesForGote() {
        String sfen = "lnsg3nl/2k2gr2/ppbp1p1pp/2p1P4/4s1S2/5B3/PPPP1P1PP/2S1GGR2/LN4KNL b 2Pp";
        ShogiPosition position = SfenConverter.fromSFEN(sfen);
        System.out.println(position);
        List<ShogiMove> allPossibleDropMoves = engine.getAllPossibleDropMoves(position, false);
        System.out.println("Drops: " + allPossibleDropMoves);
        assertEquals(10, allPossibleDropMoves.size());
    }

    @Test
    public void getAllPossibleNormalAndCaptureMoves() {
        String sfen = "lnsg3nl/2k2gr2/ppbp1p1pp/2p1P4/4s1S2/5B3/PPPP1P1PP/2S1GGR2/LN4KNL b 2Pp";
        ShogiPosition position = SfenConverter.fromSFEN(sfen);
        System.out.println(position);
        List<ShogiMove> allPossibleNormalAndCaptureMoves = engine.getAllPossibleNormalAndCaptureMoves(position, true);
        System.out.println("Moves: " + allPossibleNormalAndCaptureMoves);
        assertEquals(34, allPossibleNormalAndCaptureMoves.size());
    }

    @Test
    public void getAllPossibleNormalAndCaptureMoves2() {
        String sfen = "lnsg3nl/2k2gr2/ppbp1p1pp/2p1P4/4s1S2/5B3/PPPP1P1PP/2S1GGR2/LN4KNL b 2Pp";
        ShogiPosition position = SfenConverter.fromSFEN(sfen);
        System.out.println(position);
        List<ShogiMove> allPossibleNormalAndCaptureMoves = engine.getAllPossibleNormalAndCaptureMoves(position, false);
        System.out.println(allPossibleNormalAndCaptureMoves);
        assertEquals(38, allPossibleNormalAndCaptureMoves.size());
    }

    @Test
    public void isPositionCheck() {
        String sfen = "lnsg3nl/2k2gr2/ppbp1p1pp/2p1P4/4s1S2/5B3/PPPP1P1PP/2S1GGR2/LN4KNL b 2Pp";
        ShogiPosition position = SfenConverter.fromSFEN(sfen);
        System.out.println(position);
        assertFalse(engine.isPositionCheck(position, true));
    }

    @Test
    public void isPositionCheckIfCheckmate() {
        String sfenCheckmate = "4k4/4G4/4P4/9/9/9/9/9/9 w 2r2b3g4s4n4l17p";
        ShogiPosition position = SfenConverter.fromSFEN(sfenCheckmate);
        System.out.println(position);
        assertTrue(engine.isPositionCheck(position, true));
    }

    @Test
    public void isPositionCheckmate() {
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
