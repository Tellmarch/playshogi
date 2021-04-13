package com.playshogi.library.shogi.engine.insights;

import com.playshogi.library.shogi.models.formats.usf.UsfFormat;
import com.playshogi.library.shogi.models.record.GameRecord;
import org.junit.Ignore;
import org.junit.Test;

@Ignore
public class GameInsightsAnalyserTest {

    @Test
    public void extractInsights() {
        String usf = "    USF:1.0\n" +
                "^*:7g7f3c3d6g6f8c8d7i7h7a6b7h7g5c5d5g5f6a5b1g1f1c1d6i6h8d8e6h5g3a4b2h6h4b5c5i4h5a4b4h3h4b3b3h2h7c7d3i" +
                "3h9c9d9g9f4a4b4g4f6b7c6f6e6c6d6e6d7c6d5g6f8b6bP*6e6d7c7f7e7d7e6f7ep*7d7e8ep*6d6e6d5c6dP" +
                "*7e6d6e7e7d7c6" +
                "d5f5e2b5e7g6f6e6f8h6f5e6f6h6fb*5e6f6ip*6g6i6gs*7f6g6i7f8eB*8d6b8bS*7ep*6cP" +
                "*6e6d7e8d7e8e7d7e8f5e9I6e6d" +
                "l*6e6i7ip*7e6d6C5b6c8f7g9i7g8i7g6e6H7i8ib*6g8i9i8b8GP*6d6c5cS*6c8g8h6c7D8h9i6d6C6g4I6c5c4i3i2h1h" +
                "s*1gRSGN\n";
        GameRecord gameRecord = UsfFormat.INSTANCE.readSingle(usf);
        GameInsights insights = new GameInsightsAnalyser().extractInsights(gameRecord);

        System.out.println("Black accuracy: " + insights.getBlackAccuracy().getAverageCentipawnsLost());
        for (Mistake mistake : insights.getBlackAccuracy().getMistakes()) {
            System.out.println(mistake);
        }

        System.out.println();

        System.out.println("White accuracy: " + insights.getWhiteAccuracy().getAverageCentipawnsLost());
        for (Mistake mistake : insights.getWhiteAccuracy().getMistakes()) {
            System.out.println(mistake);
        }
    }
}