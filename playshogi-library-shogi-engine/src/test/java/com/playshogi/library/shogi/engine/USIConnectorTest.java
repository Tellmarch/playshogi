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
        String usf = "USF:1.0\n^*:7g7f3c3d";
        usiConnector.analyzeKifu(UsfFormat.INSTANCE.read(usf).getGameTree(), 2000, System.out::println);
        usiConnector.disconnect();
    }
}