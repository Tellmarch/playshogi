package com.playshogi.library.shogi.tsumesolver;

import com.playshogi.library.shogi.models.formats.kif.KifFormat;
import com.playshogi.library.shogi.models.formats.sfen.SfenConverter;
import com.playshogi.library.shogi.models.formats.usf.UsfFormat;
import com.playshogi.library.shogi.models.moves.ShogiMove;
import com.playshogi.library.shogi.models.record.GameTree;
import com.playshogi.library.shogi.models.shogivariant.ShogiInitialPositionFactory;
import org.junit.Ignore;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

@Ignore
public class TsumeSolverTest {

    @Test
    public void getAllPossibleChecks() {
        TsumeSolver tsumeSolver = new TsumeSolver();
        System.out.println(tsumeSolver.getAllPossibleChecks(SfenConverter.fromSFEN("4k4/9/4P4/9/9/9/9/9/9 b G2r2b3g4s4n4l17p")));
    }

    @Test
    public void getAllPossibleAnswers() {
        TsumeSolver tsumeSolver = new TsumeSolver();
        System.out.println(tsumeSolver.getAllPossibleAnswers(SfenConverter.fromSFEN("4k4/6G2/5+P3/9/B8/9/9/9/9 w 2rb3g4s4n4l17p")));
    }

    @Test
    public void tsumeInOne() {
        TsumeSolver tsumeSolver = new TsumeSolver();

        List<List<ShogiMove>> matingLines = tsumeSolver.allMatingLines(SfenConverter.fromSFEN("k8/9/GG7/9/9/9/9/9/9 b " +
                "2r2b2g4s4n4l18p"), 1);
        System.out.println(matingLines);
        assertEquals(4, matingLines.size());
        assertEquals("[[8c9b], [8c8b], [9c9b], [9c8b]]", matingLines.toString());
    }

    @Test
    public void tsumeInThree() {
        TsumeSolver tsumeSolver = new TsumeSolver();

        List<List<ShogiMove>> matingLines = tsumeSolver.allMatingLines(SfenConverter.fromSFEN("9/k8/9/P8/9/9/9/9/9 b 2G2r2b2g4s4n4l17p"), 3);
        System.out.println(matingLines);
        assertEquals(3, matingLines.size());
        assertEquals("[[G*9c, 9b9a, G*8b], [G*9c, 9b9a, G*9b], [G*9c, 9b8a, G*8b]]", matingLines.toString());
    }

    @Test
    public void tsumeInThreeTwo() {
        TsumeSolver tsumeSolver = new TsumeSolver();

        List<List<ShogiMove>> matingLines = tsumeSolver.allMatingLines(SfenConverter.fromSFEN("7nl/6+RG1/8k/6Ngp/9/9/9/9/9 b r2b2g4s2n3l17p"), 3);
        System.out.println(matingLines);
        assertEquals(1, matingLines.size());
        assertEquals("[[2b1b, 1a1b, 3b2b]]", matingLines.toString());
    }

    @Test
    public void tsumeInThreeTwoDepth5() {
        TsumeSolver tsumeSolver = new TsumeSolver();

        List<List<ShogiMove>> matingLines = tsumeSolver.allMatingLines(SfenConverter.fromSFEN("7nl/6+RG1/8k/6Ngp/9/9/9/9/9 b r2b2g4s2n3l17p"), 5);
        System.out.println(matingLines);
        assertEquals(1, matingLines.size());
        assertEquals("[[2b1b, 1a1b, 3b2b]]", matingLines.toString());
    }

    @Test
    public void tsumeInFive() {
        TsumeSolver tsumeSolver = new TsumeSolver();

        List<List<ShogiMove>> matingLines = tsumeSolver.allMatingLines(SfenConverter.fromSFEN("7nl/6bSk/6b2/7R1/9/9/9/9/9 b 2Gr2g3s3n3l18p"), 5);
        System.out.println(matingLines);
        assertEquals(1, matingLines.size());
        assertEquals("[[G*1c, 2a1c, 2b2a, 3b2a, G*2c]]", matingLines.toString());
    }

    @Ignore
    @Test
    public void tsumeInEleven() {
        TsumeSolver tsumeSolver = new TsumeSolver();

        List<List<ShogiMove>> matingLines = tsumeSolver.allMatingLines(SfenConverter.fromSFEN("5p1nl/4P1pk1/6n1s/3p3P1/5l3/9/9/9/9 b R2BNl"), 11);
        System.out.println(matingLines);
        assertEquals(1, matingLines.size());
        assertEquals("[[G*1c, 2a1c, 2b2a, 3b2a, G*2c]]", matingLines.toString());
    }

}