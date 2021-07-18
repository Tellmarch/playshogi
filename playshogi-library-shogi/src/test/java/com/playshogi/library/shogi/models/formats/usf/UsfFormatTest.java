package com.playshogi.library.shogi.models.formats.usf;

import com.playshogi.library.shogi.models.moves.EditMove;
import com.playshogi.library.shogi.models.record.GameNavigation;
import com.playshogi.library.shogi.models.record.GameRecord;
import com.playshogi.library.shogi.models.record.GameTree;
import com.playshogi.library.shogi.models.shogivariant.Handicap;
import com.playshogi.library.shogi.models.shogivariant.ShogiInitialPositionFactory;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class UsfFormatTest {

    @Test
    public void read() {
        String usfGame = "USF:1.0\n^*lnsgkgsnl/1r5b1/ppppppppp/9/9/9/PPPPPPPPP/1B5R1/LNSGKGSNL b " +
                "-:7g7f3c3d6g6f8c8d2h6h7a6b5i4h5a4b3i3h4b3b4h3i5c5d6i5h8d8e8h7g2b3c7i7h3b2b1g1f1c1" +
                "d3i2h6b5c4g4f1a1b6f6e4c4d7f7e2b1a6h6f3a2b6f7f4a3a7h6g6a5b6g5f5b4c3g3f5c4b2i3g8e8f" +
                "8g8f4d4e6e6d6c6d4f4e3c7G8i7gb*6iB*9f6i9F9g9fb*6iB*7a6i8G5h6g8g7f6g7f8b9b1f1e1d1eP*1c1b1c3g2er*8iB" +
                "*6f4c3c7g8e8i7I5f6gp*4f4i5h2c2d2e1C2a1c5g5fn*1f2h3g7i1i3g4f1f2H3h4gl*6e6f3C4b3cP" +
                "*6f2h2g4e4d1i4i4f4e2g3gG*4fb*1gL*3e3g4g5h4g3d3e4d4Cs*3d4e5d3d4c5d4cl*5aRSGN\n";
        GameRecord gameRecord = UsfFormat.INSTANCE.read(usfGame).get(0);
        Assert.assertNotNull(gameRecord.getGameTree());
    }

    @Test
    public void readWrite() {
        String usfGame = "USF:1.0\n" +
                "^*:7g7f3c3d6g6f8c8d2h6h7a6b5i4h5a4b3i3h4b3b4h3i5c5d6i5h8d8e8h7g2b3c7i7h3b2b1g1f1c1" +
                "d3i2h6b5c4g4f1a1b6f6e4c4d7f7e2b1a6h6f3a2b6f7f4a3a7h6g6a5b6g5f5b4c3g3f5c4b2i3g8e8f" +
                "8g8f4d4e6e6d6c6d4f4e3c7G8i7gb*6iB*9f6i9F9g9fb*6iB*7a6i8G5h6g8g7f6g7f8b9b1f1e1d1eP*1c1b1c3g2er*8iB" +
                "*6f4c3c7g8e8i7I5f6gp*4f4i5h2c2d2e1C2a1c5g5fn*1f2h3g7i1i3g4f1f2H3h4gl*6e6f3C4b3cP" +
                "*6f2h2g4e4d1i4i4f4e2g3gG*4fb*1gL*3e3g4g5h4g3d3e4d4Cs*3d4e5d3d4c5d4cl*5aRSGN\n";
        GameRecord gameRecord = UsfFormat.INSTANCE.read(usfGame).get(0);
        String result = UsfFormat.INSTANCE.write(gameRecord.getGameTree());
        Assert.assertEquals(usfGame, result);
    }

    @Test
    public void readWriteDefaultStart() {
        String usfGame = "USF:1.0\n^*:7g7f3c3d6g6f8c8d2h6h7a6b5i4h5a4b3i3h4b3b4h3i5c5d6i5h8d8e8h7g2b3c7i7h3b2b1g1f1c1" +
                "d3i2h6b5c4g4f1a1b6f6e4c4d7f7e2b1a6h6f3a2b6f7f4a3a7h6g6a5b6g5f5b4c3g3f5c4b2i3g8e8f" +
                "8g8f4d4e6e6d6c6d4f4e3c7G8i7gb*6iB*9f6i9F9g9fb*6iB*7a6i8G5h6g8g7f6g7f8b9b1f1e1d1eP*1c1b1c3g2er*8iB" +
                "*6f4c3c7g8e8i7I5f6gp*4f4i5h2c2d2e1C2a1c5g5fn*1f2h3g7i1i3g4f1f2H3h4gl*6e6f3C4b3cP" +
                "*6f2h2g4e4d1i4i4f4e2g3gG*4fb*1gL*3e3g4g5h4g3d3e4d4Cs*3d4e5d3d4c5d4cl*5aRSGN\n";
        GameRecord gameRecord = UsfFormat.INSTANCE.read(usfGame).get(0);
        String result = UsfFormat.INSTANCE.write(gameRecord.getGameTree());
        Assert.assertEquals(usfGame, result);
    }

    @Test
    public void neverOutputDefaultPosition() {
        String usfGame = "USF:1.0\n" +
                "^*lnsgkgsnl/1r5b1/ppppppppp/9/9/9/PPPPPPPPP/1B5R1/LNSGKGSNL b -:7g7f3c3d\n";

        String expected = "USF:1.0\n" +
                "^*:7g7f3c3d\n";
        GameRecord gameRecord = UsfFormat.INSTANCE.read(usfGame).get(0);
        String result = UsfFormat.INSTANCE.write(gameRecord.getGameTree());
        Assert.assertEquals(expected, result);
    }

    @Test
    public void readTest1() throws IOException {
        String path = "src/test/resources/usf/test1.usf";
        String usfGame = new String(Files.readAllBytes(Paths.get(path)));
        GameRecord gameRecord = UsfFormat.INSTANCE.read(usfGame).get(0);
        Assert.assertNotNull(gameRecord.getGameTree());
        String result = UsfFormat.INSTANCE.write(gameRecord.getGameTree());
        Assert.assertEquals(usfGame, result);
    }

    @Test
    public void readMultiple() {
        String usfGame = "USF:1.0\n" +
                "^*lnsgkgsnl/1r5b1/ppppppppp/9/9/9/PPPPPPPPP/1B5R1/LNSGKGSNL b " +
                "-:7g7f3c3d6g6f8c8d2h6h7a6b5i4h5a4b3i3h4b3b4h3i5c5d6i5h8d8e8h7g2b3c7i7h3b2b1g1f1c1" +
                "d3i2h6b5c4g4f1a1b6f6e4c4d7f7e2b1a6h6f3a2b6f7f4a3a7h6g6a5b6g5f5b4c3g3f5c4b2i3g8e8f" +
                "8g8f4d4e6e6d6c6d4f4e3c7G8i7gb*6iB*9f6i9F9g9fb*6iB*7a6i8G5h6g8g7f6g7f8b9b1f1e1d1eP*1c1b1c3g2er*8iB" +
                "*6f4c3c7g8e8i7I5f6gp*4f4i5h2c2d2e1C2a1c5g5fn*1f2h3g7i1i3g4f1f2H3h4gl*6e6f3C4b3cP" +
                "*6f2h2g4e4d1i4i4f4e2g3gG*4fb*1gL*3e3g4g5h4g3d3e4d4Cs*3d4e5d3d4c5d4cl*5aRSGN\n" +
                "^*lnsgkgsnl/1r5b1/ppppppppp/9/9/9/PPPPPPPPP/1B5R1/LNSGKGSNL b " +
                "-:7g7f3c3d6g6f8c8d2h6h7a6b5i4h5a4b3i3h4b3b4h3i5c5d6i5h8d8e8h7g2b3c7i7h3b2b1g1f1c1" +
                "d3i2h6b5c4g4f1a1b6f6e4c4d7f7e2b1a6h6f3a2b6f7f4a3a7h6g6a5b6g5f5b4c3g3f5c4b2i3g8e8f" +
                "8g8f4d4e6e6d6c6d4f4e3c7G8i7gb*6iB*9f6i9F9g9fb*6iB*7a6i8G5h6g8g7f6g7f8b9b1f1e1d1eP*1c1b1c3g2er*8iB" +
                "*6f4c3c7g8e8i7I5f6gp*4f4i5h2c2d2e1C2a1c5g5fn*1f2h3g7i1i3g4f1f2H3h4gl*6e6f3C4b3cP" +
                "*6f2h2g4e4d1i4i4f4e2g3gG*4fb*1gL*3e3g4g5h4g3d3e4d4Cs*3d4e5d3d4c5d4cl*5aRSGN\n";
        List<GameRecord> gameRecords = UsfFormat.INSTANCE.read(usfGame);
        Assert.assertEquals(2, gameRecords.size());
        GameRecord gameRecord = gameRecords.get(0);
        Assert.assertNotNull(gameRecord.getGameTree());
    }

    @Test
    public void preserveCustomTags() {
        String usfGame = "USF:1.0\n" +
                "^*l5g1l/3sk2+R1/pp1gp1g1p/2p1rp3/3N2P2/1BSP1P3/PP2P1N1P/2+b1K2S1/+p4G2L w S2N2Pl2p:3a2b3g2e\n" +
                "BN:AAA aaa\n" +
                "WN:BBB bbb\n" +
                "GD:19/07/2013\n" +
                "GN:ESC/WOSC 2013\n" +
                ".0\n" +
                "X:PLAYSHOGI:PROBLEMTYPE:WINNING_OR_LOSING\n" +
                "X:PLAYSHOGI:PREVIOUSMOVE:2a2b\n";
        GameRecord gameRecord = UsfFormat.INSTANCE.readSingle(usfGame);
        Assert.assertEquals(usfGame, UsfFormat.INSTANCE.write(gameRecord));
    }

    @Test
    public void writeMultiplePositionChanges() {
        GameTree gameTree = new GameTree(ShogiInitialPositionFactory.createInitialPosition(Handicap.TWO_PIECES));
        GameNavigation gameNavigation = new GameNavigation(gameTree);
        gameNavigation.getCurrentNode().setComment("Before first move");
        gameNavigation.addMove(UsfMoveConverter.fromUsfString("4c4d", gameNavigation.getPosition()));
        gameNavigation.getCurrentNode().setComment("After first move");
        gameNavigation.addMove(new EditMove(ShogiInitialPositionFactory.createInitialPosition(Handicap.FOUR_PIECES)));
        gameNavigation.getCurrentNode().setComment("After position change");
        gameNavigation.addMove(UsfMoveConverter.fromUsfString("3c3d", gameNavigation.getPosition()));
        gameNavigation.getCurrentNode().setComment("After second first move");
        gameNavigation.addMove(UsfMoveConverter.fromUsfString("7g7f", gameNavigation.getPosition()));
        gameNavigation.getCurrentNode().setComment("After last move");

        Assert.assertEquals("USF:1.0\n" +
                        "^*lnsgkgsnl/9/ppppppppp/9/9/9/PPPPPPPPP/1B5R1/LNSGKGSNL w -:4c4dSLNT3c3d7g7f\n" +
                        ".0\n" +
                        "#Before first move\n" +
                        ".\n" +
                        "#After first move\n" +
                        ".\n" +
                        "SFEN:1nsgkgsn1/9/ppppppppp/9/9/9/PPPPPPPPP/1B5R1/LNSGKGSNL w -\n" +
                        "#After position change\n" +
                        ".\n" +
                        "#After second first move\n" +
                        ".\n" +
                        "#After last move\n",
                UsfFormat.INSTANCE.write(gameTree));
    }

    @Test
    public void readWriteMultiplePositionChanges() {
        String usf = "USF:1.0\n" +
                "^*lnsgkgsnl/9/ppppppppp/9/9/9/PPPPPPPPP/1B5R1/LNSGKGSNL w -:4c4dSLNT3c3d7g7f\n" +
                ".0\n" +
                "#Before first move\n" +
                ".\n" +
                "#After first move\n" +
                ".\n" +
                "SFEN:1nsgkgsn1/9/ppppppppp/9/9/9/PPPPPPPPP/1B5R1/LNSGKGSNL w -\n" +
                "#After position change\n" +
                ".\n" +
                "#After second first move\n" +
                ".\n" +
                "#After last move\n";
        GameRecord gameRecord = UsfFormat.INSTANCE.readSingle(usf);
        Assert.assertEquals(usf, UsfFormat.INSTANCE.write(gameRecord));
    }
}
