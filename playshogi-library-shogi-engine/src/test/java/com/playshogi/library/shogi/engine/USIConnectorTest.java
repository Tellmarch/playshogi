package com.playshogi.library.shogi.engine;

import com.playshogi.library.shogi.models.formats.usf.UsfFormat;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@Ignore
public class USIConnectorTest {

    @Test
    public void analyseTsume() {
        USIConnector usiConnector = new USIConnector(EngineConfiguration.TSUME_ENGINE);
        usiConnector.connect();
        PositionEvaluation eval = usiConnector.analyseTsume("2p6/2+b+B1k3/3PPp3/9/1p7/9/9/9/9 b 2RP4g4s4n4l12p");
        System.out.println(eval);
        assertEquals("6b5a", eval.getBestMove());
        usiConnector.disconnect();
    }

    @Test
    public void analyseTsumeNoMate() {
        USIConnector usiConnector = new USIConnector(EngineConfiguration.TSUME_ENGINE);
        usiConnector.connect();
        PositionEvaluation eval = usiConnector.analyseTsume("2p6/2+b+B1k3/3PPp3/9/1p7/9/9/9/9 b RP4g4s4n4l12p");
        System.out.println(eval);
        assertNull(eval.getBestMove());
        usiConnector.disconnect();
    }

    @Test
    public void analysePosition() {
        USIConnector usiConnector = new USIConnector(EngineConfiguration.NORMAL_ENGINE);
        usiConnector.connect();
        System.out.println(usiConnector.analysePosition("ln1g5/1ks2gs1l/1pp4p1/p2bpn2p/3p3P1/P1P1P1P1P/1P1P1PS2" +
                "/2KGGS1R1/LN6L b RNPbp", 2000));
        usiConnector.disconnect();
    }

    @Test
    public void analyzeKifu() {
        USIConnector usiConnector = new USIConnector(EngineConfiguration.NORMAL_ENGINE);
        usiConnector.connect();
        String usf = "USF:1.0\n^*:7g7f3c3dRSGN";
        usiConnector.analyzeKifu(UsfFormat.INSTANCE.read(usf).getGameTree(), 1000, System.out::println);
        usiConnector.disconnect();
    }

    @Test
    public void analyzeKifu2() {
        USIConnector usiConnector = new USIConnector(EngineConfiguration.NORMAL_ENGINE);
        usiConnector.connect();
        String usf = "    USF:1.0\n" +
                "^*:7g7f3c3d6g6f8c8d7i7h7a6b7h7g5c5d5g5f6a5b1g1f1c1d6i6h8d8e6h5g3a4b2h6h4b5c5i4h5a4b4h3h4b3b3h2h7c7d3i" +
                "3h9c9d9g9f4a4b4g4f6b7c6f6e6c6d6e6d7c6d5g6f8b6bP*6e6d7c7f7e7d7e6f7ep*7d7e8ep*6d6e6d5c6dP" +
                "*7e6d6e7e7d7c6" +
                "d5f5e2b5e7g6f6e6f8h6f5e6f6h6fb*5e6f6ip*6g6i6gs*7f6g6i7f8eB*8d6b8bS*7ep*6cP" +
                "*6e6d7e8d7e8e7d7e8f5e9I6e6d" +
                "l*6e6i7ip*7e6d6C5b6c8f7g9i7g8i7g6e6H7i8ib*6g8i9i8b8GP*6d6c5cS*6c8g8h6c7D8h9i6d6C6g4I6c5c4i3i2h1h" +
                "s*1gRSGN\n";
        usiConnector.analyzeKifu(UsfFormat.INSTANCE.read(usf).getGameTree(), 100, System.out::println);
        usiConnector.disconnect();
    }

}