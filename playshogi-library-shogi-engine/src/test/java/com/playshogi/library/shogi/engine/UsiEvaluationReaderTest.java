package com.playshogi.library.shogi.engine;

import com.playshogi.library.shogi.models.formats.sfen.StringLineReader;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class UsiEvaluationReaderTest {

    @Test
    public void readEvaluationMultiPV3() {
        String sfen = "lnsgkgsnl/1r5b1/ppppppppp/9/9/9/PPPPPPPPP/1B5R1/LNSGKGSNL b - 0";
        String eval = "info depth 1 seldepth 1 score cp 51 multipv 1 nodes 344 nps 172000 time 2 pv 6i7h\n" +
                "info depth 1 seldepth 1 score cp 51 multipv 2 nodes 344 nps 172000 time 2 pv 7g7f\n" +
                "info depth 1 seldepth 1 score cp 51 multipv 3 nodes 344 nps 172000 time 2 pv 2g2f\n" +
                "info depth 2 seldepth 2 score cp 64 multipv 1 nodes 1212 nps 606000 time 2 pv 7g7f 8c8d\n" +
                "info depth 2 seldepth 2 score cp 51 multipv 2 nodes 1212 nps 606000 time 2 pv 6i7h 4a3b\n" +
                "info depth 2 seldepth 2 score cp 47 multipv 3 nodes 1212 nps 606000 time 2 pv 2g2f 8c8d\n" +
                "info depth 24 seldepth 27 score cp 62 multipv 1 nodes 807316 nps 7914862 time 102 pv 6i7h 8c8d 7g7f " +
                "4a3b 2g2f 8d8e 8h7g 3c3d 7i6h 2b7g+ 6h7g 3a4b 2f2e 4b3c 3g3f 7c7d 3i3h 7a6b 4g4f 8a7c 2i3g 7c6e 7g6f" +
                " 8e8f 8g8f 8b8f\n" +
                "info depth 23 seldepth 29 score cp 54 multipv 2 nodes 807316 nps 7914862 time 102 pv 7g7f 8c8d 2g2f " +
                "8d8e 2f2e 4a3b 8h7g 3c3d 7i6h 3a4b 2e2d 2c2d 2h2d 4b3c 2d2g P*2c 3g3f 7a6b 6i7h 7c7d 1g1f 6b7c 6g6f " +
                "5a4a 6h6g 7c6d 3i4h 5c5d\n" +
                "info depth 23 seldepth 29 score cp 54 multipv 3 nodes 807316 nps 7914862 time 102 pv 2g2f 8c8d 7g7f " +
                "8d8e 2f2e 4a3b 8h7g 3c3d 7i6h 3a4b 2e2d 2c2d 2h2d 4b3c 2d2g P*2c 3g3f 7a6b 6i7h 7c7d 1g1f 6b7c 6g6f " +
                "5a4a 6h6g 7c6d 3i4h 5c5d 4g4f\n" +
                "bestmove 6i7h ponder 8c8d\n";
        PositionEvaluation evaluation = UsiEvaluationReader.readEvaluation(new StringLineReader(eval), sfen, 3);
        assertEquals("6i7h 8c8d 7g7f 4a3b 2g2f 8d8e 8h7g 3c3d 7i6h 2b7G 6h7g 3a4b 2f2e 4b3c 3g3f 7c7d 3i3h 7a6b 4g4f " +
                "8a7c 2i3g 7c6e 7g6f 8e8f 8g8f 8b8f ", evaluation.getMainVariation().getUsf());
        assertEquals(3, evaluation.getVariationsHistory().size());
        assertEquals(3, evaluation.getVariationsHistory().get(0).getVariations().size());
        assertEquals(3, evaluation.getVariationsHistory().get(1).getVariations().size());
        assertEquals(3, evaluation.getVariationsHistory().get(2).getVariations().size());
        assertEquals("6i7h", evaluation.getBestMove());

    }

    @Test
    public void parsePrincipalVariation() {
        String sfen = "lnsgkgsnl/1r5b1/ppppppppp/9/9/9/PPPPPPPPP/1B5R1/LNSGKGSNL b - 0";
        String info = "info depth 26 seldepth 32 score cp 66 multipv 1 nodes 32090761 nps 5018886 hashfull 1000 time " +
                "6394 pv 7g7f 8c8d 8h7g 3c3d 7i8h 4a3b 2g2f 2b7g+ 8h7g 3a2b 3g3f 2b3c 3i4h 7c7d 2f2e 7a6b 4h3g 6b7c " +
                "3g4f 8d8e 1g1f 1c1d 6g6f 7c6d 6i7h 7d7e 7f7e 6d7e P*7d";
        Variation variation = UsiEvaluationReader.parsePrincipalVariation(info, sfen);
        assertEquals(variation.toString(), "Variation{forcedMate=false, numMovesBeforeMate=0, evaluationCP=66, " +
                "depth=26, seldepth=32, nodes=32090761, usf='7g7f 8c8d 8h7g 3c3d 7i8h 4a3b 2g2f 2b7G " +
                "8h7g 3a2b 3g3f 2b3c 3i4h 7c7d 2f2e 7a6b 4h3g 6b7c 3g4f 8d8e 1g1f 1c1d 6g6f 7c6d 6i7h 7d7e 7f7e 6d7e " +
                "P*7d ', timeMs=6394}");

    }
}